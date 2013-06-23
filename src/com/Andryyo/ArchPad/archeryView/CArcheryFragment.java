package com.Andryyo.ArchPad.archeryView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;
import com.Andryyo.ArchPad.sight.ActivitySightProperties;
import com.Andryyo.ArchPad.statistics.IOnUpdateListener;
import com.Andryyo.ArchPad.target.CEditableTargetView;

public class CArcheryFragment extends Fragment implements IOnShotAddListener{
    public static Vibrator vibrator;
    public static final String NUMBER_OF_ROUNDS = "com.Andryyo.ArchPad.numberOfDistances";
    public static final String ARROWS_IN_ROUND = "com.Andryyo.ArchPad.numberOfArrows";
    public static final String TARGET_ID = "com.Andryyo.ArchPad.targetId";
    public static final String ARROW_ID = "com.Andryyo.ArchPad.arrowId";

    public int numberOfEnds;
    public int arrowsInDistance;
    private CDistance currentDistance = null;
    private CDistance previousDistance = null;
    private long targetId;
    private long arrowId;
    private Context context;
    private IOnUpdateListener onUpdateListener;
    private View.OnTouchListener onTouchListener;
    private CEditableTargetView mTargetView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    public CArcheryFragment() {};

    public CArcheryFragment(Intent data)    {
        numberOfEnds = data.getIntExtra(NUMBER_OF_ROUNDS, 0);
        arrowsInDistance =  data.getIntExtra(ARROWS_IN_ROUND, 0);
        targetId = data.getLongExtra(TARGET_ID, 0);
        arrowId = data.getLongExtra(ARROW_ID, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        if (savedInstanceState!=null)
        {
            numberOfEnds = savedInstanceState.getInt(NUMBER_OF_ROUNDS);
            arrowsInDistance =  savedInstanceState.getInt(ARROWS_IN_ROUND);
            targetId = savedInstanceState.getLong(TARGET_ID);
            arrowId = savedInstanceState.getLong(ARROW_ID);
        }
        setHasOptionsMenu(true);
        LinearLayout parentLayout = new LinearLayout(context);
        parentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.9f));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        mTargetView = new CEditableTargetView(context);
        mTargetView.setOnShotAddListener(this);
        mTargetView.setOnTouchListener(onTouchListener);
        mTargetView.setTarget(targetId);
        mTargetView.setArrow(arrowId);
        if (savedInstanceState!=null)
            restoreDistance(savedInstanceState);
        mTargetView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 15f));
        mTargetView.setTag("targetView");
        layout.addView(mTargetView);
        СEndsCounterView mEndsCounterView = new СEndsCounterView(context,this);
        mEndsCounterView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT,1f));
        layout.addView(mEndsCounterView);
        parentLayout.addView(layout);
        CCurrentEndView mCurrentEndsView = new CCurrentEndView(context,this);
        mCurrentEndsView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.1f));
        parentLayout.addView(mCurrentEndsView);
        return parentLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)    {
        super.onSaveInstanceState(outState);
        if (currentDistance!=null)
            outState.putSerializable("savedDistance", currentDistance);
        outState.putInt(NUMBER_OF_ROUNDS, numberOfEnds);
        outState.putInt(ARROWS_IN_ROUND, arrowsInDistance);
        outState.putLong(TARGET_ID, targetId);
        outState.putLong(ARROW_ID, arrowId);

    }

    private void restoreDistance(Bundle savedInstanceState) {
        currentDistance = (CDistance) savedInstanceState.getSerializable("savedDistance");
        mTargetView.setDistance(currentDistance);
    }

    public void setOnUpdateListener(IOnUpdateListener listener)  {
        onUpdateListener = listener;
    }

    public void setOnTouchListener(View.OnTouchListener listener)   {
        onTouchListener = listener;
        if (mTargetView!=null)
            mTargetView.setOnTouchListener(listener);
    }

    public void saveCurrentDistance()  {
        if (currentDistance!=null)
        {
        if (!currentDistance.isEmpty())
        {
            CSQLiteOpenHelper helper = CSQLiteOpenHelper.getHelper(context);
            try
            {
                helper.addDistance(currentDistance);
            }
            catch (Exception e)
            {
                Toast toast = Toast.makeText(context, e.getMessage(), 3000);
                toast.show();
            }
            onUpdateListener.update();
        }
        }
    }

	public void deleteLastShot()    {
        if (currentDistance!=null)
		    currentDistance.deleteLastShot();
	}

    @Override
    public void addShot(CShot shot) {
        if (currentDistance == null)
        {
            currentDistance = new CDistance(numberOfEnds, arrowsInDistance,targetId,arrowId);
            currentDistance.addShot(shot);
            mTargetView.setDistance(currentDistance);
        }
        else
        if (currentDistance.addShot(shot) == 0)
        {
            saveCurrentDistance();
            previousDistance = currentDistance;
            currentDistance = null;
            mTargetView.setDistance(previousDistance);
        }
        getView().invalidate();
    }

    public CDistance getCurrentDistance()   {
        if ((currentDistance==null)&&(previousDistance==null))   {
            return new CDistance(numberOfEnds, arrowsInDistance,targetId,arrowId);
        }   if (currentDistance==null)
            return previousDistance;
        if ((currentDistance.isEmpty()&&(previousDistance!=null)))
            return previousDistance;
        else
            return currentDistance;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.info:
            {
                new AlertDialog.Builder(getActivity())
                .setTitle("Information" )
                .setMessage("Created by Andryyo.\r\nEmail : Andryyo@ukr.net\r\n2012")
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                })
                .create().show();
                break;
            }
            case R.id.sight:
            {
                Intent intent = new Intent(getActivity(), ActivitySightProperties.class);
                startActivity(intent);
                break;
            }
        }
        return true;
    }

    @Override
    public void onPause()   {
        super.onPause();
        vibrator.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ((currentDistance!=null)&&(!currentDistance.isEmpty()))
            saveCurrentDistance();
    }

    @Override
    public void onResume()  {
        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        super.onResume();
    }

}

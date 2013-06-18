package com.Andryyo.ArchPad.archeryView;

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

	public int numberOfSeries;
    public int arrowsInSeries;
    private CDistance currentDistance = null;
    private CDistance previousDistance = null;
    private long targetId;
    private long arrowId;
    private Context context;
    private IOnUpdateListener listener;
    private CEditableTargetView mTargetView;
    
	public CArcheryFragment(Context context, int numberOfSeries, int arrowsInSeries, long targetId, long arrowId) {
		this.numberOfSeries = numberOfSeries;
		this.arrowsInSeries = arrowsInSeries;
        this.targetId = targetId;
        this.arrowId = arrowId;
        this.context = context;
        setHasOptionsMenu(true);
    	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState!=null)
            restoreDistance(savedInstanceState);
    }

    private void restoreDistance(Bundle savedInstanceState) {
        currentDistance = (CDistance) savedInstanceState.getSerializable("savedDistance");
        mTargetView.setDistance(currentDistance);
    }

    public void setOnUpdateListener(IOnUpdateListener listener)  {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        LinearLayout parentLayout = new LinearLayout(context);
        parentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0,0.9f));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        mTargetView = new CEditableTargetView(getActivity());
        mTargetView.setOnShotAddListener(this);
        mTargetView.setTarget(targetId);
        mTargetView.setArrow(arrowId);
        mTargetView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT,15f));
        layout.addView(mTargetView);
        СSeriesCounterView mSeriesCounterView = new СSeriesCounterView(context,this);
        mSeriesCounterView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT,1f));
        layout.addView(mSeriesCounterView);
        parentLayout.addView(layout);
        CCurrentSeriesView mCurrentSeriesView = new CCurrentSeriesView(context,this);
        mCurrentSeriesView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.1f));
        parentLayout.addView(mCurrentSeriesView);
        return parentLayout;
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
            listener.update();
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
            currentDistance = new CDistance(numberOfSeries, arrowsInSeries,targetId,arrowId);
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
        if ((currentDistance==null)&&(previousDistance==null))
            return new CDistance(numberOfSeries,arrowsInSeries,targetId,arrowId);
        if (currentDistance==null)
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
            case R.id.save:
            {
                //endCurrentDistance();
                saveCurrentDistance();
                getActivity().finish();
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
    public void onSaveInstanceState(Bundle savedInstanceState)  {
        super.onSaveInstanceState(savedInstanceState);
        if (currentDistance!=null)
            savedInstanceState.putSerializable("savedDistance", currentDistance);
    }

    @Override
    public void onResume()  {
        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        super.onResume();
    }

}

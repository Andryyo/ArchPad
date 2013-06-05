package com.Andryyo.ArchPad.archeryView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.NotesActivity;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;
import com.Andryyo.ArchPad.sight.CSightPropertiesActivity;
import com.Andryyo.ArchPad.statistics.StatisticsActivity;
import com.Andryyo.ArchPad.target.CTargetView;

public class CArcheryView extends Fragment {
    public static final int SIGHT_REQUEST = 1;
    public static Vibrator vibrator;

	public int numberOfSeries;
    public int arrowsInSeries;
    private CDistance currentDistance = null;
    private CDistance previousDistance = null;
    private long targetId;
    private long arrowId;
    private Context context;
    
	public CArcheryView(Context context, int numberOfSeries, int arrowsInSeries, long targetId, long arrowId) {
		this.numberOfSeries = numberOfSeries;
		this.arrowsInSeries = arrowsInSeries;
        this.targetId = targetId;
        this.arrowId = arrowId;
        this.context = context;
        setHasOptionsMenu(true);
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
        CTargetView mTargetView = new CTargetView(getActivity(),this);
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
        }
        }
    }

    public void loadDistances()  {
        CSQLiteOpenHelper helper = CSQLiteOpenHelper.getHelper(context);
        try
        {
            currentDistance = helper.getUnfinishedDistance();
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(context, e.getMessage(), 3000);
            toast.show();
        }
        }

	public void deleteLastShot()    {
        if (currentDistance!=null)
		    currentDistance.deleteLastShot();
	}

    public void endCurrentDistance() {
        if (currentDistance!=null)
        {
            if (!currentDistance.isEmpty())
            {
                CSQLiteOpenHelper helper = CSQLiteOpenHelper.getHelper(context);
                helper.delete(CSQLiteOpenHelper.TABLE_DISTANCES, currentDistance.getId());
                currentDistance.isFinished = true;
            }
            else
                currentDistance = null;
        }
    }

    public void addShot(CShot shot) {
        if (currentDistance == null)
        {
            currentDistance = new CDistance(numberOfSeries, arrowsInSeries,targetId,arrowId);
            currentDistance.addShot(shot);
        }
        else
        if (currentDistance.addShot(shot) == 0)
        {
            endCurrentDistance();
            saveCurrentDistance();
            previousDistance = currentDistance;
            currentDistance = null;
        }
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
            case R.id.show_statistics:
            {
                Intent intent = new Intent(context, StatisticsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.info:
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Information" );
                dialog.setMessage("Created by Andryyo.\r\nEmail : Andryyo@ukr.net\r\n2012");
                dialog.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });
                dialog.show();
                break;
            }
            case R.id.save:
            {
                endCurrentDistance();
                getActivity().finish();
                break;
            }
            case R.id.sight:
            {
                Intent intent = new Intent(context, CSightPropertiesActivity.class);
                startActivityForResult(intent, SIGHT_REQUEST);
                break;
            }
            case R.id.notes:
            {
                Intent intent = new Intent(context, NotesActivity.class);
                startActivity(intent);
                break;
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if (requestCode==SIGHT_REQUEST)
            if (resultCode!= Activity.RESULT_CANCELED)
            {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putLong("sightId",data.getLongExtra("sightId",0));
                editor.commit();
            }
    }

    @Override
    public void onPause()   {
        super.onPause();
        saveCurrentDistance();
        vibrator.cancel();
    }

    @Override
    public void onResume()  {
        loadDistances();
        //postInvalidate();
        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        super.onResume();
    }

}

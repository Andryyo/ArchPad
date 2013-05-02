package com.example.archery.archeryView;

import java.io.*;
import android.content.Context;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.archery.CArrow;
import com.example.archery.CShot;
import com.example.archery.database.CMySQLiteOpenHelper;
import com.example.archery.target.CTargetView;

public  class CArcheryView extends LinearLayout {
	public Paint pen;
	public int numberOfSeries;
    public int arrowsInSeries;
    private Context context;
    private CDistance currentDistance = null;
    private CDistance previousDistance = null;
    private long targetId;
    private long arrowId;
    
	public CArcheryView(Context context,int numberOfSeries, int arrowsInSeries, long targetId, long arrowId) {
		super(context);
        this.context = context;
		this.pen = new Paint();
		this.numberOfSeries = numberOfSeries;
		this.arrowsInSeries = arrowsInSeries;
        this.targetId = targetId;
        this.arrowId = arrowId;

        this.setOrientation(VERTICAL);
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0,0.9f));
        layout.setOrientation(HORIZONTAL);
        CTargetView mTargetView = new CTargetView(context,this);
        mTargetView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT,15f));
        layout.addView(mTargetView);
        СSeriesCounterView mSeriesCounterView = new СSeriesCounterView(context,this);
        mSeriesCounterView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT,1f));
        layout.addView(mSeriesCounterView);
        this.addView(layout);
        CCurrentSeriesView mCurrentSeriesView = new CCurrentSeriesView(context,this);
        mCurrentSeriesView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.1f));
        this.addView(mCurrentSeriesView);
    	}

    public void saveDistances()  {
        if (currentDistance!=null)
        {
        if (!currentDistance.isEmpty())
        {
            CMySQLiteOpenHelper helper = CMySQLiteOpenHelper.getHelper(context);
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
        CMySQLiteOpenHelper helper = CMySQLiteOpenHelper.getHelper(context);
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
                CMySQLiteOpenHelper helper = CMySQLiteOpenHelper.getHelper(context);
                helper.deleteDistance(currentDistance);
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
            saveDistances();
            previousDistance = currentDistance;
            currentDistance = null;
        }
    }
	
    public CArrow getCurrentArrow() {
        String arrowName = PreferenceManager.getDefaultSharedPreferences(context).getString("arrowName","defaultArrow");
        try
        {
            ObjectInputStream oos =
                    new ObjectInputStream(context.openFileInput("Arrows"));
            CArrow arrows[] = (CArrow[]) oos.readObject();
            for (CArrow arrow : arrows)
                if (arrow.name.equals(arrowName))
                    return arrow;
            oos.close();
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(context, e.getMessage(), 3000);
            toast.show();
        }
        return null;
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
}

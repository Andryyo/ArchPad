package com.example.archery.archeryView;

import java.io.*;
import java.util.Vector;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.archery.target.CRing;
import com.example.archery.target.CTarget;
import com.example.archery.target.CTargetView;

public  class CArcheryView extends LinearLayout {
	public Paint pen;
	public int numberOfSeries;
    public int arrowsInSeries;
    private Context context;
    public CDistance distance;
    private Vector<CDistance> distances = null;
    
	public CArcheryView(Context context,int a, int b) {
		super(context);
        this.context = context;
		pen = new Paint();
		numberOfSeries =a;
		arrowsInSeries =b;
        /*
        try
        {
            ObjectOutputStream oos =
                    new ObjectOutputStream(context.openFileOutput("Targets",Context.MODE_PRIVATE));
            Vector<CRing> rings = new Vector<CRing>();
            rings.add(new CRing(10,1,Color.YELLOW));
            rings.add(new CRing(9,2,Color.YELLOW));
            rings.add(new CRing(8,3,Color.RED));
            rings.add(new CRing(7,4,Color.RED));
            rings.add(new CRing(6,5,Color.BLUE));
            CTarget target = new CTarget("default_target",rings);
            CTarget targets [] = {target};
            oos.writeObject(targets);
            oos.close();
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(context, e.getMessage(), 3000);
            toast.show();
        }        */
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
        try
        {
            ObjectOutputStream oos =
                    new ObjectOutputStream(context.openFileOutput("Archery",Context.MODE_PRIVATE));
            oos.writeObject(distances);
            oos.close();
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(context, e.getMessage(), 3000);
            toast.show();
        }
    }

    public void loadDistances()  {
        try
        {
            ObjectInputStream oos =
                    new ObjectInputStream(context.openFileInput("Archery"));
            distances = (Vector<CDistance>) oos.readObject();
            oos.close();
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(context, e.getMessage(), 3000);
            toast.show();
        }
        if (distances.isEmpty())
        {
            distances = new Vector<CDistance>();
            distance = new CDistance(numberOfSeries, arrowsInSeries);
            distances.add(distance);
        }
        else
        if (!distances.lastElement().isFinished)
            distance = distances.lastElement();
        else
        {
            distance = new CDistance(numberOfSeries, arrowsInSeries);
            distances.add(distance);
        }
    }

	public void deleteLastShot()
	{
		distance.deleteLastShot();
	}

    public void endCurrentDistance() {
        distance.isFinished = true;
        distance = new CDistance(numberOfSeries, arrowsInSeries);
        distances.add(distance);
    }

    public void addShot(CShot shot) {
        if (distance.addShot(shot) == 0)
        {
            distance = new CDistance(numberOfSeries, arrowsInSeries);
            distances.add(distance);
        }
    }
	
	private static void Sort(int[] m) {
        int t;
        int i, j;
        for ( i=0; i < m.length; i++) {
          t = m[i];
          for ( j=i-1; j>=0 && m[j] > t; j--)
              m[j+1] = m[j];
          m[j+1] = t;
        }
      }
}

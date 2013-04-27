package com.example.archery.archeryView;

import com.example.archery.CArrow;
import com.example.archery.CShot;

import java.io.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 19.04.13
 * Time: 23:03
 * To change this template use File | Settings | File Templates.
 */
public class CDistance implements Serializable{
    public Calendar timemark;
    public boolean isFinished;
    int numberOfArrows;
    int numberOfSeries;
    public CArrow arrow;
    public Vector<CShot[]> finishedSeries;
    public Vector<CShot> currentSeries;

    public CDistance(int numberOfSeries, int numberOfArrows, CArrow arrow)  {
        timemark = Calendar.getInstance();
        this.numberOfSeries = numberOfSeries;
        this.numberOfArrows = numberOfArrows;
        this.arrow = arrow;
        isFinished = false;
        finishedSeries = new Vector<CShot[]>();
        currentSeries = new Vector<CShot>();
    }

    public int addShot(CShot shot)   {
        currentSeries.add(shot);
        if (currentSeries.size() == numberOfArrows)
        {
            finishedSeries.add(currentSeries.toArray(new CShot[0]));
            currentSeries = new Vector<CShot>();
        }
        if (finishedSeries.size() ==  numberOfSeries)
        {
            currentSeries.clear();
            isFinished = true;
            return 0;
        }
        else
            return 1;
    }

    public void deleteLastShot()    {
        if (currentSeries.size()>0)
        {
            currentSeries.remove(currentSeries.lastElement());
        }
        else
            if (finishedSeries.size()>0)
            {
                currentSeries = new Vector<CShot>(Arrays.asList(finishedSeries.lastElement()));
                currentSeries.remove(currentSeries.lastElement());
                finishedSeries.remove(finishedSeries.lastElement());
            }
    }

    public boolean isEmpty()    {
        if (finishedSeries.isEmpty()&&currentSeries.isEmpty())
            return true;
        else
            return false;
    }

}

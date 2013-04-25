package com.example.archery.archeryView;

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
    boolean isFinished;
    int numberOfArrows;
    int numberOfSeries;
    public Vector<CShot[]> finishedSeries;
    Vector<CShot> currentSeries;

    public CDistance(int numberOfSeries, int numberOfArrows)    {
        timemark = Calendar.getInstance();
        this.numberOfSeries = numberOfSeries;
        this.numberOfArrows = numberOfArrows;
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
            currentSeries = null;
            isFinished = true;
            return 0;
        }
        else
            return 1;
    }

    public void deleteLastShot()
    {
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

}

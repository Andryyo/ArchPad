package com.Andryyo.ArchPad.archeryView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

import java.io.*;
import java.util.Calendar;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 19.04.13
 * Time: 23:03
 * To change this template use File | Settings | File Templates.
 */
public class CRound implements Serializable{
    long _id;
    public Calendar timemark;
    int numberOfArrows;
    int numberOfSeries;
    public long targetId;
    public Vector<Vector<CShot>> series;
    public long arrowId;

    public CRound(int numberOfSeries, int numberOfArrows, long targetId, long arrowId)  {
        timemark = Calendar.getInstance();
        this.numberOfSeries = numberOfSeries;
        this.numberOfArrows = numberOfArrows;
        this.targetId = targetId;
        this.arrowId = arrowId;
        series = new Vector<Vector<CShot>>();
        series.add(new Vector<CShot>());
    }

    public CRound(Cursor cursor)
    {
        //"series" "numberOfSeries" "numberOfArrows" "isFinished" "timemark"
        try
        {
            series = (Vector<Vector<CShot>>) CSQLiteOpenHelper.setObjectBytes(cursor.getBlob(cursor.getColumnIndex("series")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        timemark = Calendar.getInstance();
        timemark.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("timemark")));
        _id = cursor.getLong(cursor.getColumnIndex("_id"));
        numberOfSeries = cursor.getInt(cursor.getColumnIndex("numberOfSeries"));
        numberOfArrows = cursor.getInt(cursor.getColumnIndex("numberOfArrows"));
        targetId = cursor.getLong(cursor.getColumnIndex("targetId"));
        arrowId = cursor.getLong(cursor.getColumnIndex("arrowId"));
    }

    public int addShot(CShot shot)   {
        series.lastElement().add(shot);
        if (series.lastElement().size() == numberOfArrows)
        {
            series.add(new Vector<CShot>());
        }
        if (series.size() ==  numberOfSeries+1)
        {
            series.remove(series.lastElement());
            return 0;
        }
        else
            return 1;
    }

    public void deleteLastShot()    {
        if (series.lastElement().size()>0)
        {
            series.lastElement().remove(series.lastElement().lastElement());
        }
        else
        if (series.size()>1)
            {
                series.remove(series.lastElement());
                series.lastElement().remove(series.lastElement().lastElement());
            }
    }

    public boolean isEmpty()    {
        if (series.isEmpty())
            return true;
        else
        if ((series.size()==1)&&(series.lastElement().isEmpty()))
            return true;
        else
        return false;
    }

    public void writeToDatabase(SQLiteDatabase database)
    {
        try
        {
        ContentValues values = new ContentValues();
        values.put("series", CSQLiteOpenHelper.getObjectBytes(series));
        values.put("numberOfSeries",numberOfSeries);
        values.put("numberOfArrows",numberOfArrows);
        values.put("timemark",timemark.getTimeInMillis());
        values.put("targetId",targetId);
        values.put("arrowId",arrowId);
        database.insert(CSQLiteOpenHelper.TABLE_ROUNDS,null,values);
        }
        catch (Exception e)
        {
        }
    }

    public long getId()  {
        return _id;
    }


}

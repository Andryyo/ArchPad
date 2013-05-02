package com.example.archery.archeryView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import com.example.archery.CArrow;
import com.example.archery.CShot;
import com.example.archery.database.CMySQLiteOpenHelper;

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
    long _id;
    public Calendar timemark;
    public boolean isFinished;
    int numberOfArrows;
    int numberOfSeries;
    public long targetId;
    public Vector<Vector<CShot>> series;
    public long arrowId;

    public CDistance(int numberOfSeries, int numberOfArrows, long targetId, long arrowId)  {
        timemark = Calendar.getInstance();
        this.numberOfSeries = numberOfSeries;
        this.numberOfArrows = numberOfArrows;
        this.targetId = targetId;
        this.arrowId = arrowId;
        isFinished = false;
        series = new Vector<Vector<CShot>>();
        series.add(new Vector<CShot>());
    }

    public CDistance(Cursor cursor)
    {
        //"series" "numberOfSeries" "numberOfArrows" "isFinished" "timemark"
        try
        {
            series = (Vector<Vector<CShot>>) CMySQLiteOpenHelper.setObjectBytes(cursor.getBlob(cursor.getColumnIndex("series")));
            timemark = (Calendar) CMySQLiteOpenHelper.setObjectBytes(cursor.getBlob(cursor.getColumnIndex("timemark")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        _id = cursor.getLong(cursor.getColumnIndex("_id"));
        numberOfSeries = cursor.getInt(cursor.getColumnIndex("numberOfSeries"));
        numberOfArrows = cursor.getInt(cursor.getColumnIndex("numberOfArrows"));
        isFinished = (cursor.getInt(cursor.getColumnIndex("isFinished"))!=0);
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
            isFinished = true;
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
        values.put("series", CMySQLiteOpenHelper.getObjectBytes(series));
        values.put("numberOfSeries",numberOfSeries);
        values.put("numberOfArrows",numberOfArrows);
        values.put("isFinished",isFinished);
        values.put("timemark",CMySQLiteOpenHelper.getObjectBytes(timemark));
        values.put("targetId",targetId);
        values.put("arrowId",arrowId);
        database.insert("distances",null,values);
        }
        catch (Exception e)
        {
        }
    }

    public long getId()  {
        return _id;
    }


}

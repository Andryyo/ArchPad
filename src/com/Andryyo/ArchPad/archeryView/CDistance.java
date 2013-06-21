package com.Andryyo.ArchPad.archeryView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;
import com.Andryyo.ArchPad.target.CTarget;

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
public class CDistance implements Serializable{
    long _id;
    public Calendar timemark;
    int numberOfArrows;
    int numberOfSeries;
    public long targetId;
    public Vector<Vector<CShot>> rounds;
    public long arrowId;

    public CDistance(int numberOfSeries, int numberOfArrows, long targetId, long arrowId)  {
        timemark = Calendar.getInstance();
        this.numberOfSeries = numberOfSeries;
        this.numberOfArrows = numberOfArrows;
        this.targetId = targetId;
        this.arrowId = arrowId;
        rounds = new Vector<Vector<CShot>>();
        rounds.add(new Vector<CShot>());
    }

    public CDistance(Cursor cursor)
    {
        //"rounds" "numberOfSeries" "numberOfArrows" "isFinished" "timemark"
        try
        {
            rounds = (Vector<Vector<CShot>>) CSQLiteOpenHelper.setObjectBytes(cursor.getBlob(cursor.getColumnIndex("rounds")));
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
        rounds.lastElement().add(shot);
        if (rounds.lastElement().size() == numberOfArrows)
        {
            rounds.add(new Vector<CShot>());
        }
        if (rounds.size() ==  numberOfSeries+1)
        {
            rounds.remove(rounds.lastElement());
            return 0;
        }
        else
            return 1;
    }

    public void deleteLastShot()    {
        if (rounds.lastElement().size()>0)
        {
            rounds.lastElement().remove(rounds.lastElement().lastElement());
        }
        else
        if (rounds.size()>1)
            {
                rounds.remove(rounds.lastElement());
                rounds.lastElement().remove(rounds.lastElement().lastElement());
            }
    }

    public boolean isEmpty()    {
        if (rounds.isEmpty())
            return true;
        else
        if ((rounds.size()==1)&&(rounds.lastElement().isEmpty()))
            return true;
        else
        return false;
    }

    public void writeToDatabase(SQLiteDatabase database)
    {
        try
        {
        ContentValues values = new ContentValues();
        values.put("rounds", CSQLiteOpenHelper.getObjectBytes(rounds));
        values.put("numberOfSeries",numberOfSeries);
        values.put("numberOfArrows",numberOfArrows);
        values.put("timemark",timemark.getTimeInMillis());
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

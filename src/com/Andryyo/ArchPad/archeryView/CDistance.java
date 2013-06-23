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
public class CDistance implements Serializable{
    long _id;
    public Calendar timemark;
    int numberOfArrows;
    int numberOfEnds;
    public long targetId;
    public Vector<Vector<CShot>> ends;
    public long arrowId;

    public CDistance(int numberOfEnds, int numberOfArrows, long targetId, long arrowId)  {
        timemark = Calendar.getInstance();
        this.numberOfEnds = numberOfEnds;
        this.numberOfArrows = numberOfArrows;
        this.targetId = targetId;
        this.arrowId = arrowId;
        ends = new Vector<Vector<CShot>>();
        ends.add(new Vector<CShot>());
    }

    public CDistance(Cursor cursor)
    {
        //"ends" "numberOfEnds" "numberOfArrows" "isFinished" "timemark"
        try
        {
            ends = (Vector<Vector<CShot>>) CSQLiteOpenHelper.setObjectBytes(cursor.getBlob(cursor.getColumnIndex("ends")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        timemark = Calendar.getInstance();
        timemark.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("timemark")));
        _id = cursor.getLong(cursor.getColumnIndex("_id"));
        numberOfEnds = cursor.getInt(cursor.getColumnIndex("numberOfEnds"));
        numberOfArrows = cursor.getInt(cursor.getColumnIndex("numberOfArrows"));
        targetId = cursor.getLong(cursor.getColumnIndex("targetId"));
        arrowId = cursor.getLong(cursor.getColumnIndex("arrowId"));
    }

    public int addShot(CShot shot)   {
        ends.lastElement().add(shot);
        if (ends.lastElement().size() == numberOfArrows)
        {
            ends.add(new Vector<CShot>());
        }
        if (ends.size() ==  numberOfEnds+1)
        {
            ends.remove(ends.lastElement());
            return 0;
        }
        else
            return 1;
    }

    public void deleteLastShot()    {
        if (ends.lastElement().size()>0)
        {
            ends.lastElement().remove(ends.lastElement().lastElement());
        }
        else
        if (ends.size()>1)
            {
                ends.remove(ends.lastElement());
                ends.lastElement().remove(ends.lastElement().lastElement());
            }
    }

    public boolean isEmpty()    {
        if (ends.isEmpty())
            return true;
        else
        if ((ends.size()==1)&&(ends.lastElement().isEmpty()))
            return true;
        else
        return false;
    }

    public void writeToDatabase(SQLiteDatabase database)
    {
        try
        {
        ContentValues values = new ContentValues();
        values.put("ends", CSQLiteOpenHelper.getObjectBytes(ends));
        values.put("numberOfEnds",numberOfEnds);
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

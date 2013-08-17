package com.Andryyo.ArchPad.archeryFragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

import java.io.*;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 19.04.13
 * Time: 23:03
 * To change this template use File | Settings | File Templates.
 */
public class CDistance implements Serializable{
    private long _id;
    public int arrowsInEnd;
    public int numberOfEnds;
    public long targetId;
    public Vector<Vector<CShot>> ends;
    private long roundId;
    public long arrowId;


    public CDistance(int numberOfEnds, int arrowsInEnd, long targetId, long arrowId, long roundId)  {
        this.numberOfEnds = numberOfEnds;
        this.arrowsInEnd = arrowsInEnd;
        this.targetId = targetId;
        this.arrowId = arrowId;
        this.roundId = roundId;
        ends = new Vector<Vector<CShot>>();
        ends.add(new Vector<CShot>());
    }

    public CDistance(Cursor cursor)
    {
        try
        {
            ends = (Vector<Vector<CShot>>) CSQLiteOpenHelper.setObjectBytes(cursor.getBlob(cursor.getColumnIndex("ends")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        _id = cursor.getLong(cursor.getColumnIndex("_id"));
        numberOfEnds = cursor.getInt(cursor.getColumnIndex("numberOfEnds"));
        arrowsInEnd = cursor.getInt(cursor.getColumnIndex("arrowsInEnd"));
        targetId = cursor.getLong(cursor.getColumnIndex("targetId"));
        arrowId = cursor.getLong(cursor.getColumnIndex("arrowId"));
        roundId = cursor.getLong(cursor.getColumnIndex("roundId"));
    }

    public boolean addShot(CShot shot)   {
        ends.lastElement().add(shot);
        if (ends.lastElement().size() == arrowsInEnd)
        {
            ends.add(new Vector<CShot>());
        }
        if (ends.size() ==  numberOfEnds+1)
        {
            ends.remove(ends.lastElement());
            return false;
        }
        else
            return true;
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
        values.put("arrowsInEnd", arrowsInEnd);
        values.put("targetId",targetId);
        values.put("arrowId",arrowId);
        values.put("roundId",roundId);
        database.insert(CSQLiteOpenHelper.TABLE_DISTANCES,null,values);
        }
        catch (Exception e)
        {
        }
    }

    public long getId()  {
        return _id;
    }


}

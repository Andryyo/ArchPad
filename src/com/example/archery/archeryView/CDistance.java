package com.example.archery.archeryView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    int _id;
    public Calendar timemark;
    public boolean isFinished;
    int numberOfArrows;
    int numberOfSeries;
    public CArrow arrow;
    public Vector<Vector<CShot>> series;

    public CDistance(int numberOfSeries, int numberOfArrows, CArrow arrow)  {
        timemark = Calendar.getInstance();
        this.numberOfSeries = numberOfSeries;
        this.numberOfArrows = numberOfArrows;
        this.arrow = arrow;
        isFinished = false;
        series = new Vector<Vector<CShot>>();
        series.add(new Vector<CShot>());
    }

    public CDistance(Cursor cursor)
    {
        //"series" "numberOfSeries" "numberOfArrows" "isFinished" "timemark"
        try
        {
            setSeries(cursor.getBlob(cursor.getColumnIndex("series")));
            setCalendar(cursor.getBlob(cursor.getColumnIndex("timemark")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        _id = cursor.getInt(cursor.getColumnIndex("id"));
        numberOfSeries = cursor.getInt(cursor.getColumnIndex("numberOfSeries"));
        numberOfArrows = cursor.getInt(cursor.getColumnIndex("numberOfArrows"));
        isFinished = (cursor.getInt(cursor.getColumnIndex("isFinished"))!=0);
        this.arrow = arrow;
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

    private byte[] getSeries() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        byte [] out = baos.toByteArray();
        oos.writeObject(series);
        baos.close();
        return baos.toByteArray();
    }

    private void setSeries(byte[] s) throws Exception
    {
        ByteArrayInputStream baos = new ByteArrayInputStream(s);
        ObjectInputStream oos = new ObjectInputStream(baos);
        series = (Vector<Vector<CShot>>) oos.readObject();
        oos.close();
    }

    private byte[] getCalendar() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        byte [] bytes = baos.toByteArray();
        oos.writeObject(timemark);
        bytes = baos.toByteArray();
        oos.close();
        bytes = baos.toByteArray();
        return baos.toByteArray();
    }

    private void setCalendar(byte [] s) throws Exception {
        ByteArrayInputStream baos = new ByteArrayInputStream(s);
        ObjectInputStream oos = new ObjectInputStream(baos);
        timemark = (Calendar) oos.readObject();
        oos.close();
    }

    public void addToDatabase(SQLiteDatabase database) throws Exception  {
        ContentValues values = new ContentValues();
        values.put("series",getSeries());
        values.put("numberOfSeries",numberOfSeries);
        values.put("numberOfArrows",numberOfArrows);
        values.put("isFinished",isFinished);
        values.put("timemark",getCalendar());
        long i = database.insert("distances",null,values);
    }

    public int getId()  {
        return _id;
    }
}

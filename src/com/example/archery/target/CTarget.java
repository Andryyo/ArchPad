package com.example.archery.target;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.archery.database.CSQLiteOpenHelper;

import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 23.04.13
 * Time: 21:18
 * To change this template use File | Settings | File Templates.
 */
public class CTarget implements Serializable   {
    Vector<CRing> rings;
    String name;
    int distance;
    public long id;
    float maxr;
    static Paint fillPaint = new Paint();
    static Paint strokePaint = new Paint();
    static Paint zoomPaint = new Paint();

    public CTarget(Cursor cursor) throws IOException, ClassNotFoundException {
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.id = cursor.getLong(cursor.getColumnIndex("_id"));
        this.rings = (Vector<CRing>) CSQLiteOpenHelper.setObjectBytes(cursor.getBlob(cursor.getColumnIndex("rings")));
        maxr = rings.lastElement().distanceFromCenter;
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
    }

    public CTarget(String name, Vector<CRing> rings, int distance)    {
        this.name = new String(name);
        if (!rings.isEmpty())
        {
            this.rings = new Vector<CRing>(rings);
            maxr = rings.lastElement().distanceFromCenter;
        }
        else
            this.rings = new Vector<CRing>();
        this.distance = distance;
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
        for (CRing ring : rings)
            ring.distanceFromCenter/=maxr;
    }

    public void addRing(CRing ring)   {
        for (CRing bufring : rings)
            bufring.distanceFromCenter*=maxr;
        rings.add(ring);
        maxr = ring.distanceFromCenter;
        for (CRing bufring : rings)
            bufring.distanceFromCenter/=maxr;
    }

    public void removeRing()    {
        if (!rings.isEmpty())
        {
            for (CRing bufring : rings)
                bufring.distanceFromCenter*=maxr;
            rings.remove(rings.lastElement());
            if (!rings.isEmpty())
            {
                maxr = rings.lastElement().distanceFromCenter;
                for (CRing bufring : rings)
                    bufring.distanceFromCenter/=maxr;
            }
            else
                maxr = 0;
        }
    }

    public void draw(Canvas canvas, int center, int r) {
        for (int i=rings.size()-1;i>=0;i--)
        {
            fillPaint.setColor(rings.get(i).color);
            strokePaint.setColor(Color.BLACK);
            canvas.drawCircle(center,center,r*rings.get(i).distanceFromCenter,fillPaint);
            canvas.drawCircle(center,center,r*rings.get(i).distanceFromCenter,strokePaint);
        }
    }

    public void writeToDatabase(SQLiteDatabase database) {
        try
        {
            ContentValues values = new ContentValues();
            values.put("rings", CSQLiteOpenHelper.getObjectBytes(rings));
            values.put("name",name);
            values.put("distance",distance);
            database.insert("targets",null,values);
        }
        catch (Exception e)
        {
        }
    }

    public boolean isEmpty()    {
        return rings.isEmpty();
    }

    public void addClosingRing() {
        for (CRing bufring : rings)
            bufring.distanceFromCenter*=0.9;
        maxr/=0.9;
        rings.add(new CRing(0,1f, Color.BLACK));
    }
}

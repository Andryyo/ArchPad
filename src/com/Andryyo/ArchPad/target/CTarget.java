package com.Andryyo.ArchPad.target;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

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
    public long id;
    float drawingRadius;
    float realRadius;
    static Paint fillPaint = new Paint();
    static Paint strokePaint = new Paint();

    public CTarget(Cursor cursor) throws IOException, ClassNotFoundException {
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.id = cursor.getLong(cursor.getColumnIndex("_id"));
        this.rings = (Vector<CRing>) CSQLiteOpenHelper.setObjectBytes(cursor.getBlob(cursor.getColumnIndex("rings")));
        this.realRadius = cursor.getFloat(cursor.getColumnIndex("radius"));
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
    }

    public CTarget(String name, Vector<CRing> rings, float realRadius, int distance)    {
        this.name = new String(name);
        if (!rings.isEmpty())
        {
            this.rings = new Vector<CRing>(rings);
            this.realRadius = realRadius;
        }
        else
            this.rings = new Vector<CRing>();
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
    }

    public void setDrawingRadius(float drawingRadius)   {
        this.drawingRadius = drawingRadius;
    }

    public void addRing(CRing ring)   {
        for (CRing bufring : rings)
            bufring.distanceFromCenter*= realRadius;
        rings.add(ring);
        realRadius = ring.distanceFromCenter;
        for (CRing bufring : rings)
            bufring.distanceFromCenter/= realRadius;
    }

    public void removeRing()    {
        if (!rings.isEmpty())
        {
            for (CRing bufring : rings)
                bufring.distanceFromCenter*= realRadius;
            rings.remove(rings.lastElement());
            if (!rings.isEmpty())
            {
                realRadius = rings.lastElement().distanceFromCenter;
                for (CRing bufring : rings)
                    bufring.distanceFromCenter/= realRadius;
            }
            else
                realRadius = 0;
        }
    }

    public void calcPoints(CShot shot, float arrowRadius)
    {
        float distance = shot.getDistance();
        for (int i = 0;i<rings.size();i++)
            if (distance-(arrowRadius/realRadius)<=rings.get(i).distanceFromCenter)
            {
                shot.setPoints(rings.get(i).points);
                return;
            }
        shot.setPoints(0);
    }

    public void draw(Canvas canvas) {
        for (int i=rings.size()-1;i>=0;i--)
        {
            fillPaint.setColor(rings.get(i).color);
            strokePaint.setColor(Color.BLACK);
            canvas.drawCircle(drawingRadius, drawingRadius, drawingRadius *rings.get(i).distanceFromCenter,fillPaint);
            canvas.drawCircle(drawingRadius, drawingRadius, drawingRadius *rings.get(i).distanceFromCenter,strokePaint);
        }
    }

    public void drawShot(Canvas canvas, float arrowRadius, Paint arrowPaint, CShot shot)    {
        if (realRadius!=0)
            canvas.drawCircle((shot.x+1)* drawingRadius,(shot.y+1)* drawingRadius,arrowRadius/realRadius*drawingRadius,arrowPaint);
    }

    public void writeToDatabase(SQLiteDatabase database) {
        try
        {
            ContentValues values = new ContentValues();
            values.put("rings", CSQLiteOpenHelper.getObjectBytes(rings));
            values.put("name",name);
            values.put("radius",realRadius);
            database.insert(CSQLiteOpenHelper.TABLE_TARGETS,null,values);
        }
        catch (Exception e){    }
    }

    public boolean isEmpty()    {
        return rings.isEmpty();
    }

    public void addClosingRing() {
        addRing(new CRing(0,realRadius*1.1f,Color.BLACK));
    }
}

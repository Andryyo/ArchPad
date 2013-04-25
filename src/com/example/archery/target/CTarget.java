package com.example.archery.target;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.io.Serializable;
import java.util.Iterator;
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
    static Paint fillPaint = new Paint();
    static Paint strokePaint = new Paint();
    static Paint zoomPaint = new Paint();

    public CTarget(String name, Vector<CRing> rings)    {
        this.name = new String(name);
        this.rings = new Vector<CRing>(rings);
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
        float maxr = rings.lastElement().distanceFromCenter;
        for (CRing ring : rings)
            ring.distanceFromCenter/=maxr;
    }

    public CTarget(CTarget target)
    {
        this.name = new String(target.name);
        this.rings = new Vector<CRing>(target.rings);
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
        float maxr = rings.lastElement().distanceFromCenter;
        for (CRing ring : rings)
            ring.distanceFromCenter/=maxr;
    }

    public void draw(Canvas canvas, int r) {
        for (int i=rings.size()-1;i>=0;i--)
        {
            fillPaint.setColor(rings.get(i).color);
            strokePaint.setColor(Color.BLACK);
            canvas.drawCircle(r,r,r*rings.get(i).distanceFromCenter,fillPaint);
            canvas.drawCircle(r,r,r*rings.get(i).distanceFromCenter,strokePaint);
        }
    }
}

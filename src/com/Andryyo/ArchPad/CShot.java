package com.Andryyo.ArchPad;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.Andryyo.ArchPad.target.CRing;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 19.04.13
 * Time: 23:18
 * To change this template use File | Settings | File Templates.
 */
public class CShot implements Serializable{
    private int points;
    public final float x;
    public final float y;

    public CShot(float x,float y) {
        this.x = x;
        this.y = y;
    }

    public CShot(Vector<CRing> rings,float x,float y,float arrowRadius) {
        this.x = x;
        this.y = y;
    }

    public int getPoints()  {
        return points;
    }

    public String toString()    {
        if (points != 0)
            return (Integer.toString(points)+"");
        else return "M ";
    }

    public float getDistance()  {
        return (float) Math.hypot(x,y);
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

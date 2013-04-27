package com.example.archery;

import com.example.archery.target.CRing;

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
        this.calcPoints(rings,arrowRadius);
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

    public void calcPoints(Vector<CRing> rings,float arrowRadius)
    {
        float distance = getDistance();
        for (int i = 0;i<rings.size();i++)
            if (distance-arrowRadius<=rings.get(i).distanceFromCenter)
            {
                points = rings.get(i).points;
                return;
            }
        points = 0;
    }
}

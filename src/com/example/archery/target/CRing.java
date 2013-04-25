package com.example.archery.target;

import java.io.Serializable;

/**
* Created with IntelliJ IDEA.
* User: Андрей
* Date: 23.04.13
* Time: 22:28
* To change this template use File | Settings | File Templates.
*/

public class CRing implements Serializable {
    public float distanceFromCenter;
    public int color;
    public int points;

    public CRing(int points, float distanceFromCenter, int color)   {
        this.distanceFromCenter = distanceFromCenter;
        this.color = color;
        this.points = points;
    }
}

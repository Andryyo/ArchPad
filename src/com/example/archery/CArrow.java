package com.example.archery;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 25.04.13
 * Time: 21:55
 * To change this template use File | Settings | File Templates.
 */
public class CArrow implements Serializable{
    public float radius;
    String material;
    public String name;

    public CArrow(String name, String material, float radius)   {
        this.name = name;
        this.material = material;
        this.radius = radius;
    }
}

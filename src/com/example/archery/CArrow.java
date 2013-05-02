package com.example.archery;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.example.archery.database.CMySQLiteOpenHelper;

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
    public String description;
    public String name;
    public int _id;

    public CArrow(String name, String description, float radius,int id)   {
        this.name = name;
        this.description = description;
        this.radius = radius;
        this._id = id;
    }


    public void writeToDatabase(SQLiteDatabase database) {
        try
        {
            ContentValues values = new ContentValues();
            values.put("description",description);
            values.put("name",name);
            values.put("radius",radius);
            database.insert("arrows", null, values);
        }
        catch (Exception e)
        {
        }
    }
}

package com.example.archery.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.archery.CArrow;
import com.example.archery.archeryView.CDistance;
import com.example.archery.target.CRing;
import com.example.archery.target.CTarget;

import java.io.*;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 28.04.13
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public class CMySQLiteOpenHelper extends SQLiteOpenHelper {

    private static CMySQLiteOpenHelper mInstance;
    public SQLiteDatabase closeTargetsDatabase;
    public SQLiteDatabase arrowsDatabase;
    private SQLiteDatabase distancesDatabase;

    public static CMySQLiteOpenHelper getHelper(Context context)    {
        if (mInstance == null)
            mInstance = new CMySQLiteOpenHelper(context);
        return mInstance;
    }

    private CMySQLiteOpenHelper(Context context) {
        super(context, "Archery", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database)  {
        String CREATE_TARGETS_TABLE = "CREATE TABLE targets(_id INTEGER PRIMARY KEY,name TEXT,distance INTEGER,rings BLOB)";
        database.execSQL(CREATE_TARGETS_TABLE);
        String CREATE_ARROWS_TABLE = "CREATE TABLE arrows(_id INTEGER PRIMARY KEY, radius INTEGER,name STRING,description STRING)";
        database.execSQL(CREATE_ARROWS_TABLE);
        String CREATE_DISTANCES_TABLE = "CREATE TABLE distances(_id INTEGER PRIMARY KEY,series BLOB,numberOfSeries INTEGER," +
                "numberOfArrows INTEGER,isFinished INTEGER,timemark BLOB,arrowId INTEGER," +
                "targetId INTEGER,FOREIGN KEY(targetId) REFERENCES targets(_id),FOREIGN KEY(arrowId) REFERENCES arrows(_id))";
        database.execSQL(CREATE_DISTANCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS distances");
        database.execSQL("DROP TABLE IF EXISTS targets");
        database.execSQL("DROP TABLE IF EXISTS arrows");
        onCreate(database);
    }

    public void closeTargetsDatabase() {
        closeTargetsDatabase.close();
    }

    public CTarget getTarget(long _id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query("targets",new String[]{"_id","name","distance","rings"},
                "_id = ?",new String[]{Long.toString(_id)}
        ,null,null,null);
        if (!cursor.moveToFirst())
        {
            database.close();
            return null;
        }
        else
        {
            try
            {
            database.close();
            return new CTarget(cursor.getString(cursor.getColumnIndex("name")),
                    (Vector<CRing>) setObjectBytes(cursor.getBlob(cursor.getColumnIndex("rings"))),
                            cursor.getInt(cursor.getColumnIndex("distance")),cursor.getInt(cursor.getColumnIndex("_id")));
            }
            catch (IOException e)
            {
                e.printStackTrace();
                database.close();
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                database.close();
                return null;
            }
        }
    }

    public static CTarget getTarget(Cursor cursor) {
        try
        {
            int i= cursor.getInt(cursor.getColumnIndex("_id"));
            return new CTarget(cursor.getString(cursor.getColumnIndex("name")),
                    (Vector<CRing>) setObjectBytes(cursor.getBlob(cursor.getColumnIndex("rings"))),
                    cursor.getInt(cursor.getColumnIndex("distance")),cursor.getInt(cursor.getColumnIndex("_id")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addTarget(CTarget target) {
        SQLiteDatabase database = this.getWritableDatabase();
        target.writeToDatabase(database);
        database.close();
    }

    public Cursor getTargetsCursor()
    {
        closeTargetsDatabase = this.getReadableDatabase();
        return closeTargetsDatabase.rawQuery("SELECT * FROM targets",null);
    }


    public void deleteAllDistances() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM distances");
        database.close();
    }

    public int getDistanceCount()
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM distances",null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void addDistance(CDistance distance) throws Exception{
        SQLiteDatabase database = this.getWritableDatabase();
        distance.writeToDatabase(database);
        database.close();
    }

    public Cursor getDistancesCursor()  {
        distancesDatabase = this.getReadableDatabase();
        return distancesDatabase.rawQuery("SELECT * FROM distances",null);
    }

    public Cursor getDistanceCursor(long _id)   {
        distancesDatabase = this.getReadableDatabase();
        return  distancesDatabase.query("distances", new String[]{"_id","series", "numberOfSeries", "numberOfArrows",
                "isFinished", "timemark","targetId","arrowID"},
                "_id=?", new String[]{String.valueOf(_id)}, null, null, null,null);
    }

    public void closeDistancesDatabase()    {
        distancesDatabase.close();
    }

    public CDistance getUnfinishedDistance()    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query("distances", new String[]{"_id","series", "numberOfSeries", "numberOfArrows",
                "isFinished", "timemark","targetId","arrowID"},
                "isFinished=?", new String[]{String.valueOf(0)}, null, null, null,null);
        if (cursor.moveToFirst()==false)
        {
            cursor.close();
            database.close();
            return null;
        }
        CDistance distance = new CDistance(cursor);
        deleteDistance(distance);
        cursor.close();
        database.close();
        return distance;
    }

    public void deleteDistance(CDistance distance)  {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("distances", "_id = ?", new String[]{String.valueOf(distance.getId())});
        database.close();
    }


    public void addArrow(CArrow arrow)  {
        SQLiteDatabase database = this.getWritableDatabase();
        arrow.writeToDatabase(database);
        database.close();
    }

    public CArrow getArrow(long _id)  {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query("arrows",new String[]{"_id","name","description","radius"},
                "_id = ?",new String[]{Long.toString(_id)}
                ,null,null,null);
        if (!cursor.moveToFirst())
        {
            database.close();
            return null;
        }
        else
        {
            database.close();
            return new CArrow(cursor.getString(cursor.getColumnIndex("name")),
                              cursor.getString(cursor.getColumnIndex("description")),
                              cursor.getFloat(cursor.getColumnIndex("radius")),
                              cursor.getInt(cursor.getColumnIndex("_id")));
        }
    }

    public Cursor getArrowsCursor()  {
        arrowsDatabase = this.getReadableDatabase();
        return arrowsDatabase.rawQuery("SELECT * FROM arrows",null);
    }

    public void closeArrowsDatabase() {
        arrowsDatabase.close();
    }

    static public byte [] getObjectBytes(Object o) throws IOException  {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return baos.toByteArray();
    }

    static public Object setObjectBytes(byte [] b) throws IOException,ClassNotFoundException {
        ByteArrayInputStream baos = new ByteArrayInputStream(b);
        ObjectInputStream oos = new ObjectInputStream(baos);
        oos.close();
        return oos.readObject();
    }

}

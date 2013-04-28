package com.example.archery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.archery.archeryView.CDistance;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 28.04.13
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public class CMySQLiteOpenHelper extends SQLiteOpenHelper {

    public CMySQLiteOpenHelper(Context context) {
        super(context, "Archery", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database)  {
        String CREATE_DISTANCES_TABLE = "CREATE TABLE distances(id INTEGER PRIMARY KEY,series BLOB,numberOfSeries INTEGER" +
                ",numberOfArrows INTEGER,isFinished INTEGER, timemark BLOB)";
        database.execSQL(CREATE_DISTANCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS distances");
        onCreate(database);
    }

    public void clean() {
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
        SQLiteDatabase db = this.getWritableDatabase();
        distance.addToDatabase(db);
        db.close();
    }

    public Vector<CDistance> getAllDistances()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Vector<CDistance> distances = new Vector<CDistance>();
        Cursor cursor = db.rawQuery("SELECT * FROM distances",null);
        if (cursor.moveToFirst())
        do {
            distances.add(new CDistance(cursor));
        }
        while (cursor.moveToNext());
        cursor.close();
        db.close();
        return distances;
    }

    public CDistance getUnfinishedDistance()    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("distances", new String[]{"id","series", "numberOfSeries", "numberOfArrows", "isFinished", "timemark"},
                "isFinished=?", new String[]{String.valueOf(0)}, null, null, null,null);
        int i = cursor.getCount();
        if (cursor.moveToFirst()==false)
        {
            cursor.close();
            db.close();
            return null;
        }
        CDistance distance = new CDistance(cursor);
        deleteDistance(distance);
        cursor.close();
        db.close();
        return distance;
    }

    public void deleteDistance(CDistance distance)  {
        SQLiteDatabase database = this.getWritableDatabase();
        int i = database.delete("distances","id = ?",new String[]{String.valueOf(distance.getId())});
        database.close();
    }

   // public void updateDistance(CDistance distance)

}

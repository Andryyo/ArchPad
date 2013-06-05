package com.Andryyo.ArchPad.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.AsyncTaskLoader;
import com.Andryyo.ArchPad.CArrow;
import com.Andryyo.ArchPad.archeryView.CDistance;
import com.Andryyo.ArchPad.target.CTarget;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 28.04.13
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public class CSQLiteOpenHelper extends SQLiteOpenHelper {

    private static CSQLiteOpenHelper helper = null;
    private Context context;
    public static final String TABLE_DISTANCES = "distances";
    public static final String TABLE_ARROWS = "arrows";
    public static final String TABLE_SIGHTS = "sights";
    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_TARGETS = "targets";

    private CSQLiteOpenHelper(Context context) {
        super(context, "Archery", null, 1);
        this.context = context;
    }

    public static CSQLiteOpenHelper getHelper(Context context) {
        if (helper==null)
        {
            helper = new CSQLiteOpenHelper(context);
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase database)  {
        String CREATE_TARGETS_TABLE = "CREATE TABLE targets(_id INTEGER PRIMARY KEY,name TEXT,distance INTEGER,rings BLOB)";
        database.execSQL(CREATE_TARGETS_TABLE);
        String CREATE_ARROWS_TABLE = "CREATE TABLE arrows(_id INTEGER PRIMARY KEY, radius INTEGER,name STRING,description STRING)";
        database.execSQL(CREATE_ARROWS_TABLE);
        String CREATE_SIGHTS_TABLE = "CREATE TABLE sights(_id INTEGER PRIMARY KEY, name TEXT, description TEXT, x INTEGER, y INTEGER)";
        database.execSQL(CREATE_SIGHTS_TABLE);
        String CREATE_NOTES_TABLE = "CREATE TABLE notes(_id INTEGER PRIMARY KEY, name TEXT, text TEXT, timemark INTEGER)";
        database.execSQL(CREATE_NOTES_TABLE);
        String CREATE_DISTANCES_TABLE = "CREATE TABLE distances(_id INTEGER PRIMARY KEY,series BLOB,numberOfSeries INTEGER," +
                "numberOfArrows INTEGER,isFinished INTEGER,timemark INTEGER,arrowId INTEGER," +
                "targetId INTEGER,FOREIGN KEY(targetId) REFERENCES targets(_id),FOREIGN KEY(arrowId) REFERENCES arrows(_id))";
        database.execSQL(CREATE_DISTANCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS distances");
        database.execSQL("DROP TABLE IF EXISTS targets");
        database.execSQL("DROP TABLE IF EXISTS sights");
        database.execSQL("DROP TABLE IF EXISTS arrows");
        database.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(database);
    }

    private Cursor getCursor(String table)   {
        return this.getReadableDatabase().query(table,null, null, null, null, null, null);
    }

    private Cursor getCursor(String table, long _id) {
        return this.getReadableDatabase().query(table, null,
                "_id=?", new String[]{Long.toString(_id)}, null, null, null, null);
    }

    public static CSQLiteCursorLoader getCursorLoader(Context context, String table)   {
        return new CSQLiteCursorLoader(context, table);
    }

    public static CSQLiteCursorLoader getCursorLoader(Context context, String table, long _id)   {
        return new CSQLiteCursorLoader(context, table, _id);
    }

    private static class CSQLiteCursorLoader extends AsyncTaskLoader<Cursor> {

        private String table;
        private long _id;
        private Context context;

        public CSQLiteCursorLoader(Context context, String table, long _id) {
            super(context);
            this.context = context;
            this.table = table;
            this._id = _id;
        }

        public CSQLiteCursorLoader(Context context, String table) {
            super(context);
            this.context = context;
            this.table = table;
            this._id = -1;
        }

        @Override
        public void onStartLoading()    {
            forceLoad();
        }

        @Override
        public void onCanceled(Cursor cursor)  {
            super.onCanceled(cursor);
            cursor.close();
        }

        @Override
        public Cursor loadInBackground() {
            if (_id == -1)
                return getHelper(context).getCursor(table);
            else
                return getHelper(context).getCursor(table, _id);
        }
    }

    public void delete(String table)  {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(table, null, null);
        database.close();
    }

    public void delete(String table, long _id)  {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(table, "_id = ?", new String[]{Long.toString(_id)});
        database.close();
    }

    public CTarget getTarget(long _id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query("targets",new String[]{"_id","name","distance","rings"},
                "_id = ?",new String[]{Long.toString(_id)}
        ,null,null,null);
        if (!cursor.moveToFirst())
        {
            cursor.close();
            database.close();
            return null;
        }
        else
        {
            try
            {
            database.close();
            return new CTarget(cursor);
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

    public void addTarget(CTarget target) {
        SQLiteDatabase database = this.getWritableDatabase();
        target.writeToDatabase(database);
        database.close();
    }

    public void deleteTarget(long _id)    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_TARGETS, "_id = ?", new String[]{Long.toString(_id)});
        database.delete(TABLE_DISTANCES, "arrowId=?", new String[]{Long.toString(_id)});
        database.close();
    }

    public void addDistance(CDistance distance) throws Exception{
        SQLiteDatabase database = this.getWritableDatabase();
        distance.writeToDatabase(database);
        database.close();
    }

    public CDistance getDistance(long id){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query("distances", new String[]{"_id","series", "numberOfSeries", "numberOfArrows",
                "isFinished", "timemark","targetId","arrowID"},
                "_id=?", new String[]{Long.toString(id)}, null, null, null,null);
        if (cursor.moveToFirst()==false)
        {
            cursor.close();
            database.close();
            return null;
        }
        CDistance distance = new CDistance(cursor);
        cursor.close();
        database.close();
        return distance;
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
        delete(TABLE_DISTANCES, distance.getId());
        cursor.close();
        database.close();
        return distance;
    }

    public void addArrow(CArrow arrow)  {
        SQLiteDatabase database = this.getWritableDatabase();
        arrow.writeToDatabase(database);
        database.close();
    }

    public void deleteArrow(long _id)    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("arrows", "_id = ?", new String[]{Long.toString(_id)});
        database.delete("distances", "arrowId=?", new String[]{Long.toString(_id)});
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
            cursor.close();
            return null;
        }
        else
        {
            database.close();
            CArrow arrow = new CArrow(cursor.getString(cursor.getColumnIndex("name")),
                              cursor.getString(cursor.getColumnIndex("description")),
                              cursor.getFloat(cursor.getColumnIndex("radius")),
                              cursor.getInt(cursor.getColumnIndex("_id")));
            cursor.close();
            return arrow;
        }
    }

    public String[] getSight(long id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query("sights",new String[]{"_id","x","y"},
                "_id = ?",new String[]{Long.toString(id)}
                ,null,null,null);
        cursor.moveToFirst();
        database.close();
        String[] strings = new String[]{Float.toString(cursor.getFloat(cursor.getColumnIndex("x"))),
                                           Float.toString(cursor.getFloat(cursor.getColumnIndex("y")))};
        cursor.close();
        return strings;
    }

    public void addSight(String name, String description)   {
        SQLiteDatabase database = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("description",description);
            database.insert("sights",null,values);
        }
        catch (Exception e)
        {
        }
        database.close();
    }

    public void updateSight(long id, String x, String y) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put("x", Float.valueOf(x));
            }
        catch (NumberFormatException e) {
            values.put("x", 0);
            }
        try {
            values.put("y", Float.valueOf(y));
            }
        catch (NumberFormatException e) {
            values.put("y", 0);
        }
        try {
            database.update("sights",values,"_id=?",new String[]{Long.toString(id)});
        }
        catch (Exception e)
        {
        }
        finally {
            database.close();
        }
    }

    public void addNote(String name,String text, long timemark)   {
        SQLiteDatabase database = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("text",text);
            values.put("timemark",timemark);
            database.insert("notes",null,values);
        }
        catch (Exception e)
        {
        }
        database.close();
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

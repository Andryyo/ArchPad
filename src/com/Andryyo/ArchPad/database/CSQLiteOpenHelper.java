package com.Andryyo.ArchPad.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
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
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;

    private CSQLiteOpenHelper(Context context) {
        super(context, "Archery", null, 1);
        this.context = context;
        readableDatabase = getReadableDatabase();
    }

    private void refresh()  {
        if (!readableDatabase.isOpen())
            readableDatabase = getReadableDatabase();
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
        String CREATE_TARGETS_TABLE = "CREATE TABLE targets(_id INTEGER PRIMARY KEY,name TEXT,radius INTEGER,distance INTEGER,rings BLOB)";
        database.execSQL(CREATE_TARGETS_TABLE);
        String CREATE_ARROWS_TABLE = "CREATE TABLE arrows(_id INTEGER PRIMARY KEY, radius INTEGER,name STRING,description STRING)";
        database.execSQL(CREATE_ARROWS_TABLE);
        String CREATE_SIGHTS_TABLE = "CREATE TABLE sights(_id INTEGER PRIMARY KEY, name TEXT, description TEXT, x INTEGER, y INTEGER)";
        database.execSQL(CREATE_SIGHTS_TABLE);
        String CREATE_NOTES_TABLE = "CREATE TABLE notes(_id INTEGER PRIMARY KEY, name TEXT, text TEXT, timemark INTEGER)";
        database.execSQL(CREATE_NOTES_TABLE);
        String CREATE_DISTANCES_TABLE = "CREATE TABLE distances(_id INTEGER PRIMARY KEY,rounds BLOB,numberOfSeries INTEGER," +
                "numberOfArrows INTEGER,timemark INTEGER,arrowId INTEGER," +
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
        Cursor cursor =  readableDatabase.query(table,null, null, null, null, null, null);
        int i = cursor.getCount();
        return cursor;
    }

    private Cursor getCursor(String table, long _id) {
        return readableDatabase.query(table, null,
                "_id=?", new String[]{Long.toString(_id)}, null, null, null, null);
    }

    public static CSQLiteCursorLoader getCursorLoader(Context context, String table)   {
        return new CSQLiteCursorLoader(context, table);
    }

    public static CSQLiteCursorLoader getCursorLoader(Context context, String table, long _id, Bundle data)   {
        return new CSQLiteCursorLoader(context, table, _id, data);
    }

    public static class CSQLiteCursorLoader extends AsyncTaskLoader<Cursor> {

        private String table;
        private long _id;
        private Context context;
        private Bundle data;
        private final ForceLoadContentObserver observer = new ForceLoadContentObserver();
        private Cursor mCursor;

        public CSQLiteCursorLoader(Context context, String table, long _id, Bundle data) {
            super(context);
            this.context = context;
            this.table = table;
            this._id = _id;
            this.data = data;
        }

        public CSQLiteCursorLoader(Context context, String table) {
            super(context);
            this.context = context;
            this.table = table;
            this._id = -1;
        }

        public Bundle getData()   {
            return data;
        }

        /* Runs on a worker thread */
        @Override
        public Cursor loadInBackground() {
            if (_id == -1)
                mCursor = getHelper(context).getCursor(table);
            else
                mCursor =  getHelper(context).getCursor(table, _id);
            if (mCursor != null) {
                int i = mCursor.getCount();
                // Ensure the cursor window is filled
                mCursor.registerContentObserver(observer);
            }
            return mCursor;
        }

        /* Runs on the UI thread */
        @Override
        public void deliverResult(Cursor cursor) {
            if (isReset()) {
                // An async query came in while the loader is stopped
                if (cursor != null) {
                    cursor.close();
                }
                return;
            }
            Cursor oldCursor = mCursor;
            mCursor = cursor;

            if (isStarted()) {
                super.deliverResult(cursor);
            }

            if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
                oldCursor.close();
            }
        }

        @Override
        protected void onStartLoading() {
            if (mCursor != null) {
                deliverResult(mCursor);
            }
            if (takeContentChanged() || mCursor == null) {
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        @Override
        public void onCanceled(Cursor cursor) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        @Override
        protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
            mCursor = null;
        }

    }

    public synchronized void delete(String table)  {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(table, null, null);
        database.close();
        refresh();
    }

    public synchronized void delete(String table, long _id)  {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(table, "_id = ?", new String[]{Long.toString(_id)});
        database.close();
        refresh();
    }

    public CTarget getTarget(long _id) {
        Cursor cursor = readableDatabase.query("targets", null,
                "_id = ?", new String[]{Long.toString(_id)}
                , null, null, null);
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return null;
        }
        else
        {
            try {
            return new CTarget(cursor);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public synchronized void addTarget(CTarget target) {
        SQLiteDatabase database = this.getWritableDatabase();
        target.writeToDatabase(database);
        database.close();
        refresh();
    }

    public synchronized void deleteTarget(long _id)    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_TARGETS, "_id = ?", new String[]{Long.toString(_id)});
        database.delete(TABLE_DISTANCES, "arrowId=?", new String[]{Long.toString(_id)});
        database.close();
        refresh();
    }

    public synchronized void addDistance(CDistance distance) throws Exception{
        SQLiteDatabase database = this.getWritableDatabase();
        distance.writeToDatabase(database);
        database.close();
        refresh();
    }

    public CDistance getDistance(long id){
        Cursor cursor = readableDatabase.query("distances", new String[]{"_id","rounds", "numberOfSeries", "numberOfArrows",
                "timemark","targetId","arrowID"},
                "_id=?", new String[]{Long.toString(id)}, null, null, null,null);
        if (cursor.moveToFirst()==false)
        {
            cursor.close();
            return null;
        }
        CDistance distance = new CDistance(cursor);
        cursor.close();
        return distance;
    }

    public synchronized void addArrow(CArrow arrow)  {
        SQLiteDatabase database = this.getWritableDatabase();
        arrow.writeToDatabase(database);
        database.close();
        refresh();
    }

    public synchronized void deleteArrow(long _id)    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("arrows", "_id = ?", new String[]{Long.toString(_id)});
        database.delete("distances", "arrowId=?", new String[]{Long.toString(_id)});
        database.close();
        refresh();
    }

    public CArrow getArrow(long _id)  {
        Cursor cursor = readableDatabase.query("arrows",new String[]{"_id","name","description","radius"},
                "_id = ?",new String[]{Long.toString(_id)}
                ,null,null,null);
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return null;
        }
        else
        {
            CArrow arrow = new CArrow(cursor.getString(cursor.getColumnIndex("name")),
                              cursor.getString(cursor.getColumnIndex("description")),
                              cursor.getFloat(cursor.getColumnIndex("radius")),
                              cursor.getInt(cursor.getColumnIndex("_id")));
            cursor.close();
            return arrow;
        }
    }

    public synchronized String[] getSight(long id) {
        Cursor cursor = readableDatabase.query("sights",new String[]{"_id","x","y"},
                "_id = ?",new String[]{Long.toString(id)}
                ,null,null,null);
        cursor.moveToFirst();
        String[] strings = new String[]{Float.toString(cursor.getFloat(cursor.getColumnIndex("x"))),
                                           Float.toString(cursor.getFloat(cursor.getColumnIndex("y")))};
        cursor.close();
        return strings;
    }

    public synchronized void addSight(String name, String description)   {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description",description);
        database.insert("sights",null,values);
        database.close();
        refresh();
    }

    public synchronized void updateSight(long id, String x, String y) {
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
        database.update("sights",values,"_id=?",new String[]{Long.toString(id)});
        database.close();
        refresh();
    }

    public synchronized void addNote(String name,String text, long timemark)   {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("text",text);
        values.put("timemark",timemark);
        database.insert("notes",null,values);
        database.close();
        refresh();
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

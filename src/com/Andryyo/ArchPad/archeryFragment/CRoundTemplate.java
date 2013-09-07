package com.Andryyo.ArchPad.archeryFragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 24.06.13
 * Time: 18:58
 * To change this template use File | Settings | File Templates.
 */
public class CRoundTemplate implements Serializable{

    private long arrowId;
    private String description;
    //Ибо при попытке унаследовать класс начинают выскакивать исключения при сериализации
    private ArrayList<CDistanceTemplate> templates = new ArrayList<CDistanceTemplate>();

    public CRound createRound(Context context)    {
        return new CRound(context, description, templates, arrowId);
    }

    public CRoundTemplate setArrowId(long id)  {
        this.arrowId = id;
        return this;
    }

    public CRoundTemplate addDistanceTemplate(CDistanceTemplate template)  {
        templates.add(template);
        return this;
    }

    public CRoundTemplate setDescription(String description)  {
        this.description = new String(description);
        return this;
    }

    public void writeToDatabase(SQLiteDatabase database)    {
        ContentValues values = new ContentValues();
        values.put("description", description);
        try {
            values.put("template",CSQLiteOpenHelper.getObjectBytes(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
        database.insert(CSQLiteOpenHelper.TABLE_ROUND_TEMPLATES, null, values);
    }
}

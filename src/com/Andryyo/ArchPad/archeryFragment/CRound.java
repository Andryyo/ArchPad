package com.Andryyo.ArchPad.archeryFragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;
import com.Andryyo.ArchPad.statistics.IOnUpdateListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 23.06.13
 * Time: 19:39
 * To change this template use File | Settings | File Templates.
 */
public class CRound implements Serializable{

    private long id;
    private Context context;
    private String description;
    private long arrowId;
    private ArrayList<CDistanceTemplate> templates;
    private int currentTemplateNum;
    private CDistanceTemplate nextTemplate;
    private CDistance currentDistance;
    private boolean isInfinite = false;
    private Calendar timemark;

    public CRound(Context context, String description, ArrayList<CDistanceTemplate> templates, long arrowId)    {
        this.context = context;
        this.description = description;
        this.arrowId = arrowId;
        this.id = CSQLiteOpenHelper.getHelper(context).addRound(this);
        this.templates = new ArrayList<CDistanceTemplate>(templates);
        currentTemplateNum = 1;
        currentDistance = templates.get(0).createDistance(id, arrowId);
    }

    public CRound(Context context, String description, CDistanceTemplate template, long arrowId) {
        this.context = context;
        this.description = description;
        this.id = CSQLiteOpenHelper.getHelper(context).addRound(this);
        nextTemplate = template;
        this.arrowId = arrowId;
        isInfinite = true;
        currentDistance = template.createDistance(id, arrowId);
    }

    public boolean addShot(CShot shot)    {
        if (!currentDistance.addShot(shot))
        {
            CSQLiteOpenHelper.getHelper(context).addDistance(currentDistance);
            if ((!isInfinite)&&(currentTemplateNum==templates.size()))
                return false;
            else
                currentDistance = null;
        }
        if (currentDistance==null)  {
            if (isInfinite)
                currentDistance = nextTemplate.createDistance(id, arrowId);
            else    {
                currentDistance = templates.get(currentTemplateNum).createDistance(id, arrowId);
                currentTemplateNum++;
            }
        }
        return true;
    }

    public void saveCurrentDistance()   {
        if (currentDistance!=null)
            CSQLiteOpenHelper.getHelper(context).addDistance(currentDistance);
    }

    public CDistance getCurrentDistance()   {
        return currentDistance;
    }

    public long writeToDatabase(SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put("description", description);
        values.put("arrowId",arrowId);
        timemark = Calendar.getInstance();
        values.put("timemark", timemark.getTimeInMillis());
        return database.insert(CSQLiteOpenHelper.TABLE_ROUNDS, null, values);
    }

    public long getArrowId() {
        return arrowId;
    }

    public long getCurrentTargetId() {
        return currentDistance.targetId;
    }
}
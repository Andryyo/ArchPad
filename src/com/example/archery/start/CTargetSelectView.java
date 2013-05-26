package com.example.archery.start;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.archery.database.CMySQLiteOpenHelper;
import com.example.archery.target.CTarget;

import java.io.IOException;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 30.04.13
 * Time: 23:15
 * To change this template use File | Settings | File Templates.
 */
public class CTargetSelectView extends Gallery {
    CTargetsSelectAdapter adapter;

    public CTargetSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CMySQLiteOpenHelper helper = CMySQLiteOpenHelper.getHelper(context);
        Cursor targets = helper.getTargetsCursor();
        adapter = new CTargetsSelectAdapter(context,targets);
        this.setAdapter(adapter);
    }

    public void closeCursor()   {
        adapter.getCursor().close();
    }

    public void deleteSelectedTarget() {
        CMySQLiteOpenHelper.getHelper(getContext()).deleteTarget(getSelectedItemId());
        update();
    }

    public void update() {
        adapter.changeCursor(CMySQLiteOpenHelper.getHelper(getContext()).getTargetsCursor());
        invalidate();
    }

    private class CTargetsSelectAdapter extends CursorAdapter {


        public CTargetsSelectAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            try {
                return new CTargetPreview(context,new CTarget(cursor));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            try {
                ((CTargetPreview)view).setTarget(new CTarget(cursor));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            };
        }
    }
}

package com.example.archery.start;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.R;
import com.example.archery.database.CMySQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 02.05.13
 * Time: 20:43
 * To change this template use File | Settings | File Templates.
 */
public class CArrowSelectView extends Spinner {
    SimpleCursorAdapter adapter;

    public CArrowSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context)  {
        adapter = new SimpleCursorAdapter(context,
                R.layout.simple_list_item_1,
                CMySQLiteOpenHelper.getHelper(context).getArrowsCursor(),
                new String[]{"name","description"},
                new int[]{R.id.text1});
        setAdapter(adapter);
    }

    /*public int getSelectedItemId() {
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(getSelectedItemPosition());
        int i = cursor.getInt(cursor.getColumnIndex("_id"));
        cursor.close();
        return i;
    } */

    public void closeCursor()   {
        adapter.getCursor().close();
    }

}

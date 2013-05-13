package com.example.archery.start;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.example.archery.R;
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
        adapter = new SimpleCursorAdapter(context,
                R.layout.spinner_child_2,
                CMySQLiteOpenHelper.getHelper(context).getArrowsCursor(),
                new String[]{"name","description"},
                new int[]{R.id.text1,R.id.text2});
        setAdapter(adapter);
    }

    public void closeCursor()   {
        ((SimpleCursorAdapter)this.getAdapter()).getCursor().close();
    }

    public void deleteSelectedArrow() {
        CMySQLiteOpenHelper.getHelper(getContext()).deleteArrow(getSelectedItemId());
        update();
    }

    public void update() {
        adapter.changeCursor(CMySQLiteOpenHelper.getHelper(getContext()).getArrowsCursor());
        invalidate();
    }
}

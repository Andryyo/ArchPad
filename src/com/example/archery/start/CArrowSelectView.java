package com.example.archery.start;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
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

    public CArrowSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAdapter(new SimpleCursorAdapter(context,
                R.layout.select_spinner_child,
                CMySQLiteOpenHelper.getHelper(context).getArrowsCursor(),
                new String[]{"name","description"},
                new int[]{R.id.name,R.id.description}));
    }

    public void closeCursor()   {
        ((SimpleCursorAdapter)this.getAdapter()).getCursor().close();
    }

}

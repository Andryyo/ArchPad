package com.example.archery.sight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.example.archery.R;
import com.example.archery.database.CMySQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 06.05.13
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
public class CSightSelectView extends Spinner {
    SimpleCursorAdapter adapter;

    public CSightSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        adapter = new SimpleCursorAdapter(context,
                R.layout.select_spinner_child,
                CMySQLiteOpenHelper.getHelper(context).getSightsCursor(),
                new String[]{"name","description"},
                new int[]{R.id.name,R.id.description});
        setAdapter(adapter);
    }

    public void closeCursor()   {
        ((SimpleCursorAdapter)this.getAdapter()).getCursor().close();
    }

    public void update()    {
        adapter.changeCursor(CMySQLiteOpenHelper.getHelper(getContext()).getSightsCursor());
    }

    public void setSelection(long sightId) {
        for (int i = 0;i<adapter.getCount();i++)
            if (adapter.getItemId(i)==sightId)
                setSelection(i);
    }

    public void deleteSigth() {
        CMySQLiteOpenHelper.getHelper(getContext()).deleteSight(getSelectedItemId());
        update();
    }
}

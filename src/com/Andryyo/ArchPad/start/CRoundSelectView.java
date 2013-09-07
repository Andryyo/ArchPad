package com.Andryyo.ArchPad.start;

import android.R;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 03.09.13
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class CRoundSelectView extends ListView {

    private LoaderManager loaderManager;
    private Context context;

    public CRoundSelectView(Context context, LoaderManager loaderManager) {
        super(context);
        this.context = context;
        this.loaderManager = loaderManager;
        this.setAdapter(new CMySimpleCursorAdapter());
    }

    private class CMySimpleCursorAdapter extends SimpleCursorAdapter implements LoaderManager.LoaderCallbacks<Cursor>{
        public CMySimpleCursorAdapter() {
            super(context, R.layout.simple_list_item_1, null, null, null, 0);
            loaderManager.restartLoader(0, null, this);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return CSQLiteOpenHelper.getCursorLoader(context, CSQLiteOpenHelper.TABLE_ROUNDS);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            changeCursorAndColumns(cursor, new String[]{"_id"}, new int[]{R.id.text1});
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            changeCursor(null);
        }
    }
}

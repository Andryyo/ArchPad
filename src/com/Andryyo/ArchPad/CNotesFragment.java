package com.Andryyo.ArchPad;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 11.05.13
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class CNotesFragment extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener {

    CMySimpleCursorTreeAdapter adapter;
    ExpandableListView expandableListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        setHasOptionsMenu(false);
        expandableListView = new ExpandableListView(getActivity());
        adapter = new CMySimpleCursorTreeAdapter(getActivity(),
                            getLoaderManager(),
                            R.layout.expand_list_group,
                            new String[]{"timemark"},
                            new int[]{R.id.text1},
                            R.layout.expand_list_child,
                            new String[]{"text"},
                            new int[]{R.id.text1});
        View footerView = inflater.inflate(R.layout.add_note_button, null);
        footerView.setOnClickListener(this);
        expandableListView.
                addFooterView(footerView);
        expandableListView.setAdapter(adapter);
        registerForContextMenu(expandableListView);
        return expandableListView;
    }

    public void update()    {
        if (adapter!=null)
            adapter.update();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.notes_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.delete_note)
        {
            ExpandableListView.ExpandableListContextMenuInfo info =
                    (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
            adapter.deleteRecord(info.id);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        new AlertDialog.Builder(getActivity())
        .setTitle("Новая заметка")
        .setView(getActivity().getLayoutInflater().inflate(R.layout.add_dialog, null))
        .setPositiveButton("Создать", this)
        .create().show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        CSQLiteOpenHelper.getHelper(getActivity()).addNote(
                ((EditText) ((AlertDialog) dialogInterface).findViewById(R.id.text1)).getText().toString(),
                ((EditText) ((AlertDialog) dialogInterface).findViewById(R.id.text2)).getText().toString(),
                Calendar.getInstance().getTimeInMillis()
        );
        update();
    }

    class CMySimpleCursorTreeAdapter extends SimpleCursorTreeAdapter implements LoaderManager.LoaderCallbacks<Cursor>{

        private static final String GROUP_POS = "groupPos";

        Context context;
        LoaderManager loaderManager;

        public CMySimpleCursorTreeAdapter(Context context, LoaderManager lm, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
            super(context, null, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
            this.setViewBinder(new CMyViewBinder());
            this.context = context;
            this.loaderManager = lm;
            update();
        }

        public void deleteRecord(long _id)  {
            CSQLiteOpenHelper helper = CSQLiteOpenHelper.getHelper(context);
            helper.delete(CSQLiteOpenHelper.TABLE_NOTES,_id);
            update();
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            Bundle bundle = new Bundle();
            bundle.putInt(GROUP_POS, groupCursor.getPosition());
            loaderManager.restartLoader((int) groupCursor.getLong(groupCursor.getColumnIndex("_id")), bundle, this);
            return null;
        }

        public void update()    {
            loaderManager.restartLoader(0, null, this);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            if (i==0)
                return CSQLiteOpenHelper.getCursorLoader(context, CSQLiteOpenHelper.TABLE_NOTES);
            else
                return CSQLiteOpenHelper.getCursorLoader(context, CSQLiteOpenHelper.TABLE_NOTES, i, bundle);

        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            if (cursorLoader.getId()==0)
            {
                changeCursor(cursor);
                if (getGroupCount()!=0)
                    expandableListView.setSelection(getGroupCount()-1);
            }
            else
                setChildrenCursor(((CSQLiteOpenHelper.CSQLiteCursorLoader)cursorLoader)
                        .getData().getInt(GROUP_POS), cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            changeCursor(null);
        }


        class CMyViewBinder implements ViewBinder {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex==cursor.getColumnIndex("text"))
                    return false;
                if (columnIndex==cursor.getColumnIndex("timemark"))
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("timemark")));
                    ((TextView)view).setText(
                            calendar.get(Calendar.DATE) + ":" +
                                    calendar.get(Calendar.MONTH) + ":" +
                                    calendar.get(Calendar.YEAR) + " " +
                                    calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                                    calendar.get(Calendar.MINUTE)+" "+
                                    cursor.getString(cursor.getColumnIndex("name")));
                    return true;
                }
                return false;
            }
        }
    }

}
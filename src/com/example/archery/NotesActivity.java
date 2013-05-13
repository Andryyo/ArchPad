package com.example.archery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.example.archery.database.CMySQLiteOpenHelper;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 11.05.13
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class NotesActivity extends ExpandableListActivity implements DialogInterface.OnDismissListener{

    CMySimpleCursorTreeAdapter adapter;
    LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        adapter = new CMySimpleCursorTreeAdapter(this,
                            CMySQLiteOpenHelper.getHelper(this).getNotesCursor(),
                            R.layout.expand_list_group,
                            new String[]{"timemark"},
                            new int[]{R.id.text1},
                            R.layout.expand_list_child,
                            new String[]{"text"},
                            new int[]{R.id.text1});
        getExpandableListView().
                addFooterView(inflater.inflate(R.layout.add_note,null));
        setListAdapter(adapter);
        registerForContextMenu(findViewById(android.R.id.list));
        getExpandableListView().setBackgroundColor(Color.BLACK);
         }

    public void addNote(View view)   {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Новая заметка");
        builder.setView(inflater.inflate(R.layout.add_dialog, null));
        builder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                CMySQLiteOpenHelper.getHelper(getApplicationContext()).addNote(
                        ((EditText) ((AlertDialog) dialog).findViewById(R.id.text1)).getText().toString(),
                        ((EditText) ((AlertDialog) dialog).findViewById(R.id.text2)).getText().toString(),
                        Calendar.getInstance().getTimeInMillis()
                );
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        ((CMySimpleCursorTreeAdapter)getExpandableListAdapter()).update();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.delete_record)
        {
            ExpandableListView.ExpandableListContextMenuInfo info =
                    (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
            adapter.deleteRecord(info.id);
        }
        return true;
    }

    class CMySimpleCursorTreeAdapter extends SimpleCursorTreeAdapter {

        Context context;

        public CMySimpleCursorTreeAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
            this.setViewBinder(new CMyViewBinder());
            this.context = context;
        }

        public void deleteRecord(long _id)  {
            CMySQLiteOpenHelper helper = CMySQLiteOpenHelper.getHelper(context);
            helper.deleteNote(_id);
            update();
        }

        @Override
        protected Cursor getChildrenCursor(Cursor cursor) {
            return CMySQLiteOpenHelper.getHelper(context).
                    getNoteCursor(cursor.getLong(cursor.getColumnIndex("_id")));
        }

        public void update()    {
            CMySQLiteOpenHelper helper = CMySQLiteOpenHelper.getHelper(context);
            adapter.changeCursor(helper.getNotesCursor());
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
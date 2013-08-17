package com.Andryyo.ArchPad.start;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Andryyo.ArchPad.CArrow;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 02.05.13
 * Time: 20:43
 * To change this template use File | Settings | File Templates.
 */
public class CArrowSelectView extends LinearLayout implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, DialogInterface.OnClickListener {

    public static final int arrowLoaderId = 1;

    private Context context;
    private SimpleCursorAdapter adapter;
    private Spinner spinner;
    private LoaderManager loaderManager;
    private FragmentManager fragmentManager;

    public CArrowSelectView(Context context, LoaderManager loaderManager, FragmentManager fragmentManager) {
        super(context);
        this.context = context;
        this.loaderManager = loaderManager;
        this.fragmentManager = fragmentManager;
        View view = LayoutInflater.from(context).inflate(R.layout.arrow_select_view, this);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        view.findViewById(R.id.addArrow).setOnClickListener(this);
        view.findViewById(R.id.deleteArrow).setOnClickListener(this);
        adapter = new SimpleCursorAdapter(context,
                R.layout.spinner_child_2,
                null,
                new String[]{"name","description"},
                new int[]{R.id.text1,R.id.text2});
        spinner.setAdapter(adapter);
        loaderManager.initLoader(arrowLoaderId, null, this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return  CSQLiteOpenHelper.getCursorLoader(context, CSQLiteOpenHelper.TABLE_ARROWS);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursorLoader.getId()==arrowLoaderId)
            adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        if (cursorLoader.getId()==arrowLoaderId)
            adapter.changeCursor(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())   {
            case R.id.addArrow:
            {
                new AlertDialog.Builder(context).
                        setTitle("Новый тип стрел").
                        setView(LayoutInflater.from(context).inflate(R.layout.arrow_add_dialog, null)).
                        setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).
                        setPositiveButton("Сохранить", this)
                        .create().show();
                break;
            }
            case R.id.deleteArrow:
            {
                CSQLiteOpenHelper.getHelper(context).deleteArrow(spinner.getSelectedItemId());
                loaderManager.restartLoader(arrowLoaderId, null, this);
                break;
            }
        }
    }

    public void saveArrow(CArrow arrow) {
        CSQLiteOpenHelper.getHelper(context).addArrow(arrow);
        loaderManager.restartLoader(arrowLoaderId, null, this);
    }

    public long getSelectedItemId()    {
        return spinner.getSelectedItemId();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            CArrow arrow = new CArrow(
                    ((EditText) ((AlertDialog)dialogInterface).findViewById(R.id.name)).getText().toString(),
                    ((EditText) ((AlertDialog)dialogInterface).findViewById(R.id.description)).getText().toString(),
                    Float.parseFloat(((EditText) ((AlertDialog)dialogInterface).findViewById(R.id.radius)).getText().toString()
                    ));
            saveArrow(arrow);
        } catch (Exception e)   {
            e.printStackTrace();
        }
        dialogInterface.dismiss();
    }
}

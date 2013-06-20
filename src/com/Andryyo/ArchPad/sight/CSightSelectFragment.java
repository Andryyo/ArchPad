package com.Andryyo.ArchPad.sight;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 13.06.13
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
public class CSightSelectFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, DialogInterface.OnClickListener {

    SimpleCursorAdapter adapter;
    Spinner spinner;
    long selectedItemId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.sight_select_fragment, null);
        view.findViewById(R.id.addSight).setOnClickListener(this);
        view.findViewById(R.id.deleteSight).setOnClickListener(this);
        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.spinner_child_2,
                null,
                new String[]{"name","description"},
                new int[]{R.id.text1,R.id.text2});
        spinner = (Spinner)view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    public void update()    {
        getLoaderManager().restartLoader(0, null, this);
    }

    public void setSelection(long sightId) {
        selectedItemId = sightId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return CSQLiteOpenHelper.getCursorLoader(getActivity(), CSQLiteOpenHelper.TABLE_SIGHTS);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> objectLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
        for (int i = 0;i<adapter.getCount();i++)
            if (adapter.getItemId(i)==selectedItemId)
                spinner.setSelection(i);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> objectLoader) {
        adapter.changeCursor(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())   {
            case R.id.addSight:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Новая конфигурация прицела")
                        .setView(getActivity().getLayoutInflater().inflate(R.layout.add_dialog, null))
                        .setPositiveButton("Создать", this).create().show();
                break;
            case R.id.deleteSight:
                CSQLiteOpenHelper.getHelper(getActivity()).delete(CSQLiteOpenHelper.TABLE_SIGHTS,
                        spinner.getSelectedItemId());
                update();
                break;
    }
}

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        CSQLiteOpenHelper.getHelper(getActivity()).addSight(
                ((EditText) ((AlertDialog) dialogInterface).findViewById(R.id.text1)).getText().toString(),
                ((EditText) ((AlertDialog) dialogInterface).findViewById(R.id.text2)).getText().toString()
        );
        update();
    }

    public void setOnItemSelectedListener(Spinner.OnItemSelectedListener listener) {
        spinner.setOnItemSelectedListener(listener);
    }
}

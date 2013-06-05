package com.example.archery.start;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.archery.CArrow;
import com.example.archery.R;
import com.example.archery.database.CSQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 02.05.13
 * Time: 20:43
 * To change this template use File | Settings | File Templates.
 */
public class CArrowSelectFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    SimpleCursorAdapter adapter;
    Spinner spinner;
    LoaderManager loaderManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.arrow_select_fragment, null);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        view.findViewById(R.id.addArrow).setOnClickListener(this);
        view.findViewById(R.id.deleteArrow).setOnClickListener(this);
        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.spinner_child_2,
                null,
                new String[]{"name","description"},
                new int[]{R.id.text1,R.id.text2});
        spinner.setAdapter(adapter);
        loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return  CSQLiteOpenHelper.getCursorLoader(getActivity(), CSQLiteOpenHelper.TABLE_ARROWS);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.changeCursor(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())   {
            case R.id.addArrow:
            {
                ArrowAddDialog.newInstance().show(getFragmentManager().beginTransaction(), "dialog");
                break;
            }
            case R.id.deleteArrow:
            {
                CSQLiteOpenHelper.getHelper(getActivity()).deleteArrow(spinner.getSelectedItemId());
                loaderManager.restartLoader(0, null, this);
                break;
            }
        }
    }

    public void saveArrow(CArrow arrow) {
        CSQLiteOpenHelper.getHelper(getActivity()).addArrow(arrow);
        getLoaderManager().restartLoader(0, null, this);
    }

    public long getSelectedItemId()    {
        return spinner.getSelectedItemId();
    }

    private static class ArrowAddDialog extends DialogFragment {
        static ArrowAddDialog newInstance() {
            return new ArrowAddDialog();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity()).
                    setTitle("Новый тип стрел").
                    setView(getActivity().getLayoutInflater().inflate(R.layout.arrow_add_dialog,null)).
                    setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    }).
                    setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            CArrow arrow = new CArrow(
                                    ((EditText) ((AlertDialog)dialogInterface).findViewById(R.id.name)).getText().toString(),
                                    ((EditText) ((AlertDialog)dialogInterface).findViewById(R.id.description)).getText().toString(),
                                    Float.parseFloat(((EditText) ((AlertDialog)dialogInterface).findViewById(R.id.radius)).getText().toString()
                                    ));
                            ((StartActivity)getActivity()).saveArrow(arrow);
                            dismiss();
                        }
                    }).create();
        }

    }
}

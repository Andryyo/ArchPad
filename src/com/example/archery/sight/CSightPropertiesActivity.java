package com.example.archery.sight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.example.archery.MainActivity;
import com.example.archery.R;
import com.example.archery.database.CMySQLiteOpenHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 06.05.13
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
public class CSightPropertiesActivity extends Activity implements Spinner.OnItemSelectedListener, DialogInterface.OnDismissListener {

    private long currentSightId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sight_select);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.setTitle("Управление конфигурациями прицелов");
        getWindow().setAttributes(params);
        ((CSightSelectView)findViewById(R.id.sight_select_view)).setOnItemSelectedListener(this);
        ((CSightSelectView)findViewById(R.id.sight_select_view)).setSelection(
                PreferenceManager.getDefaultSharedPreferences(this).getLong("sightId",0));
    }

    public void onClick(View view)  {
        switch (view.getId())
        {
            case R.id.dec_y:
            {
                EditText editText = (EditText) findViewById(R.id.sight_y);
                if (editText.getText().toString().equals(""))
                    editText.setText("0.0");
                BigDecimal b = new BigDecimal(editText.getText().toString());
                b = b.subtract(new BigDecimal(0.1)).setScale(1,RoundingMode.HALF_UP);
                editText.setText(b.toString());
                break;
            }
            case R.id.dec_x:
            {
                EditText editText = (EditText) findViewById(R.id.sight_x);
                if (editText.getText().toString().equals(""))
                    editText.setText("0.0");
                BigDecimal b = new BigDecimal(editText.getText().toString());
                b = b.subtract(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP);
                editText.setText(b.toString());
                break;
            }
            case R.id.inc_y:
            {
                EditText editText = (EditText) findViewById(R.id.sight_y);
                if (editText.getText().toString().equals(""))
                    editText.setText("0.0");
                BigDecimal b = new BigDecimal(editText.getText().toString());
                b = b.add(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP);
                editText.setText(b.toString());
                break;
            }
            case R.id.inc_x:
            {
                EditText editText = (EditText) findViewById(R.id.sight_x);
                if (editText.getText().toString().equals(""))
                    editText.setText("0.0");
                BigDecimal b = new BigDecimal(editText.getText().toString());
                b = b.add(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP);
                editText.setText(b.toString());
                break;
            }
        }
    }

    public void addSight(View view)  {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Новая конфигурация прицела");
        builder.setView(inflater.inflate(R.layout.add_dialog, null));
        builder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                CMySQLiteOpenHelper.getHelper(getApplicationContext()).addSight(
                        ((EditText) ((AlertDialog) dialog).findViewById(R.id.text1)).getText().toString(),
                        ((EditText) ((AlertDialog) dialog).findViewById(R.id.text2)).getText().toString()
                );
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    public void deleteSight(View view)  {
        ((CSightSelectView)findViewById(R.id.sight_select_view)).deleteSigth();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
        if (currentSightId!=0)
            CMySQLiteOpenHelper.getHelper(this).updateSight(currentSightId,
                    ((EditText)findViewById(R.id.sight_x)).getText().toString(),
                    ((EditText)findViewById(R.id.sight_y)).getText().toString()
                    );
        currentSightId = id;
        String[] strings = CMySQLiteOpenHelper.getHelper(this).getSight(id);
        ((EditText)findViewById(R.id.sight_x)).setText(strings[0]);
        ((EditText)findViewById(R.id.sight_y)).setText(strings[1]);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        currentSightId = 0;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        ((CSightSelectView)findViewById(R.id.sight_select_view)).update();
    }

    @Override
    public void onPause()   {
        super.onPause();
        CMySQLiteOpenHelper.getHelper(this).updateSight(currentSightId,
                ((EditText)findViewById(R.id.sight_x)).getText().toString(),
                ((EditText)findViewById(R.id.sight_y)).getText().toString()
        );
    }

    @Override
    public void onBackPressed() {
        if (currentSightId!=0)
        {
            Intent intent = new Intent();
            intent.putExtra("sightId",currentSightId);
            setResult(MainActivity.SIGHT_REQUEST,intent);
            finish();
        }
        else
        {
            Toast toast = Toast.makeText(getBaseContext(),"Выберите или создайте конфигурацию прицела",3000);
            toast.show();
        }
    }

    @Override
    public void onDestroy() {
        ((CSightSelectView)findViewById(R.id.sight_select_view)).closeCursor();
        super.onDestroy();
    }

    /**
     * Created with IntelliJ IDEA.
     * User: Андрей
     * Date: 06.05.13
     * Time: 21:09
     * To change this template use File | Settings | File Templates.
     */
    public static class CSightSelectView extends Spinner {
        SimpleCursorAdapter adapter;

        public CSightSelectView(Context context, AttributeSet attrs) {
            super(context, attrs);
            adapter = new SimpleCursorAdapter(context,
                    R.layout.spinner_child_2,
                    CMySQLiteOpenHelper.getHelper(context).getSightsCursor(),
                    new String[]{"name","description"},
                    new int[]{R.id.text1,R.id.text2});
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
}
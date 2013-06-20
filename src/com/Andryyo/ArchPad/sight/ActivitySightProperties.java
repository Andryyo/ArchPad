package com.Andryyo.ArchPad.sight;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 06.05.13
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
public class ActivitySightProperties extends FragmentActivity implements Spinner.OnItemSelectedListener {

    private long currentSightId;
    private EditText editTextX;
    private EditText editTextY;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sight_properties);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.setTitle("Управление конфигурациями прицелов");
        getWindow().setAttributes(params);
        CSightSelectFragment fragment = (CSightSelectFragment)getSupportFragmentManager().findFragmentById(R.id.sight_select_fragment);
        fragment.setOnItemSelectedListener(this);
        fragment.setSelection(PreferenceManager.getDefaultSharedPreferences(this).getLong("sightId",0));
        editTextX = (EditText) findViewById(R.id.sight_x);
        editTextY = (EditText) findViewById(R.id.sight_y);
    }

    public void onClick(View view)  {
        switch (view.getId())
        {
            case R.id.dec_y:
            {
                if (editTextY.getText().toString().equals(""))
                    editTextY.setText("0.0");
                BigDecimal b = new BigDecimal(editTextY.getText().toString());
                b = b.subtract(new BigDecimal(0.1)).setScale(1,RoundingMode.HALF_UP);
                editTextY.setText(b.toString());
                break;
            }
            case R.id.dec_x:
            {
                if (editTextX.getText().toString().equals(""))
                    editTextX.setText("0.0");
                BigDecimal b = new BigDecimal(editTextX.getText().toString());
                b = b.subtract(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP);
                editTextX.setText(b.toString());
                break;
            }
            case R.id.inc_y:
            {
                if (editTextY.getText().toString().equals(""))
                    editTextY.setText("0.0");
                BigDecimal b = new BigDecimal(editTextY.getText().toString());
                b = b.add(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP);
                editTextY.setText(b.toString());
                break;
            }
            case R.id.inc_x:
            {
                if (editTextX.getText().toString().equals(""))
                    editTextX.setText("0.0");
                BigDecimal b = new BigDecimal(editTextX.getText().toString());
                b = b.add(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP);
                editTextX.setText(b.toString());
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
        if (currentSightId!=0)
            CSQLiteOpenHelper.getHelper(this).updateSight(currentSightId,
                    editTextX.getText().toString(),
                    editTextY.getText().toString()
                            );
        PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("sightId",id).commit();
        currentSightId = id;
        String[] strings = CSQLiteOpenHelper.getHelper(this).getSight(id);
        editTextX.setText(strings[0]);
        editTextY.setText(strings[1]);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        currentSightId = 0;
    }

    @Override
    public void onPause()   {
        super.onPause();
        CSQLiteOpenHelper.getHelper(this).updateSight(currentSightId,
                editTextX.getText().toString(),
                editTextY.getText().toString()
        );
    }

}
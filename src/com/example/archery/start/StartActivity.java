package com.example.archery.start;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.app.Activity;
import android.content.Intent;
import com.example.archery.CArrow;
import com.example.archery.MainActivity;
import com.example.archery.R;
import com.example.archery.database.CMySQLiteOpenHelper;
import com.example.archery.target.CRing;
import com.example.archery.target.CTarget;

import java.util.Vector;

public class StartActivity extends Activity implements OnItemSelectedListener,DialogInterface.OnDismissListener{
	
	public Spinner Number_of_series_spinner;
    public Spinner Arrows_in_series_spinner;
    public final static String NUMBER_OF_SERIES="com.example.Archery.NUMBER_OF_SERIES";
    public final static String ARROWS_IN_SERIES="com.example.Archery.ARROWS_IN_SERIES";
    public final static String TARGET_ID="com.example.Archery.TARGET_ID";
    public final static String ARROW_ID="com.example.Archery.ARROW_ID";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Number_of_series_spinner=(Spinner) findViewById(R.id.spinner1);
        Arrows_in_series_spinner=(Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> Number_of_series_adapter = ArrayAdapter.createFromResource
                (this, R.array.Number_of_series_array, android.R.layout.simple_spinner_item);
        Number_of_series_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Number_of_series_spinner.setAdapter(Number_of_series_adapter);
        ArrayAdapter<CharSequence> Arrows_in_series_adapter = ArrayAdapter.createFromResource
                (this, R.array.Arrows_in_series_array, android.R.layout.simple_spinner_item);
        Arrows_in_series_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Arrows_in_series_spinner.setAdapter(Arrows_in_series_adapter);
        Number_of_series_spinner.setOnItemSelectedListener(this);
        Arrows_in_series_spinner.setOnItemSelectedListener(this);
    }


    public void Start(View view)
    {
        if ((((CTargetSelectView) findViewById(R.id.targetSelectView)).getSelectedItemId()==AdapterView.INVALID_ROW_ID)||
            (((CArrowSelectView) findViewById(R.id.arrowSelectView)).getSelectedItemId()==AdapterView.INVALID_ROW_ID))
        {
            Toast.makeText(getBaseContext(),"Необходимо выбрать тип мишени и стрел",3000).show();
        }
        else
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(NUMBER_OF_SERIES, Integer.parseInt(Number_of_series_spinner.getSelectedItem().toString()));
            intent.putExtra(ARROWS_IN_SERIES, Integer.parseInt(Arrows_in_series_spinner.getSelectedItem().toString()));
            CTargetSelectView targetSelectView = (CTargetSelectView) findViewById(R.id.targetSelectView);
            intent.putExtra(TARGET_ID,targetSelectView.getSelectedItemId());
            CArrowSelectView arrowSelectView = (CArrowSelectView) findViewById(R.id.arrowSelectView);
            intent.putExtra(ARROW_ID,arrowSelectView.getSelectedItemId());
    	    startActivity(intent);
        }
    }

    @Override
    public void onDestroy()    {
        ((CTargetSelectView) findViewById(R.id.targetSelectView)).closeCursor();
        ((CArrowSelectView) findViewById(R.id.arrowSelectView)).closeCursor();
        CMySQLiteOpenHelper.getHelper(this).close();
        super.onDestroy();
    }

    @Override
    public void onResume()    {
        super.onResume();
        ((CTargetSelectView) findViewById(R.id.targetSelectView)).update();
    }

    public void onClick(View view)  {
        switch (view.getId())
        {
            case R.id.addTarget: {
                Intent intent = new Intent(this, TargetAddActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.deleteTarget:  {
                ((CTargetSelectView) findViewById(R.id.targetSelectView)).deleteSelectedTarget();
                break;
            }
            case R.id.addArrow: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                builder.setTitle("Новый комплект стрел");
                builder.setView(inflater.inflate(R.layout.arrow_add_dialog, null));
                builder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        CArrow arrow = new CArrow(
                                ((EditText) ((AlertDialog) dialog).findViewById(R.id.name)).getText().toString(),
                                ((EditText) ((AlertDialog) dialog).findViewById(R.id.description)).getText().toString(),
                                Float.parseFloat(((EditText) ((AlertDialog) dialog).findViewById(R.id.radius)).getText().toString())
                                );
                        CMySQLiteOpenHelper.getHelper(getApplicationContext()).addArrow(arrow);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setOnDismissListener(this);
                dialog.show();
                break;
            }
            case R.id.deleteArrow:  {
                ((CArrowSelectView) findViewById(R.id.arrowSelectView)).deleteSelectedArrow();
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        ((CArrowSelectView) findViewById(R.id.arrowSelectView)).update();
    }
}

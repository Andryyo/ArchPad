package com.example.archery;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.app.Activity;
import android.content.Intent;
import com.example.archery.database.CMySQLiteOpenHelper;
import com.example.archery.start.CArrowSelectView;
import com.example.archery.start.CTargetSelectView;
import com.example.archery.target.CRing;
import com.example.archery.target.CTarget;

import java.util.Vector;

public class StartActivity extends Activity implements OnItemSelectedListener{
	
	public Spinner Number_of_series_spinner;
    public int Number_of_series;
    public int Arrows_in_series;
    public int targetId = 0;
    public Spinner Arrows_in_series_spinner;
    public final static String NUMBER_OF_SERIES="com.example.Archery.NUMBER_OF_SERIES";
    public final static String ARROWS_IN_SERIES="com.example.Archery.ARROWS_IN_SERIES";
    public final static String TARGET_ID="com.example.Archery.TARGET_ID";
    public final static String ARROW_ID="com.example.Archery.ARROW_ID";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vector<CRing> rings = new Vector<CRing>();
        rings.add(new CRing(10,1, Color.YELLOW));
        rings.add(new CRing(9,2,Color.YELLOW));
        rings.add(new CRing(8,3,Color.RED));
        rings.add(new CRing(7,4,Color.RED));
        rings.add(new CRing(6,5,Color.BLUE));
        rings.add(new CRing(5,6,Color.BLUE));
        rings.add(new CRing(0,7,Color.WHITE));
        CTarget target = new CTarget("default_target",rings,30,0);
        CMySQLiteOpenHelper helper = CMySQLiteOpenHelper.getHelper(this);
        //helper.addTarget(target);
        //helper.addTarget(target);
        //helper.addArrow(new CArrow("Алюминий","",0.05f,0));
        //helper.addArrow(new CArrow("Карбон","а тут описание",0.01f,0));
        setContentView(R.layout.activity_start);
        Number_of_series_spinner=(Spinner) findViewById(R.id.spinner1);
        Arrows_in_series_spinner=(Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> Number_of_series_adapter = ArrayAdapter.createFromResource(this, R.array.Number_of_series_array, android.R.layout.simple_spinner_item);
        Number_of_series_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Number_of_series_spinner.setAdapter(Number_of_series_adapter);
        ArrayAdapter<CharSequence> Arrows_in_series_adapter = ArrayAdapter.createFromResource(this, R.array.Arrows_in_series_array, android.R.layout.simple_spinner_item);
        Arrows_in_series_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Arrows_in_series_spinner.setAdapter(Arrows_in_series_adapter);
        Number_of_series_spinner.setOnItemSelectedListener(this);
        Arrows_in_series_spinner.setOnItemSelectedListener(this);
    }
            
    public void Start(View view)
    {
    	Intent intent = new Intent(this, MainActivity.class);
    	intent.putExtra(NUMBER_OF_SERIES, Number_of_series);
    	intent.putExtra(ARROWS_IN_SERIES, Arrows_in_series);
        CTargetSelectView targetSelectView = (CTargetSelectView) findViewById(R.id.targetPreivew);
        //CTargetPreview targetPreview = (CTargetPreview) targetSelectView.getSelectedView();
        //long id = targetPreview.getTargetId();
        intent.putExtra(TARGET_ID,targetSelectView.getSelectedItemId());
        CArrowSelectView arrowSelectView = (CArrowSelectView) findViewById(R.id.arrowSelectView);
        intent.putExtra(ARROW_ID,arrowSelectView.getSelectedItemId());
    	startActivity(intent);
    }

    @Override
    public void onDestroy()    {
        ((CTargetSelectView) findViewById(R.id.targetPreivew)).closeCursor();
        ((CArrowSelectView) findViewById(R.id.arrowSelectView)).closeCursor();
        CMySQLiteOpenHelper.getHelper(this).close();
        super.onDestroy();
    }

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3)
		{
		switch (arg0.getId())
		{
			case R.id.spinner1:
			{
				Number_of_series=Integer.parseInt(arg0.getItemAtPosition(arg2).toString());
			break;	
			}
			case R.id.spinner2:
			{
				Arrows_in_series=Integer.parseInt(arg0.getItemAtPosition(arg2).toString());
			break;
			}
		}
		}

	public void onNothingSelected(AdapterView<?> arg0) {
		}
}

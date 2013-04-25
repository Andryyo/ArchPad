package com.example.archery;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.app.Activity;
import android.content.Intent;

public class StartActivity extends Activity implements OnItemSelectedListener{
	
	public Spinner Number_of_series_spinner;
    public int Number_of_series;
    public int Arrows_in_series;
    public Spinner Arrows_in_series_spinner;
    public final static String NUMBER_OF_SERIES="com.example.Archery.NUMBER_OF_SERIES";
    public final static String ARROWS_IN_SERIES="com.example.Archery.ARROWS_IN_SERIES";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    	startActivity(intent);
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

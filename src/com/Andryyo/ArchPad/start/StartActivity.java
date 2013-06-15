package com.Andryyo.ArchPad.start;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import com.Andryyo.ArchPad.CArrow;
import com.Andryyo.ArchPad.MainActivity;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.target.CTarget;

public class StartActivity extends FragmentActivity{
	
	public Spinner Number_of_series_spinner;
    public Spinner Arrows_in_series_spinner;
    public final static String NUMBER_OF_SERIES="com.Andryyo.Archery.NUMBER_OF_SERIES";
    public final static String ARROWS_IN_SERIES="com.Andryyo.Archery.ARROWS_IN_SERIES";
    public final static String TARGET_ID="com.Andryyo.Archery.TARGET_ID";
    public final static String ARROW_ID="com.Andryyo.Archery.ARROW_ID";
    
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
    }


    public void Start(View view)
    {
        long targetId = ((CTargetSelectFragment)getSupportFragmentManager().
                findFragmentById(R.id.targetSelectFragment)).getSelectedItemId();
        long arrowId =  ((CArrowSelectFragment)getSupportFragmentManager().
                findFragmentById(R.id.arrowSelectFragment)).getSelectedItemId();
        if ((targetId==AdapterView.INVALID_ROW_ID)||
            (arrowId==AdapterView.INVALID_ROW_ID))
        {
            Toast.makeText(getBaseContext(),"Необходимо выбрать тип мишени и стрел",3000).show();
        }
        else
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(NUMBER_OF_SERIES, Integer.parseInt(Number_of_series_spinner.getSelectedItem().toString()));
            intent.putExtra(ARROWS_IN_SERIES, Integer.parseInt(Arrows_in_series_spinner.getSelectedItem().toString()));
            intent.putExtra(TARGET_ID,targetId);
            intent.putExtra(ARROW_ID,arrowId);
    	    startActivity(intent);
        }
    }

    public void saveRing(float ringWidth, int points, int color)    {
        ((CTargetSelectFragment.CTargetCreateDialog)getSupportFragmentManager().
                findFragmentByTag("targetCreateDialog")).saveRing(ringWidth, points, color);
    }

    public void saveTarget(CTarget target)  {
        ((CTargetSelectFragment)getSupportFragmentManager().
                findFragmentById(R.id.targetSelectFragment)).saveTarget(target);
    }
}

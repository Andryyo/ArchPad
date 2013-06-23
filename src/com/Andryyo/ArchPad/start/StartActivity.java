package com.Andryyo.ArchPad.start;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import com.Andryyo.ArchPad.CArrow;
import com.Andryyo.ArchPad.MainActivity;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.archeryView.CArcheryFragment;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;
import com.Andryyo.ArchPad.target.CTarget;

public class StartActivity extends FragmentActivity{
	
	public Spinner Number_of_ends_spinner;
    public Spinner Arrows_in_ends_spinner;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("firstLaunch", true))
        {
            CSQLiteOpenHelper.getHelper(this).init();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("firstLaunch", false).commit();
        }
        Number_of_ends_spinner=(Spinner) findViewById(R.id.spinner1);
        Arrows_in_ends_spinner=(Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> Number_of_ends_adapter = ArrayAdapter.createFromResource
                (this, R.array.Number_of_ends_array, android.R.layout.simple_spinner_item);
        Number_of_ends_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Number_of_ends_spinner.setAdapter(Number_of_ends_adapter);
        ArrayAdapter<CharSequence> Arrows_in_ends_adapter = ArrayAdapter.createFromResource
                (this, R.array.Arrows_in_end_array, android.R.layout.simple_spinner_item);
        Arrows_in_ends_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Arrows_in_ends_spinner.setAdapter(Arrows_in_ends_adapter);
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
            intent.putExtra(CArcheryFragment.NUMBER_OF_ROUNDS, Integer.parseInt(Number_of_ends_spinner.getSelectedItem().toString()));
            intent.putExtra(CArcheryFragment.ARROWS_IN_ROUND, Integer.parseInt(Arrows_in_ends_spinner.getSelectedItem().toString()));
            intent.putExtra(CArcheryFragment.TARGET_ID,targetId);
            intent.putExtra(CArcheryFragment.ARROW_ID,arrowId);
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

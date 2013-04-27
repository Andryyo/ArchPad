package com.example.archery;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.example.archery.archeryView.CArcheryView;
import com.example.archery.statistics.StatisticsActivity;

public class MainActivity extends Activity {
    public CArcheryView mArcheryView = null;
    public static Vibrator vibrator;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout content=(RelativeLayout) getLayoutInflater().inflate(R.layout.activity_start, null);
        setContentView(content);
        Intent intent = getIntent();
        mArcheryView = new CArcheryView(this, intent.getIntExtra(StartActivity.NUMBER_OF_SERIES,1)
				  ,intent.getIntExtra(StartActivity.ARROWS_IN_SERIES,1));
        mArcheryView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        content.addView(mArcheryView);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId())
    	{
    	case R.id.show_statistics:
    	{
    		Intent intent = new Intent(this, StatisticsActivity.class);
    		startActivity(intent);
    		break;
    	}
    	case R.id.info:
    	{
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Information" );
            dialog.setMessage("Created by Andryyo.\r\nEmail : Androyyo@ukr.net\r\n2012");
            dialog.setPositiveButton("Ок", new OnClickListener() {
    									       	public void onClick(DialogInterface dialog, int arg1) {
    									       		dialog.cancel();
    									       	}
    								});
    		dialog.show();
    		break;
    	}
        case R.id.save:
            {
             mArcheryView.endCurrentDistance();
             Intent intent=new Intent(this, StartActivity.class);
             startActivity(intent);
             mArcheryView.invalidate();
             break;
            }
    	}
    	return true;
    }

    @Override
    public void onPause()   {
        super.onPause();
        mArcheryView.saveDistances();
        vibrator.cancel();
    }

    @Override
    public void onResume()  {
        super.onResume();
        mArcheryView.loadDistances();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event)   {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            mArcheryView.endCurrentDistance();
            finish();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        mArcheryView.deleteLastShot();
        mArcheryView.invalidate();
    }

    @Override
    public void onStop()    {
        mArcheryView.endCurrentDistance();
        super.onStop();
    }
}

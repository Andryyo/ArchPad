package com.Andryyo.ArchPad;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import com.Andryyo.ArchPad.archeryView.CArcheryView;
import com.Andryyo.ArchPad.start.StartActivity;

public class MainActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event)   {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            //mArcheryView.endCurrentDistance();
            //getActivity().finish();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //deleteLastShot();
        //postInvalidate();
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
                    Intent intent = getIntent();
                    return new CArcheryView(getApplication(),intent.getIntExtra(StartActivity.NUMBER_OF_SERIES,1)
                            ,intent.getIntExtra(StartActivity.ARROWS_IN_SERIES,1),intent.getLongExtra(StartActivity.TARGET_ID,1),
                            intent.getLongExtra(StartActivity.ARROW_ID,1));
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}

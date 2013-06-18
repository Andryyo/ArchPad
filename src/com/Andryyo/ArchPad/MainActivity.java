package com.Andryyo.ArchPad;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import com.Andryyo.ArchPad.archeryView.CArcheryFragment;
import com.Andryyo.ArchPad.start.StartActivity;
import com.Andryyo.ArchPad.statistics.CStatisticsFragment;
import com.Andryyo.ArchPad.statistics.IOnUpdateListener;

import java.util.Vector;

public class MainActivity extends FragmentActivity implements IOnUpdateListener{

    ViewPager pager;
    Vector<Fragment> fragments = new Vector<Fragment>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        fragments.add(new CNotesFragment());
        fragments.add(new CArcheryFragment(getApplication()
                ,intent.getIntExtra(StartActivity.NUMBER_OF_SERIES,1)
                ,intent.getIntExtra(StartActivity.ARROWS_IN_SERIES,1)
                ,intent.getLongExtra(StartActivity.TARGET_ID,1)
                ,intent.getLongExtra(StartActivity.ARROW_ID,1)));
        fragments.add(new CStatisticsFragment());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        ((CArcheryFragment)fragments.elementAt(1)).setOnUpdateListener(this);
        pager.setCurrentItem(1,false);
    }

    @Override
    public void update() {
        ((CStatisticsFragment)fragments.elementAt(2)).update();
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.elementAt(i);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}

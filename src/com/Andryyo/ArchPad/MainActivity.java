package com.Andryyo.ArchPad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.Andryyo.ArchPad.archeryView.CArcheryFragment;
import com.Andryyo.ArchPad.start.StartActivity;
import com.Andryyo.ArchPad.statistics.CStatisticsFragment;
import com.Andryyo.ArchPad.statistics.IOnUpdateListener;

import java.util.Vector;

public class MainActivity extends FragmentActivity implements DialogInterface.OnClickListener{

    ViewPager pager;
    PagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new PagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1,false);
        if (savedInstanceState!=null)   {
            CArcheryFragment fragment = (CArcheryFragment)getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:"+R.id.pager+":1");
            fragment.setOnTouchListener(adapter);
            fragment.setOnUpdateListener(adapter);
        }
    }

    @Override
    public void onBackPressed()    {
       new AlertDialog.Builder(this)
               .setMessage("Вы уверены, что хотите завершить раунд?")
               .setPositiveButton("Да", this)
               .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                   }
               })
               .create().show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        super.onBackPressed();
    }

    private class PagerAdapter extends FragmentPagerAdapter implements IOnUpdateListener, View.OnTouchListener {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void update()    {
            ((CStatisticsFragment)getSupportFragmentManager().
                    findFragmentByTag("android:switcher:"+R.id.pager+":2")).update();
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i)  {
                case 0:
                    fragment = new CNotesFragment();
                    break;
                case 1:
                    fragment = new CArcheryFragment(getIntent());
                    ((CArcheryFragment)fragment).setOnTouchListener(this);
                    ((CArcheryFragment)fragment).setOnUpdateListener(this);
                    break;
                case 2:
                    fragment = new CStatisticsFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (view.getTag()=="targetView")    {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                        pager.requestDisallowInterceptTouchEvent(true);
                if (event.getAction()==MotionEvent.ACTION_UP)
                    pager.requestDisallowInterceptTouchEvent(false);

            }
            return false;
        }
    }
}

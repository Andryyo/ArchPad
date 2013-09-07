package com.Andryyo.ArchPad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import com.Andryyo.ArchPad.archeryFragment.CArcheryFragment;
import com.Andryyo.ArchPad.archeryFragment.CRoundTemplate;
import com.Andryyo.ArchPad.note.CNotesFragment;
import com.Andryyo.ArchPad.sight.СSightPropertiesFragment;
import com.Andryyo.ArchPad.start.CStartFragment;
import com.Andryyo.ArchPad.start.IOnFragmentSwapRequiredListener;
import com.Andryyo.ArchPad.statistics.CStatisticsFragment;
import com.Andryyo.ArchPad.statistics.IOnUpdateListener;

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
            //TODO: Костыль, кстати. Тут потому-что при пересоздании Активити теряется ArcheryFragment
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:"+R.id.pager+":1");
            if (fragment instanceof CArcheryFragment) {
                ((CArcheryFragment)fragment).setOnTouchListener(adapter);
                ((CArcheryFragment)fragment).setOnUpdateListener(adapter);
                ((CArcheryFragment)fragment).setOnFragmentSwapRequiredListener(adapter);
            }
            if (fragment instanceof CStartFragment) {
                ((CStartFragment)fragment).setOnFragmentSwapListener(adapter);
                ((CStartFragment)fragment).setOnTouchListener(adapter);
            }
            adapter.setArcheryFragment(fragment);
        }
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
            case R.id.info:
            {
                new AlertDialog.Builder(this)
                        .setTitle("Information" )
                        .setMessage("Created by Andryyo.\r\nEmail : Andryyo@ukr.net\r\n2012")
                        .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.cancel();
                            }
                        })
                        .create().show();
                return true;
            }
            case R.id.sight:
            {
                Intent intent = new Intent(this, СSightPropertiesFragment.class);
                startActivity(intent);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        super.onBackPressed();
    }

    private class PagerAdapter extends FragmentPagerAdapter implements IOnUpdateListener,IOnFragmentSwapRequiredListener, View.OnTouchListener {

        Fragment archeryFragment;
        boolean updateRequired;

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
            switch (i)  {
                case 0:
                    return new CNotesFragment();
                case 1:
                    if (archeryFragment == null)    {
                        archeryFragment = new CStartFragment();
                        ((CStartFragment)archeryFragment).setOnFragmentSwapListener(this);
                        ((CStartFragment)archeryFragment).setOnTouchListener(this);
                    };
                    return archeryFragment;
                case 2:
                    return new CStatisticsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public int getItemPosition(Object object)
        {
            if (updateRequired&&(object instanceof CArcheryFragment||object instanceof CStartFragment)) {
                updateRequired = false;
                return POSITION_NONE;
            }
            return POSITION_UNCHANGED;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if ((view.getTag()=="targetView")||(view.getTag()=="targetSelectView"))    {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                        pager.requestDisallowInterceptTouchEvent(true);
                if (event.getAction()==MotionEvent.ACTION_UP)
                    pager.requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }

        @Override
        public void swapToArcheryFragment(CRoundTemplate template) {
            getSupportFragmentManager().beginTransaction().remove(archeryFragment).commit();
            archeryFragment = new CArcheryFragment(template);
            ((CArcheryFragment)archeryFragment).setOnTouchListener(this);
            ((CArcheryFragment)archeryFragment).setOnUpdateListener(this);
            ((CArcheryFragment)archeryFragment).setOnFragmentSwapRequiredListener(this);
            updateRequired = true;
            notifyDataSetChanged();
        }

        @Override
        public void swapToStartFragment() {
            getSupportFragmentManager().beginTransaction().remove(archeryFragment).commit();
            archeryFragment = null;
            updateRequired = true;
            notifyDataSetChanged();
        }

        public void setArcheryFragment(Fragment fragment)   {
            this.archeryFragment = fragment;
        }
    }
}

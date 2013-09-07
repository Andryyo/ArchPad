package com.Andryyo.ArchPad.start;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.*;
import android.support.v4.widget.CursorAdapter;
import android.text.AndroidCharacter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.SimpleCursorAdapter;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.archeryFragment.CDistanceTemplate;
import com.Andryyo.ArchPad.archeryFragment.CRoundTemplate;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;
import com.Andryyo.ArchPad.target.CTarget;

public class CStartFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private CRoundSelectView roundSelectView;
    private IOnFragmentSwapRequiredListener listener;
    private View.OnTouchListener onTouchListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("firstLaunch", true))
        {
            CSQLiteOpenHelper.getHelper(context).init();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("firstLaunch", false).commit();
        }
        LinearLayout parentView = new LinearLayout(context);
        parentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        parentView.setOrientation(LinearLayout.VERTICAL);
        roundSelectView = new CRoundSelectView(context, getLoaderManager());
        roundSelectView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        parentView.addView(roundSelectView);
        Button button = new Button(context);
        button.setLayoutParams
                (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText("Начать");
        button.setOnClickListener(this);
        parentView.addView(button);
        return parentView;
    }

    @Override
    public void onClick(View view) {
            //listener.swapToArcheryFragment(template);
    }

    public void setOnFragmentSwapListener(IOnFragmentSwapRequiredListener listener) {
        this.listener = listener;
    }

    public void setOnTouchListener(View.OnTouchListener listener)   {
        onTouchListener = listener;
    }

    public void saveTarget(CTarget target) {
        //targetSelectView.saveTarget(target);
    }
}

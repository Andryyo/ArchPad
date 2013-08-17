package com.Andryyo.ArchPad.archeryFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.start.IOnFragmentSwapRequiredListener;
import com.Andryyo.ArchPad.statistics.IOnUpdateListener;
import com.Andryyo.ArchPad.target.CEditableTargetView;

public class CArcheryFragment extends Fragment implements IOnShotAddListener{
    public static Vibrator vibrator;
    public static final String ROUND_TEMPLATE = "com.Andryyo.ArchPad.template";

    private CRound currentRound = null;
    private CRound previousRound = null;
    private Context context;
    private View.OnTouchListener onTouchListener;
    private IOnFragmentSwapRequiredListener onFragmentSwapRequiredListener;
    private CEditableTargetView mTargetView;
    private IOnUpdateListener onUpdateListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    public CArcheryFragment() {};

    public CArcheryFragment(CRoundTemplate template)    {
        currentRound = template.createRound(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        if (savedInstanceState!=null)
            restoreDistance(savedInstanceState);
        setHasOptionsMenu(true);
        LinearLayout parentLayout = new LinearLayout(context);
        parentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.9f));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        mTargetView = new CEditableTargetView(context);
        mTargetView.setOnShotAddListener(this);
        mTargetView.setOnTouchListener(onTouchListener);
        mTargetView.setTarget(currentRound.getCurrentTargetId());
        mTargetView.setArrow(currentRound.getArrowId());
        mTargetView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 15f));
        mTargetView.setTag("targetView");
        layout.addView(mTargetView);
        СEndsCounterView mEndsCounterView = new СEndsCounterView(context,this);
        mEndsCounterView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT,1f));
        layout.addView(mEndsCounterView);
        parentLayout.addView(layout);
        CCurrentEndView mCurrentEndsView = new CCurrentEndView(context,this);
        mCurrentEndsView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.1f));
        parentLayout.addView(mCurrentEndsView);
        return parentLayout;
    }

    public void onSaveInstanceState(Bundle outState)    {
        super.onSaveInstanceState(outState);
        if (currentRound!=null)
            outState.putSerializable("savedRound", currentRound);
    }

    private void restoreDistance(Bundle savedInstanceState) {
        currentRound = (CRound) savedInstanceState.getSerializable("savedRound");
        //mTargetView.setDistance(currentRound.getCurrentDistance());
    }

    public void setOnUpdateListener(IOnUpdateListener listener)  {
        onUpdateListener = listener;
    }

    public void setOnTouchListener(View.OnTouchListener listener)   {
        onTouchListener = listener;
        if (mTargetView!=null)
            mTargetView.setOnTouchListener(listener);
    }

    //TODO:добавить удаление последнего выстрела
	/*public void deleteLastShot()    {
        if (currentDistance!=null)
		    currentDistance.deleteLastShot();
	} */

    @Override
    public void addShot(CShot shot) {
        if (!currentRound.addShot(shot))    {
            onFragmentSwapRequiredListener.startStartFragment();
            onUpdateListener.update();
        }
        else    {
            mTargetView.setDistance(currentRound.getCurrentDistance());
            getView().invalidate();
        }
    }

    public CDistance getCurrentDistance()   {
        return currentRound.getCurrentDistance();
    }

    @Override
    public void onPause()   {
        super.onPause();
        vibrator.cancel();
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        currentRound.saveCurrentDistance();
    } */

    @Override
    public void onResume()  {
        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        super.onResume();
    }

    public void setOnFragmentSwapRequiredListener(IOnFragmentSwapRequiredListener onFragmentSwapRequiredListener) {
        this.onFragmentSwapRequiredListener = onFragmentSwapRequiredListener;
    }
}

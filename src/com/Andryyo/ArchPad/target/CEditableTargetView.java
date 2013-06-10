package com.Andryyo.ArchPad.target;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.archeryView.CDistance;
import com.Andryyo.ArchPad.archeryView.IOnShotAddListener;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 10.06.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */

public class CEditableTargetView extends CZoomableTargetView {

    private IOnShotAddListener listener;

    public CEditableTargetView(Context context, CDistance distance) {
        super(context, distance);
    }

    public CEditableTargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CEditableTargetView(Context context) {
        super(context);
    }

    public void setOnShotAddListener(IOnShotAddListener listener)   {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        super.onTouchEvent(me);
        if (me.getAction()==MotionEvent.ACTION_UP)
        {
            listener.addShot(new CShot(getTarget().rings, me.getX() / getCenter() - 1, me.getY() / getCenter() - 1,
                getArrowRadius()));
            cancelDrawSightMark();
        }
        if (me.getAction()==MotionEvent.ACTION_MOVE)
        {
            drawSightMark(me.getX(), me.getY());
        }
        return true;
    }


}

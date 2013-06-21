package com.Andryyo.ArchPad.target;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 09.06.13
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */
public class CTargetView extends View{

    private Context context;
    private CTarget target = null;
    private int center;

    public CTargetView(Context context, long _id)    {
        super(context);
        setTarget(_id);
        this.context = context;
    }

    public CTargetView(Context context, CTarget target)    {
        super(context);
        setTarget(target);
        this.context = context;
    }

    public CTargetView(Context context)    {
        super(context);
        this.context = context;
    }

    public CTargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setTarget(long _id) {
        target = CSQLiteOpenHelper.getHelper(context).getTarget(_id);
        if (target!=null)
            target.setDrawingRadius(center);
    }

    public void setTarget(CTarget target)   {
        this.target = target;
        if (target!=null)
            target.setDrawingRadius(center);
    }

    public CTarget getTarget() {
        return target;
    }

    public float getRealRadius()    {
        return target.realRadius;
    }

    public int getCenter()  {
        return center;
    }

    @Override
    public void onMeasure(int w, int h) {
        center = Math.min(MeasureSpec.getSize(w),MeasureSpec.getSize(h))/2;
        this.setMeasuredDimension(center*2, center*2);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (target!=null)
            target.setDrawingRadius(center);
    }

    @Override
    public void onDraw(Canvas canvas)   {
        if (target!=null)
            target.draw(canvas);
    }
}

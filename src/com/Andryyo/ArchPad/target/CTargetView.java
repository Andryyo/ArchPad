package com.Andryyo.ArchPad.target;

import android.content.Context;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import com.Andryyo.ArchPad.CArrow;
import com.Andryyo.ArchPad.archeryView.CArcheryView;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.archeryView.CDistance;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 23.04.13
 * Time: 21:00
 * To change this template use File | Settings | File Templates.
 */
public class CTargetView extends View {
    Context context;
    CTarget target;
    CArrow arrow;
    CDistance distance;
    int maxr;
    int screenWidth;
    int screenHeight;
    boolean haveZoom;
    float zoom = 0.1f;
    float x;
    float y;
    Rect zoomDestRect;
    Rect zoomSrcRect;
    Paint arrowPaint = new Paint();
    CArcheryView mArcheryView = null;
    boolean IsEditable;

    public CTargetView(Context context,CArcheryView mArcheryView) {
        super(context);
        this.context = context;
        this.mArcheryView = mArcheryView;
        IsEditable = true;
        CSQLiteOpenHelper helper = CSQLiteOpenHelper.getHelper(context);
        target = helper.getTarget(mArcheryView.getCurrentDistance().targetId);
        arrow = helper.getArrow(mArcheryView.getCurrentDistance().arrowId);
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setColor(Color.GREEN);
    }

    public CTargetView(Context context,long distanceId) {
        super(context);
        this.context = context;
        IsEditable = false;
        CSQLiteOpenHelper helper = CSQLiteOpenHelper.getHelper(context);
        distance = helper.getDistance(distanceId);
        target = helper.getTarget(distance.targetId);
        arrow = helper.getArrow(distance.arrowId);
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setColor(Color.GREEN);
    }

    @Override
    public void onMeasure(int w, int h) {
        maxr = Math.min(MeasureSpec.getSize(w),MeasureSpec.getSize(h))/2;
        screenWidth = MeasureSpec.getSize(w);
        screenHeight = MeasureSpec.getSize(h);
        this.setMeasuredDimension(maxr*2, maxr*2);
    }

    @Override
    public void onDraw(Canvas screenCanvas)
    {
        if (target == null)
            return;
        Bitmap bitmap = Bitmap.createBitmap(maxr*2,maxr*2,Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        target.draw(canvas, maxr, maxr);
        if (IsEditable)
            distance = mArcheryView.getCurrentDistance();
        for (Vector<CShot> series : distance.series)
            for (CShot shot : series)
                canvas.drawCircle((shot.x+1)*maxr,(shot.y+1)*maxr,arrow.radius*maxr,arrowPaint);
        if (haveZoom)
        {
            if (IsEditable)
            {
                canvas.drawPoint(x,y,arrowPaint);
                canvas.drawCircle(x,y,arrow.radius*maxr,arrowPaint);
            }
                canvas.drawBitmap(bitmap,zoomSrcRect,zoomDestRect,null);
        }
        screenCanvas.drawBitmap(bitmap,0,0,null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
	{
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            haveZoom = true;
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            x = (int) event.getX();
            y = (int) event.getY();
            if (x>maxr)
                zoomDestRect = new Rect(0,0, (int) (maxr*4/5), (int) (maxr*4/5));
            else
                zoomDestRect = new Rect(maxr*6/5,0,maxr*2,maxr*4/5);
            int left = (int) (x - zoom*maxr);
            int top = (int) (y - zoom*maxr);
            int right = (int) (x + zoom*maxr);
            int bottom = (int) (y + zoom*maxr);
            zoomSrcRect = new Rect(left,top,right,bottom);
            invalidate();
            return true;
        }
		if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if (IsEditable)
            {
                mArcheryView.addShot(new CShot(target.rings, x / maxr - 1, y / maxr - 1,
                        arrow.radius));
                //MainActivity.vibrator.vibrate(100);
                //mArcheryView.invalidate();
            }
                invalidate();
                haveZoom = false;
                return true;
        }
		return false;
	}
}
package com.Andryyo.ArchPad.target;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.Andryyo.ArchPad.CArrow;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.archeryFragment.CDistance;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 10.06.13
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
public class CZoomableTargetView extends CTargetView{

    private CDistance distance;
    private CArrow arrow;
    private CSQLiteOpenHelper helper;
    public static Paint arrowPaint = new Paint();
    private Rect zoomDestRect;
    private Rect zoomSrcRect;
    private boolean haveZoom;
    private boolean haveSightMark = false;
    private float sightMarkX;
    private float sightMarkY;
    private static float zoom = 0.25f;

    public CZoomableTargetView(Context context, CDistance distance) {
        super(context, distance.targetId);
        this.distance = distance;
        arrow = helper.getArrow(distance.arrowId);
        arrowPaint.setColor(Color.GREEN);
        arrowPaint.setStyle(Paint.Style.STROKE);
    }

    public CZoomableTargetView(Context context, long distanceId) {
        super(context);
        setDistance(CSQLiteOpenHelper.getHelper(context).getDistance(distanceId));
        arrowPaint.setColor(Color.GREEN);
        arrowPaint.setStyle(Paint.Style.STROKE);
    }

    public CZoomableTargetView(Context context) {
        super(context);
        arrowPaint.setColor(Color.GREEN);
        arrowPaint.setStyle(Paint.Style.STROKE);
    }

    public CZoomableTargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        arrowPaint.setColor(Color.GREEN);
        arrowPaint.setStyle(Paint.Style.STROKE);
    }

    public void setDistance(CDistance distance) {
        this.distance = distance;
        if (distance!=null)
        {
            setTarget(distance.targetId);
            setArrow(distance.arrowId);
        }
    }

    public void setArrow(long id)   {
        this.arrow = CSQLiteOpenHelper.getHelper(getContext()).getArrow(id);
    }

    public float getArrowRadius()    {
        return arrow.radius;
    }

    public boolean haveZoom()   {
        return haveZoom;
    }

    @Override
    public void onDraw(Canvas canvas)    {
        try {
        Bitmap bitmap = Bitmap.createBitmap(getCenter()*2,getCenter()*2,Bitmap.Config.RGB_565);
        Canvas buf = new Canvas(bitmap);
        super.onDraw(buf);
        if (distance!=null)
            for (Vector<CShot> shots : distance.ends)
                for (CShot shot : shots)
                    getTarget().drawShot(buf, arrow.radius, arrowPaint, shot);
        if ((haveZoom)&&(zoomSrcRect!=null))
        {
            if (haveSightMark)
            {
                drawSightMark(buf);
            }
            buf.drawBitmap(bitmap,zoomSrcRect,zoomDestRect,null);
        }
        canvas.drawBitmap(bitmap, 0, 0, null);
        } catch (NullPointerException e)    {
            e.printStackTrace();
        }
    }

    public void drawSightMark(Canvas buf) {
        buf.drawPoint(sightMarkX,sightMarkY,arrowPaint);
        buf.drawCircle(sightMarkX,sightMarkY,arrow.radius/getRealRadius()*getCenter(),arrowPaint);
    }

    public void beginDrawSightMark(float x, float y) {
        haveSightMark = true;
        sightMarkX = x;
        sightMarkY = y;
    }

    public void cancelDrawSightMark()   {
        haveSightMark = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            haveZoom = true;
            int left = (int) (x - zoom*getCenter());
            int top = (int) (y - zoom*getCenter());
            int right = (int) (x + zoom*getCenter());
            int bottom = (int) (y + zoom*getCenter());
            zoomSrcRect = new Rect(left,top,right,bottom);
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            if (x>getCenter())
                zoomDestRect = new Rect(0,0, getCenter()*4/5, getCenter()*4/5);
            else
                zoomDestRect = new Rect(getCenter()*6/5,0,getCenter()*2,getCenter()*4/5);
            int left = (int) (x - zoom*getCenter());
            int top = (int) (y - zoom*getCenter());
            int right = (int) (x + zoom*getCenter());
            int bottom = (int) (y + zoom*getCenter());
            zoomSrcRect = new Rect(left,top,right,bottom);
            invalidate();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            invalidate();
            haveZoom = false;
            return true;
        }
        return false;
    }
}

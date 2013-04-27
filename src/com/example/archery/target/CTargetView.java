package com.example.archery.target;

import android.content.Context;

import android.graphics.*;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.example.archery.MainActivity;
import com.example.archery.archeryView.CArcheryView;
import com.example.archery.CShot;
import com.example.archery.archeryView.CDistance;

import java.io.ObjectInputStream;

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
    CArcheryView mArcheryView;
    CDistance distance;

    public CTargetView(Context context,CArcheryView mArcheryView) {
        super(context);
        this.context = context;
        this.mArcheryView = mArcheryView;
        String  name = PreferenceManager.getDefaultSharedPreferences(context).getString("target_name","default_target");
        target = loadFromFile(name);
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setColor(Color.GREEN);
    }

    @Override
    public void onSizeChanged(int w,int h, int oldw, int oldh)
    {
        maxr = Math.min(w,h)/2;
        screenWidth = w;
        screenHeight = h;
    }

    @Override
    public void onDraw(Canvas screenCanvas)
    {
        if (target == null)
            return;
        Bitmap bitmap = Bitmap.createBitmap(maxr*2,maxr*2,Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        target.draw(canvas,maxr);
        distance = mArcheryView.getCurrentDistance();
        for (CShot [] series : distance.finishedSeries)
            for (CShot shot : series)
                canvas.drawCircle((shot.x+1)*maxr,(shot.y+1)*maxr,distance.arrow.radius*maxr,arrowPaint);
        for (CShot shot : distance.currentSeries)
            canvas.drawCircle((shot.x+1)*maxr,(shot.y+1)*maxr,distance.arrow.radius*maxr,arrowPaint);
        if (haveZoom)
        {
            canvas.drawPoint(x,y,arrowPaint);
            canvas.drawCircle(x,y,distance.arrow.radius*maxr,arrowPaint);
            canvas.drawBitmap(bitmap,zoomSrcRect,zoomDestRect,null);
        }
        screenCanvas.drawBitmap(bitmap,0,0,null);
    }

    public CTarget loadFromFile(String name)  {
        CTarget targets [];
        try
        {
            ObjectInputStream ois = new ObjectInputStream(context.openFileInput("Targets"));
            targets = (CTarget[]) ois.readObject();
            ois.close();
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(context, e.getMessage(), 3000);
            toast.show();
            return null;
        }
        for (CTarget target : targets)
        {
            if (target.name.equals(name))
                return new CTarget(target);
        }
        return null;
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
            haveZoom = false;
            mArcheryView.addShot(new CShot(target.rings, x / maxr - 1, y / maxr - 1,
                    mArcheryView.getCurrentDistance().arrow.radius));
            MainActivity.vibrator.vibrate(100);
			mArcheryView.invalidate();
            return true;
        }
		return false;
	}
}

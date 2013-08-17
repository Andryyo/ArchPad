package com.Andryyo.ArchPad.archeryFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 25.04.13
 * Time: 11:01
 * To change this template use File | Settings | File Templates.
 */
public class СEndsCounterView extends View {
    int radius;
    int offset;
    int screenWidth;
    int screenHeight;
    CArcheryFragment mArcheryView;
    Paint paint = new Paint();

    public СEndsCounterView(Context context, CArcheryFragment mArcheryView)   {
        super(context);
        this.mArcheryView = mArcheryView;
        paint.setColor(Color.WHITE);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        paint.setTextAlign(Paint.Align.LEFT);
        screenWidth = w;
        screenHeight = h;
    }

    @Override
    public void onDraw(Canvas canvas)   {
        radius = Math.min(screenWidth*8/20,screenHeight/(mArcheryView.getCurrentDistance().numberOfEnds+2));
        offset = (int) (2.5*radius);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        CDistance distance;
        distance = mArcheryView.getCurrentDistance();
        for (int i =0;i<distance.ends.size();i++)
            canvas.drawCircle(radius,(i+1)*offset+2,radius,paint);
        paint.setStyle(Paint.Style.STROKE);
        for (int i =distance.ends.size();i<distance.numberOfEnds;i++)
            canvas.drawCircle(radius,(i+1)*offset,radius,paint);
    }
}

package com.example.archery.archeryView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import com.example.archery.CShot;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 23.04.13
 * Time: 23:38
 * To change this template use File | Settings | File Templates.
 */

public class CCurrentSeriesView extends View {
    CDistance distance;
    int textHeight;
    int textWidth;
    int screenWidth;
    CArcheryView mArcheryView;
    Paint paint = new Paint();

    public CCurrentSeriesView(Context context, CArcheryView mArcheryView)   {
        super(context);
        this.mArcheryView = mArcheryView;
        paint.setColor(Color.WHITE);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        paint.setTextAlign(Paint.Align.LEFT);
        textHeight = h*8/10;
        paint.setTextSize(textHeight);
        screenWidth = w;
    }

    @Override
    public void onDraw(Canvas canvas)   {
        int i=0;
        distance = mArcheryView.getCurrentDistance();
        textWidth = screenWidth/(distance.numberOfArrows+1);
        CShot buf[];
        if ((distance.currentSeries.size()==0)&&(!distance.finishedSeries.isEmpty()))
            buf = distance.finishedSeries.lastElement();
        else
            buf = distance.currentSeries.toArray(new CShot[0]);
        for (CShot shot : buf)
        {
            canvas.drawText(shot.toString(), i*textWidth+textWidth/2, textHeight, paint);
            i++;
        }
    }
}

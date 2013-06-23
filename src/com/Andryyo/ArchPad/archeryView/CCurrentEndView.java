package com.Andryyo.ArchPad.archeryView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import com.Andryyo.ArchPad.CShot;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 23.04.13
 * Time: 23:38
 * To change this template use File | Settings | File Templates.
 */

public class CCurrentEndView extends View {
    CDistance distance;
    int textHeight;
    int textWidth;
    int screenWidth;
    CArcheryFragment mArcheryView;
    Paint paint = new Paint();

    public CCurrentEndView(Context context, CArcheryFragment mArcheryView)   {
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
        Vector<CShot> buf;
        if ((distance.ends.lastElement().isEmpty())&&(distance.ends.size()>1))
            buf = distance.ends.get(distance.ends.size()-2);
        else
            buf = distance.ends.lastElement();
        for (CShot shot : buf)
        {
            canvas.drawText(shot.toString(), i*textWidth+textWidth/2, textHeight, paint);
            i++;
        }
    }
}

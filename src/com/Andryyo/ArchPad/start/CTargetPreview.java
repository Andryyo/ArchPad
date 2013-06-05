package com.Andryyo.ArchPad.start;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Gallery;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.target.CTarget;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 30.04.13
 * Time: 23:20
 * To change this template use File | Settings | File Templates.
 */
public class CTargetPreview extends View {

    CTarget target = null;
    int maxr;

    public CTargetPreview(Context context, AttributeSet attrs)  {
        super(context,attrs,R.style.Theme_galleryItemBackground);
    }

    public CTargetPreview(Context context, CTarget target) {
        super(context,null, R.style.Theme_galleryItemBackground);
        this.target = target;
        this.setLayoutParams(new Gallery.LayoutParams(100,100));
    }

    public void setTarget(CTarget target)   {
        this.target = target;
    }

    @Override
    public void onMeasure(int w, int h) {
        maxr = Math.min(MeasureSpec.getSize(w),MeasureSpec.getSize(h))/2;
        this.setMeasuredDimension(maxr*2, maxr*2);
    }

    @Override
    public void onDraw(Canvas canvas)   {
        if (target!=null)
            target.draw(canvas, maxr, maxr);
    }
}

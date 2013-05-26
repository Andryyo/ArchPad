package com.example.archery.start;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Gallery;
import com.example.archery.R;
import com.example.archery.target.CTarget;

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
    public void onSizeChanged(int w,int h,int oldw,int oldh)    {
        maxr = Math.min(w,h)/2;
        super.onSizeChanged(w,h,oldw,oldh);
    }

    @Override
    public void onDraw(Canvas canvas)   {
        if (target!=null)
            if (!target.isEmpty())
                target.draw(canvas, maxr, maxr);
    }

    public long getTargetId()    {
        return target.id;
    }
}

package com.Andryyo.ArchPad.statistics;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;
import com.Andryyo.ArchPad.R;

/**
* Created with IntelliJ IDEA.
* User: Андрей
* Date: 12.05.13
* Time: 12:25
* To change this template use File | Settings | File Templates.
*/
public class CBorderedTextView extends TextView {
        int color;
        int width;
        Paint paint = new Paint();
        Rect rect;

        public CBorderedTextView(Context context)   {
            super(context);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
        }

        public CBorderedTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CBorderedTextView,0,0);
            try
            {
                color = a.getColor(R.styleable.CBorderedTextView_borderColor, Color.WHITE);
                width = a.getInt(R.styleable.CBorderedTextView_borderWidth,0);
            }
            finally {
                a.recycle();
            }
            paint.setColor(color);
            paint.setStrokeWidth(width);
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w,h,oldw, oldh);
            rect = new Rect(0,0,w-1,h-1);
        }
        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawRect(rect, paint);
            super.onDraw(canvas);
        }
    }

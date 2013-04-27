package com.example.archery.statistics;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;
import com.example.archery.R;

public class BorderedTextView extends TextView {
        int color;
        int width;
        Paint paint = new Paint();

        public BorderedTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BorderedTextView,0,0);
            try
            {
                color = a.getColor(R.styleable.BorderedTextView_borderColor, Color.WHITE);
                width = a.getInt(R.styleable.BorderedTextView_borderWidth,0);
            }
            finally {
                a.recycle();
            }
            paint.setColor(color);
            paint.setStrokeWidth(width);
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawRect(canvas.getClipBounds(), paint);
            super.onDraw(canvas);
        }
    }
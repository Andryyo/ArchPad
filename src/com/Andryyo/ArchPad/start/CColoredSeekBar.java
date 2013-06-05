package com.Andryyo.ArchPad.start;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 26.05.13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class CColoredSeekBar extends SeekBar implements SeekBar.OnSeekBarChangeListener{

    private int color;

    public CColoredSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress!=0)
            color = (Color.BLACK-Color.WHITE)/100*progress;
        else
            color = Color.WHITE;
        seekBar.setProgressDrawable(new ColorDrawable(color));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public int getSelectedColor()   {
        return color;
    }
}

package com.Andryyo.ArchPad.start;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 26.05.13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class CColorSelectView extends LinearLayout implements SeekBar.OnSeekBarChangeListener{

    SeekBar redSeekBar;
    SeekBar greenSeekBar;
    SeekBar blueSeekBar;
    int red = 0;
    int green = 0;
    int blue = 0;
    int color = 0;
    ImageView colorView;

    public CColorSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        layout.setOrientation(VERTICAL);
        redSeekBar = new SeekBar(context);
        greenSeekBar = new SeekBar(context);
        blueSeekBar = new SeekBar(context);
        redSeekBar.setOnSeekBarChangeListener(this);
        greenSeekBar.setOnSeekBarChangeListener(this);
        blueSeekBar.setOnSeekBarChangeListener(this);
        layout.addView(redSeekBar);
        layout.addView(greenSeekBar);
        layout.addView(blueSeekBar);
        addView(layout);
        colorView = new ImageView(context);
        colorView.setLayoutParams(new LayoutParams(100, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(colorView);
        applyColor(0,0,0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar==redSeekBar)
            red = 255/100*progress;
        else if (seekBar==greenSeekBar)
            green = 255/100*progress;
        else
            blue = 255/100*progress;
        applyColor(red, green, blue);
    }

    private void applyColor(int red, int green, int blue)   {
        redSeekBar.setProgressDrawable(new ColorDrawable(Color.rgb(red,0,0)));
        greenSeekBar.setProgressDrawable(new ColorDrawable(Color.rgb(0,green,0)));
        blueSeekBar.setProgressDrawable(new ColorDrawable(Color.rgb(0,0,blue)));
        color = Color.rgb(red, green, blue);
        colorView.setImageDrawable(new ColorDrawable(color));
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

package com.example.archery.statistics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.example.archery.R;
import com.example.archery.start.CTargetPreview;
import com.example.archery.target.CTargetView;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 23.05.13
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class CRecordView extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout content = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_record_view,null);
        setContentView(content);
        Intent intent = getIntent();
        Long id = intent.getLongExtra("record_id",-1);
        CTargetView targetView = new CTargetView(this,id);
        targetView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                 ViewGroup.LayoutParams.WRAP_CONTENT));
        content.addView(targetView);
    }
}
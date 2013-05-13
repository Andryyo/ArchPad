package com.example.archery.start;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.example.archery.R;
import com.example.archery.database.CMySQLiteOpenHelper;
import com.example.archery.target.CRing;
import com.example.archery.target.CTarget;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 12.05.13
 * Time: 19:03
 * To change this template use File | Settings | File Templates.
 */
public class TargetAddActivity extends Activity implements DialogInterface.OnDismissListener{

    float distanceFromCenter;
    CTarget target;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_target);
        target = new CTarget("",new Vector<CRing>(),0);
        ((CTargetPreview)findViewById(R.id.targetPreview)).setTarget(target);
    }

    public void addRing(View view)    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Новое кольцо:");
        builder.setView(inflater.inflate(R.layout.ring_add_dialog, null));
        builder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                AlertDialog alertDialog = (AlertDialog) dialog;
                distanceFromCenter+=Float.parseFloat(((EditText) alertDialog.findViewById(R.id.distanceFromCenter)).getText().toString());
                target.addRing(new CRing(Integer.parseInt(((EditText) alertDialog.findViewById(R.id.points)).getText().toString()),
                                    distanceFromCenter,
                                    Integer.parseInt(((EditText) alertDialog.findViewById(R.id.color)).getText().toString())));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    @Override
    public void onPause()   {
        super.onPause();
        if (!target.isEmpty())
        {
            target.addClosingRing();
            CMySQLiteOpenHelper.getHelper(getBaseContext()).addTarget(target);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        ((CTargetPreview) findViewById(R.id.targetPreview)).invalidate();
    }
}
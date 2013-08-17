package com.Andryyo.ArchPad.note;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.EditText;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 20.06.13
 * Time: 17:36
 * To change this template use File | Settings | File Templates.
 */
public class CNoteCreateActivity extends FragmentActivity{

    EditText text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        text = new EditText(this);
        text.setGravity(Gravity.TOP|Gravity.LEFT);
        if (savedInstanceState!=null)
            text.setText(savedInstanceState.getString("text"));
        setContentView(text);
    }

    @Override
    public void onSaveInstanceState(Bundle out)   {
        out.putString("text",text.getText().toString());
        super.onSaveInstanceState(out);
    }

    @Override
    public void onBackPressed() {
        String string = text.getText().toString();
        if (!string.equals("")) {
            if (string.indexOf("\n")!=-1)
                CSQLiteOpenHelper.getHelper(this).addNote(
                    string.substring(0, string.indexOf("\n")),
                    text.getText().toString(),
                    Calendar.getInstance().getTimeInMillis()
                );
            else
                CSQLiteOpenHelper.getHelper(this).addNote(
                        text.getText().toString(),
                        text.getText().toString(),
                        Calendar.getInstance().getTimeInMillis()
                );
        }
        super.onBackPressed();
    }
}

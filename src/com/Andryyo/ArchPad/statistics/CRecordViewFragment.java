package com.Andryyo.ArchPad.statistics;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.target.CZoomableTargetView;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 23.05.13
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class CRecordViewFragment extends DialogFragment {

    private long id;

    public CRecordViewFragment(long id)  {
        super();
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.activity_record_view,null);
        CZoomableTargetView targetView = new CZoomableTargetView(getActivity(),id);
        targetView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.addView(targetView);
        builder.setView(view);
        return builder.create();
    }
}
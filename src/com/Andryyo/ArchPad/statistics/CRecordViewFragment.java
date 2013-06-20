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

    public static final String RECORD_ID = "recordId";
    private long id;

    public CRecordViewFragment()    {}

    public CRecordViewFragment(long id)  {
        super();
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null)
            id = savedInstanceState.getLong(RECORD_ID);
    }

    @Override
    public void onSaveInstanceState(Bundle out)   {
        super.onSaveInstanceState(out);
        out.putLong(RECORD_ID, id);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.record_view_fragment,null);
        CZoomableTargetView targetView = new CZoomableTargetView(getActivity(),id);
        targetView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        view.addView(targetView);
        builder.setView(view);
        return builder.create();
    }
}
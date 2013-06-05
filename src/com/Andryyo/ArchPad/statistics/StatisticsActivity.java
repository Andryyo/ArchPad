package com.Andryyo.ArchPad.statistics;

import java.util.Arrays;
import java.util.Calendar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.*;
import com.Andryyo.ArchPad.CShot;
import com.Andryyo.ArchPad.R;
import com.Andryyo.ArchPad.archeryView.CDistance;
import com.Andryyo.ArchPad.database.CSQLiteOpenHelper;

public class StatisticsActivity extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private ExpandListAdapter adapter;
	private ExpandableListView expandableListView;
    private Context context;

    //TODO:Поработать над статистикой:подсчёт очков, графики т.д.

    public StatisticsActivity(Context context)  {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        //TODO:откомментировать, когда проверю
        //Cursor cursor = CSQLiteOpenHelper.getHelper(context).getCursor(CSQLiteOpenHelper.TABLE_DISTANCES);
        ExpandableListView parent = new ExpandableListView(context);
        //adapter = new ExpandListAdapter(cursor,context,true);
        parent.setAdapter(adapter);
        parent.setBackgroundColor(Color.BLACK);
        registerForContextMenu(expandableListView);
        return parent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_display, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId())
    	{
    	case R.id.clear:
    	{
			adapter.deleteAll();
	    	break;
    	}
    	case R.id.expand:
    	{
    		for (int i=0;i<adapter.getGroupCount();i++)
    			expandableListView.expandGroup(i);
    		break;
    	}
    	case R.id.hide:
    	{
    		for (int i=0;i<adapter.getGroupCount();i++)
    			expandableListView.collapseGroup(i);
    		break;
    	}
    	}
    	return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu,View view,ContextMenu.ContextMenuInfo contextMenuInfo)   {
        super.onCreateContextMenu(contextMenu,view,contextMenuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.records_context_menu,contextMenu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.delete_record:
            {
                ExpandableListView.ExpandableListContextMenuInfo info =
                    (ExpandableListView.ExpandableListContextMenuInfo)menuItem.getMenuInfo();
                adapter.deleteRecord(info.id);
                break;
            }
            case  R.id.view_record:
            {
                Intent intent = new Intent(getActivity(), CRecordView.class);
                ExpandableListView.ExpandableListContextMenuInfo info =
                        (ExpandableListView.ExpandableListContextMenuInfo)menuItem.getMenuInfo();
                intent.putExtra("record_id",info.id);
                startActivity(intent);
                break;
            }
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //cursorLoader = new CursorLoader(context, );
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.changeCursor(null);
    }

    private class ExpandListAdapter extends CursorTreeAdapter {
    CSQLiteOpenHelper helper;
    int sum;

        public ExpandListAdapter(Cursor cursor, Context context,boolean autoRequery) {
            super(cursor, context, autoRequery);
            helper = CSQLiteOpenHelper.getHelper(context);
        }

        public void deleteRecord(long id)
        {
            //TODO:откомментировать, когда проверю
            helper.delete(CSQLiteOpenHelper.TABLE_DISTANCES,id);
            //adapter.changeCursor(helper.getDistancesCursor());
        }

	public void deleteAll()
	{
        //TODO:откомментировать, когда проверю
        helper.delete(CSQLiteOpenHelper.TABLE_DISTANCES);
        //adapter.changeCursor(helper.getDistancesCursor());
    }

        private void fillStatisticsBlockView(LinearLayout layout, CShot[][] series)  {
            int first_series_sum = 0;
            int second_series_sum = 0;
            for (CShot shot : series[0])
                if (shot!=null)
                    first_series_sum+=shot.getPoints();
            if (series[1]!=null)
                for (CShot shot : series[1])
                    if (shot!=null)
                        second_series_sum+=shot.getPoints();
            CBorderedTextView tv = (CBorderedTextView)layout.findViewById(R.id.first_series);
            tv.setText(Arrays.deepToString(series[0]));
            if (series[1]!=null)
                {
                tv = (CBorderedTextView)layout.findViewById(R.id.second_series);
                tv.setText(Arrays.deepToString(series[1]));
                tv = (CBorderedTextView)layout.findViewById(R.id.second_series_sum);
                tv.setText(Integer.toString(second_series_sum));
                }
            tv = (CBorderedTextView)layout.findViewById(R.id.first_series_sum);
            tv.setText(Integer.toString(first_series_sum));
            tv = (CBorderedTextView)layout.findViewById(R.id.two_series);
            tv.setText(Integer.toString((first_series_sum+second_series_sum)));
            tv = (CBorderedTextView)layout.findViewById(R.id.all_series);
            sum+=first_series_sum+second_series_sum;
            tv.setText(Integer.toString(sum));
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            //TODO:откомментировать, когда проверю
            //Cursor cursor = helper.getDistanceCursor(groupCursor.getLong(groupCursor.getColumnIndex("_id")));
            //startManagingCursor(cursor);
            //return cursor;
            return null;
        }

        @Override
        protected View newGroupView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return infalInflater.inflate(R.layout.expand_list_group, null);
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean b) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("timemark")));
            ((TextView)view.findViewById(R.id.text1)).setText(
                    calendar.get(Calendar.DATE) + ":" +
                    Integer.toString(calendar.get(Calendar.MONTH)+1) + ":" +
                    calendar.get(Calendar.YEAR) + " " +
                    calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                    calendar.get(Calendar.MINUTE));
        }

        @Override
        protected View newChildView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
            LinearLayout view = new LinearLayout(context);
            view.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,
                    ListView.LayoutParams.WRAP_CONTENT));
            view.setBackgroundColor(Color.BLACK);
            view.setOrientation(LinearLayout.VERTICAL);
            return view;
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean b) {
            ((LinearLayout)view).removeAllViews();
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            CDistance distance = new CDistance(cursor);
            sum = 0;
            LinearLayout statisticsBlock;
            for (int i =0; i<distance.series.size()/2;i++)
            {
                statisticsBlock = (LinearLayout) infalInflater.inflate(R.layout.statistics_block,null);
                fillStatisticsBlockView(statisticsBlock, new CShot[][]{distance.series.get(i*2).toArray(new CShot[0]),
                        distance.series.get(i*2 + 1).toArray(new CShot[0])});
                ((LinearLayout) view).addView(statisticsBlock);
            }
            if (distance.series.size()%2!=0)
            {
                statisticsBlock = (LinearLayout) infalInflater.inflate(R.layout.statistics_block,null);
                fillStatisticsBlockView(statisticsBlock, new CShot[][]{distance.series.lastElement().toArray(new CShot[0]),
                        null});
                ((LinearLayout) view).addView(statisticsBlock);
            }
        }
    }

}

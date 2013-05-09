package com.example.archery.statistics;

import java.util.Arrays;
import java.util.Calendar;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.archery.CShot;
import com.example.archery.R;
import com.example.archery.archeryView.CDistance;
import com.example.archery.database.CMySQLiteOpenHelper;

public class StatisticsActivity extends Activity    {

	private ExpandListAdapter adapter;
	private ExpandableListView expandableListView;

    //TODO:Поработать над статистикой:подсчёт очков, графики т.д.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Cursor cursor = CMySQLiteOpenHelper.getHelper(this).getDistancesCursor();
        startManagingCursor(cursor);
        adapter = new ExpandListAdapter(cursor,this,true);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView1);
        expandableListView.setAdapter(adapter);
        registerForContextMenu(expandableListView);
    }

    @Override
    public void onDestroy() {
        CMySQLiteOpenHelper.getHelper(this).close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_display, menu);
        return true;
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu,contextMenu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId()==R.id.delete_record)
        {
            ExpandableListView.ExpandableListContextMenuInfo info =
                    (ExpandableListView.ExpandableListContextMenuInfo)menuItem.getMenuInfo();
            adapter.delete_record(info.id);
        }
        return true;
    }

    private class ExpandListAdapter extends CursorTreeAdapter   {
    CMySQLiteOpenHelper helper;
    int sum;

        public ExpandListAdapter(Cursor cursor, Context context,boolean autoRequery) {
            super(cursor, context, autoRequery);
            helper = CMySQLiteOpenHelper.getHelper(context);
        }

        public void delete_record(long id)
        {
            helper.deleteDistance(id);
            adapter.changeCursor(helper.getDistancesCursor());
        }

	public void deleteAll()
	{
        helper.deleteAllDistances();
        adapter.changeCursor(helper.getDistancesCursor());
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
            long l = groupCursor.getLong(groupCursor.getColumnIndex("_id"));
            Cursor cursor = helper.getDistanceCursor(groupCursor.getLong(groupCursor.getColumnIndex("_id")));
            l = cursor.getCount();
            startManagingCursor(cursor);
            return cursor;
        }

        @Override
        protected View newGroupView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
            TextView textview = new TextView(context);
            textview.setTextColor(Color.GREEN);
            return textview;
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean b) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("timemark")));
            ((TextView)view).setText(cursor.getLong(cursor.getColumnIndex("_id"))+"___"+
                    calendar.get(Calendar.DATE) + ":" +
                    calendar.get(Calendar.MONTH) + ":" +
                    calendar.get(Calendar.YEAR) + " " +
                    calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                    calendar.get(Calendar.MINUTE));
        }

        @Override
        protected View newChildView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return infalInflater.inflate(R.layout.expandlistchild,null);
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean b) {
            ((LinearLayout)view).removeAllViews();
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            CDistance distance = new CDistance(cursor);
            sum = 0;
            for (int i =0; i<distance.series.size()-distance.series.size()%2;i+=2)
            {
                LinearLayout statisticsBlock = (LinearLayout) infalInflater.inflate(R.layout.statistics_block, null);
                fillStatisticsBlockView(statisticsBlock, new CShot[][]{distance.series.get(i).toArray(new CShot[0]),
                        distance.series.get(i + 1).toArray(new CShot[0])});
                ((LinearLayout)view).addView(statisticsBlock);
            }
            if (distance.series.size()%2!=0)
            {
                LinearLayout statisticsBlock = (LinearLayout) infalInflater.inflate(R.layout.statistics_block, null);
                fillStatisticsBlockView(statisticsBlock, new CShot[][]{distance.series.lastElement().toArray(new CShot[0]),
                        null});
                ((LinearLayout)view).addView(statisticsBlock);
            }
        }
    }

}

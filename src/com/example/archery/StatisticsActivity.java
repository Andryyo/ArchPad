package com.example.archery;

import java.io.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Vector;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.archery.archeryView.CDistance;
import com.example.archery.archeryView.CShot;

public class StatisticsActivity extends Activity{

	private ExpandListAdapter adapter;
	private ExpandableListView expandableListView;

    //TODO:Поработать над статистикой:подсчёт очков, графики т.д.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Vector<CDistance> distances = new Vector<CDistance>();
        try
        {
        ObjectInputStream ois = new ObjectInputStream(this.openFileInput("Archery"));
        distances = (Vector<CDistance>) ois.readObject();
        ois.close();
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(this, e.getMessage(), 3000);
            toast.show();
        }

        adapter = new ExpandListAdapter(this, distances);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView1);
        expandableListView.setAdapter(adapter);
        registerForContextMenu(expandableListView);
    }
    @Override
    public void onPause()    {
        super.onPause();
        adapter.saveDistances();
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
    		this.deleteFile("Archery");
			adapter.Clean();
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
            adapter.delete_record(ExpandableListView.getPackedPositionGroup(info.packedPosition));
        }
        return true;
    }


    private class ExpandListAdapter extends BaseExpandableListAdapter   {
	Context context;
	Vector<CDistance> distances;

    public void delete_record(int n)
    {
        distances.remove(n);
        notifyDataSetChanged();
    }

	public void Clean()
	{
		distances.clear();
        notifyDataSetChanged();
	}
	
	public ExpandListAdapter(Context context, Vector<CDistance> distances)
	{
		this.context=context;
		this.distances=distances;

	}
	
	public Object getChild(int groupPosition, int childPosition) {
		return distances.get(groupPosition).finishedSeries.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String child = Arrays.deepToString((CShot[]) getChild(groupPosition, childPosition));

		if (convertView == null){
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.expandlistchild, null);
		}
		TextView textview = (TextView) convertView.findViewById(R.id.child);
		textview.setText(child);
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return distances.get(groupPosition).finishedSeries.size();
	}

	public Object getGroup(int groupPosition) {
		return distances.get(groupPosition);
	}

	public int getGroupCount() {
		return distances.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null){
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.expandlistgroup, null);
		}
		TextView textview = (TextView) convertView.findViewById(R.id.group);
        Calendar calendar = distances.get(groupPosition).timemark;
		textview.setText(calendar.get(Calendar.DATE)+":"+
                         calendar.get(Calendar.MONTH)+":"+
                         calendar.get(Calendar.YEAR)+" "+
                         calendar.get(Calendar.HOUR_OF_DAY)+":"+
                         calendar.get(Calendar.MINUTE));
		return convertView;
	}

    public void saveDistances()
    {
        try
        {
        ObjectOutputStream oos = new ObjectOutputStream(openFileOutput("Archery",MODE_PRIVATE));
        oos.writeObject(distances);
        oos.close();
        }
        catch (IOException e)
        {
            Toast toast = Toast.makeText(context, e.getMessage(), 3000);
            toast.show();
        }
    }

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
}

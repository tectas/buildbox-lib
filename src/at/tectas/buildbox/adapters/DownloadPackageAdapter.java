package at.tectas.buildbox.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import at.tectas.buildbox.R;
import at.tectas.buildbox.helpers.DownloadPackage;
import at.tectas.buildbox.helpers.SharedObjectsHelper;

public class DownloadPackageAdapter extends BaseAdapter {
	
	private final static String TAG = "DownloadPackageAdapter";
	private final Activity context;
	
	static class ViewHolder {
		public TextView text;
		public CheckBox checkbox;
		public ProgressBar progressbar;
	}
	
	public DownloadPackageAdapter(Activity context) {
		super();
		this.context = context;
	}
	
	public void add(DownloadPackage object) {		
		SharedObjectsHelper.downloads.put(object.md5sum, object);
		this.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Log.e(TAG, "getView");

		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = this.context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.download_list_item_fragment, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.download_item_text);
			viewHolder.checkbox = (CheckBox) rowView.findViewById(R.id.download_md5sum_check);
			viewHolder.progressbar = (ProgressBar) rowView.findViewById(R.id.download_item_progress);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		DownloadPackage item = SharedObjectsHelper.downloads.get(position);
	    holder.text.setText(item.filename);
	    holder.checkbox.setChecked(false);
	    holder.checkbox.setActivated(false);
	    if (item.response != null) {
	    	holder.progressbar.setProgress(item.response.progress);
	    }
	    
		return rowView;
	}

	@Override
	public int getCount() {
		return SharedObjectsHelper.downloads.size();
	}

	@Override
	public Object getItem(int position) {
		return SharedObjectsHelper.downloads.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
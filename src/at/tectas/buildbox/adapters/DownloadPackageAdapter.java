package at.tectas.buildbox.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.DownloadResponse.DownloadStatus;

public class DownloadPackageAdapter extends BaseAdapter implements OnLongClickListener {
	private final BuildBoxMainActivity context;
	
	public DownloadPackageAdapter(Activity context) {
		super();
		this.context = (BuildBoxMainActivity) context;
	}
	
	public void add(DownloadPackage object) {		
		this.context.getDownloads().put(object);
		this.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		
		LayoutInflater inflater = this.context.getLayoutInflater();
		rowView = inflater.inflate(R.layout.download_list_item_fragment, parent, false);
		TextView text = (TextView) rowView.findViewById(R.id.download_text);
		TextView subText = (TextView) rowView.findViewById(R.id.download_subtext);
		CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.download_md5sum_check);
		ProgressBar progressbar = (ProgressBar) rowView.findViewById(R.id.download_progress);
		TextView progressText = (TextView) rowView.findViewById(R.id.download_textprogress);
		TextView statusText = (TextView) rowView.findViewById(R.id.download_result_status);		
		
		DownloadPackage item = this.context.getDownloads().get(position);
		
		rowView.setOnLongClickListener(this);
		
		try {
			text.setText(item.title == null?"": item.title);
			subText.setText(item.filename);
		    checkbox.setChecked(false);
		    checkbox.setClickable(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	    
	    if (item != null && item.response != null) {	    	
	    	progressbar.setEnabled(true);
	    	progressbar.setMax(100);
	    	progressbar.setIndeterminate(false);
	    	progressbar.setProgress(item.response.progress);
	    	progressText.setText(String.valueOf(item.response.progress) + "%");
	    	
	    	if (item.response.status == DownloadStatus.Successful) {
	    		checkbox.setChecked(true);
	    		statusText.setText("Successful");
	    	}
	    	else if (item.response.status == DownloadStatus.Done) {
	    		checkbox.setChecked(false);
	    		statusText.setText("Done");
	    	}
	    	else if (item.response.status == DownloadStatus.Broken) {
	    		checkbox.setChecked(false);
	    		text.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
	    		statusText.setText("Broken");
	    	}
	    	else if (item.response.status == DownloadStatus.Md5mismatch) {
	    		checkbox.setChecked(false);
	    		statusText.setText("Md5sum mismatch");
	    	}
	    	else if (item.response.status == DownloadStatus.Aborted) {
	    		checkbox.setChecked(false);
	    		statusText.setText("Aborted");
	    	}
	    }
	    
	    rowView.setTag(position);
	    
	    int finishedDownload = 0;
	    
	    for (DownloadPackage pack: this.context.getDownloads().values()) {
			if(pack.response != null && pack.response.status != DownloadStatus.Pending) {
				finishedDownload++;
			}
	    }
	    
	    if (finishedDownload == this.context.getDownloads().size()) {
	    	ViewGroup outerParent = (ViewGroup) parent.getParent();
	    	
	    	Button button = (Button) outerParent.findViewById(R.id.download_all_button);
	    	
	    	if (button != null) {
				button.setText(R.string.download_all_button_text);
	    	}
	    }
	    
		return rowView;
	}

	@Override
	public int getCount() {
		return this.context.getDownloads().size();
	}

	@Override
	public Object getItem(int position) {
		return this.context.getDownloads().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean onLongClick(View v) {
		int position = (Integer) v.getTag();
		
		this.context.getDownloads().remove(position);
		
		this.notifyDataSetChanged();
		
		return true;
	}
}
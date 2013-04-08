package at.tectas.buildbox.library.adapters;

import com.mobeta.android.dslv.DragSortListView.DragSortListener;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import at.tectas.buildbox.R;
import at.tectas.buildbox.library.communication.DownloadPackage;
import at.tectas.buildbox.library.communication.DownloadResponse;
import at.tectas.buildbox.library.communication.DownloadStatus;
import at.tectas.buildbox.library.download.DownloadActivity;
import at.tectas.buildbox.library.service.DownloadService;

public class DownloadPackageAdapter extends BaseAdapter implements DragSortListener {
	private final DownloadActivity context;
	private Button downloadButton = null;
	private ViewGroup buttonLayout = null;
	
	public DownloadPackageAdapter(Activity context, ViewGroup buttonLayout) {
		super();
		this.context = (DownloadActivity) context;
		this.buttonLayout = buttonLayout;
	}
	
	public void add(DownloadPackage object) {		
		this.context.getDownloads().put(object);
		this.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		
		if (rowView == null) {
			LayoutInflater inflater = this.context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.download_list_item_fragment, parent, false);
		}	
		
		TextView text = (TextView) rowView.findViewById(R.id.download_text);
		TextView subText = (TextView) rowView.findViewById(R.id.download_subtext);
		CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.download_md5sum_check);
		ProgressBar progressbar = (ProgressBar) rowView.findViewById(R.id.download_progress);
		TextView progressText = (TextView) rowView.findViewById(R.id.download_textprogress);
		TextView statusText = (TextView) rowView.findViewById(R.id.download_result_status);
		
		DownloadPackage item = this.context.getDownloads().get(position);
		
		try {
			text.setText(item.title == null?"": item.title);
			subText.setText(item.getFilename());
		    checkbox.setChecked(false);
		    checkbox.setClickable(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	    
	    if (item != null && item.getResponse() != null) {
	    	DownloadResponse response = item.getResponse();
	    	
	    	progressbar.setEnabled(true);
	    	progressbar.setMax(100);
	    	progressbar.setIndeterminate(false);
	    	progressbar.setProgress(response.progress);
	    	progressText.setText(String.valueOf(response.progress) + "%");
	    	
	    	if (response.status == DownloadStatus.Successful) {
	    		checkbox.setChecked(true);
	    		text.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
	    		statusText.setText("Successful");
	    	}
	    	else if (response.status == DownloadStatus.Done) {
	    		checkbox.setChecked(false);
	    		text.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
	    		statusText.setText("Done");
	    	}
	    	else if (response.status == DownloadStatus.Broken) {
	    		checkbox.setChecked(false);
	    		text.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
	    		statusText.setText("Broken");
	    	}
	    	else if (response.status == DownloadStatus.Md5mismatch) {
	    		checkbox.setChecked(false);
	    		text.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
	    		statusText.setText("Md5sum mismatch");
	    	}
	    	else if (response.status == DownloadStatus.Aborted) {
	    		checkbox.setChecked(false);
	    		text.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
	    		statusText.setText("Aborted");
	    	}
	    	else if (response.status == DownloadStatus.Pending) {
	    		checkbox.setChecked(false);
	    		text.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
	    		statusText.setText("");
	    	}
	    }
	    else {
	    	progressbar.setProgress(0);
	    	progressText.setText("");
	    	checkbox.setChecked(false);
    		statusText.setText("");
	    }
	    
	    rowView.setTag(position);
    	
    	this.downloadButton = (Button) buttonLayout.findViewById(R.id.download_all_button);
    	
    	if (this.downloadButton != null) {
    		if (this.context.downloadMapContainsBrokenOrAborted() == true && DownloadService.Processing == false) {
    			this.downloadButton.setText(R.string.download_retry_broken);
    		}
    		else if (this.context.allDownloadsFinished() == true) {
    			this.downloadButton.setText(R.string.download_flash_text);
    		}
    		else if (DownloadService.Processing == true) {
    			this.downloadButton.setText(this.context.getString(R.string.download_stop_button_text));
    		}
    		else {
    			this.downloadButton.setText(this.context.getString(R.string.download_all_button_text));
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
	public void drop(int from, int to) {
		if (from != to) {			
			this.context.getDownloads().move(from, to);
			
			this.notifyDataSetChanged();
		}
	}

	@Override
	public void drag(int from, int to) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(int which) {
		if (which < this.context.getDownloads().size()) {
			this.context.getDownloads().remove(which);
			
			this.notifyDataSetChanged();
		}
	}
}
package at.tectas.buildbox.library.listeners;

import android.view.View;
import android.widget.Button;
import at.tectas.buildbox.library.R;
import at.tectas.buildbox.library.download.DownloadActivity;
import at.tectas.buildbox.library.service.DownloadService;

public class ListDownloadButtonListener extends DownloadButtonBaseListener {
	
	public ListDownloadButtonListener(DownloadActivity activity) {
		super(activity);
	}
	
	@Override
	public void onClick(View v) {		
		Button button = (Button)v;
		
		if (button.getText().equals(this.activity.getString(R.string.download_flash_text))) {
			activity.installFiles();
		}
		else if (DownloadService.Processing == true) {
			activity.stopDownload();
			button.setText(R.string.download_all_button_text);
		}
		else {
			activity.startDownload();
			button.setText(R.string.download_stop_button_text);
		}
	}
}

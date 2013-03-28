package at.tectas.buildbox.listeners;

import android.view.View;
import android.widget.Button;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.service.DownloadService;

public class ListDownloadButtonListener extends DownloadButtonBaseListener {
	
	public ListDownloadButtonListener(BuildBoxMainActivity activity) {
		super(activity);
	}
	
	@Override
	public void onClick(View v) {		
		Button button = (Button)v;
		
		if (button.getText().equals(this.activity.getString(R.string.download_flash_text))) {
			activity.installApks();
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

package at.tectas.buildbox.library.listeners;

import android.view.View.OnClickListener;
import at.tectas.buildbox.library.download.DownloadActivity;

public abstract class DownloadButtonBaseListener implements OnClickListener {
	
	protected DownloadActivity activity = null;
	
	protected DownloadButtonBaseListener(DownloadActivity activity) {
		this.activity = activity;
	}
}

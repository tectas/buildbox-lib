package at.tectas.buildbox.listeners;

import android.view.View.OnClickListener;
import at.tectas.buildbox.BuildBoxMainActivity;

public abstract class DownloadButtonBaseListener implements OnClickListener {
	
	protected BuildBoxMainActivity activity = null;
	
	protected DownloadButtonBaseListener(BuildBoxMainActivity activity) {
		this.activity = activity;
	}
}

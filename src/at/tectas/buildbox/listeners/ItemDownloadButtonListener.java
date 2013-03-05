package at.tectas.buildbox.listeners;

import android.view.View;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.communication.DownloadPackage;

public class ItemDownloadButtonListener extends DownloadButtonBaseListener {
	
	public ItemDownloadButtonListener(BuildBoxMainActivity activity) {
		super(activity);
	}
	
	@Override
	public void onClick(View v) {
		final View button = v;
		
		if (!activity.bar.getTabAt(activity.bar.getTabCount() - 1).getText().equals("Downloads")) {
			activity.addDownloadsTab();
		}
		
 		DownloadPackage pack = (DownloadPackage) button.getTag();
 		
		if (activity.downloadAdapter != null) {
			activity.downloadAdapter.add(pack);
		}
		else {
			activity.getDownloads().put(pack);
		}
	}
}

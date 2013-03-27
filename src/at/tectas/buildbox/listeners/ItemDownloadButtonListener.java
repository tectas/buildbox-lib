package at.tectas.buildbox.listeners;

import android.view.View;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.service.DownloadService;

public class ItemDownloadButtonListener extends DownloadButtonBaseListener {
	
	public ItemDownloadButtonListener(BuildBoxMainActivity activity) {
		super(activity);
	}
	
	@Override
	public void onClick(View v) {
		final View button = v;
		
		activity.addDownloadsTab();
		
 		DownloadPackage pack = (DownloadPackage) button.getTag();
 		
 		if (DownloadService.Processing == false) {
			if (activity.downloadAdapter != null) {
				activity.downloadAdapter.add(pack);
				activity.downloadAdapter.notifyDataSetChanged();
			}
			else {
				activity.getDownloads().put(pack);
			}
 		}
 		else {
 			activity.addDownload(pack);
 		}
 		
 		while(activity.fragment.getChildFragmentManager().popBackStackImmediate());
	}
}

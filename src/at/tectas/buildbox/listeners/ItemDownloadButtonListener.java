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
		
 		DownloadPackage pack = (DownloadPackage) button.getTag();
 		
 		if (DownloadService.Processing == false) {
			if (activity.getDownloadPackageAdapter() != null) {
				activity.getDownloadPackageAdapter().add(pack);
				activity.getDownloadPackageAdapter().notifyDataSetChanged();
			}
			else {
				activity.getDownloads().put(pack);
			}
 		}
 		else {
 			activity.addDownload(pack);
 		}
 		
		activity.addDownloadsTab();
 		
 		while(activity.getCurrentFragment().getChildFragmentManager().popBackStackImmediate());
	}
}

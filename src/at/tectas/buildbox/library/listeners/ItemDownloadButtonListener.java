package at.tectas.buildbox.library.listeners;

import android.view.View;
import at.tectas.buildbox.library.communication.DownloadPackage;
import at.tectas.buildbox.library.download.DownloadActivity;

public class ItemDownloadButtonListener extends DownloadButtonBaseListener {
	
	public ItemDownloadButtonListener(DownloadActivity activity) {
		super(activity);
	}

	@Override
	public void onClick(View v) {
		final View button = v;
			
 		DownloadPackage pack = (DownloadPackage) button.getTag();
 		
 		activity.addDownload(pack);
 		
 		while(activity.getCurrentFragment().getChildFragmentManager().popBackStackImmediate());
	}
}

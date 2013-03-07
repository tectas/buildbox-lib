package at.tectas.buildbox.listeners;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.service.DownloadService;

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
 		
 		if (DownloadService.Processing == false) {
			if (activity.downloadAdapter != null) {
				activity.downloadAdapter.add(pack);
			}
			else {
				activity.getDownloads().put(pack);
			}
 		}
 		else {
 			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
 			
 			builder.setTitle(R.string.download_running_alert_title);
 			
 			builder.setIcon(android.R.drawable.ic_dialog_alert);
 			
 			builder.setMessage(R.string.download_running_alert_text);
 			
 			builder.setCancelable(true);
 			
 			builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
 			
 			builder.create().show();
 		}
	}
}

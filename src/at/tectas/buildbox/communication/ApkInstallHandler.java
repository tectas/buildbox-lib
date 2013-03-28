package at.tectas.buildbox.communication;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.helpers.PropertyHelper;

@SuppressLint("DefaultLocale")
public class ApkInstallHandler implements IInstallDownloadHandler {
	
	public DownloadPackage packag = null;
	public BuildBoxMainActivity activity = null;
	public int itemIndex = -1;
	
	public ApkInstallHandler(BuildBoxMainActivity activity, DownloadPackage packag, int itemIndex) {
		this.activity = activity;
		this.packag = packag;
	}
	
	@Override
	public void installDownload() {
		
		if (packag != null && activity != null) {
			DownloadResponse response = packag.getResponse();
			
			if (PropertyHelper.stringIsNullOrEmpty(packag.getDirectory()) == false && 
				response != null && (
					(response.mime != null && response.mime.toLowerCase().equals("apk")) || 
					(packag.type != null && packag.type.equals(activity.getString(R.string.item_download_type_apk)))
				)
				&& (
					response.status == DownloadStatus.Successful || 
					response.status == DownloadStatus.Done)
				) {
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				
				intent.setDataAndType(Uri.fromFile(new File(packag.getDirectory(), response.pack.getFilename())), "application/vnd.android.package-archive");
				
				activity.currentApkInstallIndex = this.itemIndex;
				
				activity.startActivityForResult(intent, BuildBoxMainActivity.PACKAGE_MANAGER_RESULT); 
			}
		}
	}
}

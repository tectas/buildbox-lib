package at.tectas.buildbox.communication.handler;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.msteam.R;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.DownloadResponse;
import at.tectas.buildbox.communication.DownloadStatus;
import at.tectas.buildbox.communication.handler.interfaces.IActivityInstallDownloadHandler;
import at.tectas.buildbox.helpers.PropertyHelper;

public class ApkInstallDownloadHandler implements IActivityInstallDownloadHandler {
	
	private static final String TAG = "ApkInstallDownloadHandler";
	public DownloadPackage packag = null;
	public Activity activity = null;
	

	@Override
	public void setParentActivity(Activity activity) {
		this.activity = activity;
	}
	
	public ApkInstallDownloadHandler(DownloadPackage packag) {
		this.packag = packag;
	}
	
	@Override
	@SuppressLint("DefaultLocale")
	public void install() {
		
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
				
				if (activity != null)
					activity.startActivityForResult(intent, BuildBoxMainActivity.PACKAGE_MANAGER_RESULT);
				else
					Log.e(TAG, "activity == null: " + packag.title);
			}
		}
	}
}

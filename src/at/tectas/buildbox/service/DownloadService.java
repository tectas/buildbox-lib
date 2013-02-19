package at.tectas.buildbox.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import at.tectas.buildbox.MainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.helpers.DownloadPackage;
import at.tectas.buildbox.helpers.SharedObjectsHelper;
import at.tectas.buildbox.communication.Communicator;
import at.tectas.buildbox.communication.DownloadResponse;
import at.tectas.buildbox.communication.IDownloadProcessFinishedCallback;
import at.tectas.buildbox.communication.IDownloadProcessProgressCallback;

public class DownloadService extends Service implements IDownloadProcessProgressCallback, IDownloadProcessFinishedCallback {
	
	private final int notificationID = 5;
	private Notification notification = null;
	private NotificationCompat.Builder builder = null;
	private PendingIntent contentIntent = null;
	private Communicator communicator = new Communicator();
	
	@Override
	public void onCreate() {
		for (DownloadPackage pack: SharedObjectsHelper.downloads.values()) {
			pack.updateCallbacks.add(this);
			pack.finishedCallbacks.add(this);
		}
		
		this.getPackageManager().setApplicationEnabledSetting(this.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
		
		Intent activity = new Intent(this, MainActivity.class);

		this.contentIntent = PendingIntent.getActivity(this, 0,	activity, 0);
		
		this.builder = new NotificationCompat.Builder(this);
		
		this.builder.setSmallIcon(R.drawable.buildbox);
		
		this.builder.setDeleteIntent(this.contentIntent);
		
		this.builder.setContentTitle("BuildBox Download Service");
		
		this.builder.setContentText("");
		
		this.builder.setOngoing(true);
		
		this.builder.setAutoCancel(false);
		
		this.builder.setPriority(10);
		
		this.notification = this.builder.build();
		
		this.notification.flags |= Notification.FLAG_FOREGROUND_SERVICE
				| Notification.FLAG_NO_CLEAR;
		
		startForeground(this.notificationID, this.notification);
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		for (DownloadPackage pack: SharedObjectsHelper.downloads.values()) {
			this.builder.setContentTitle(pack.filename);
			
			this.builder.setProgress(100, 0, true);
			
			this.notification = builder.build();
			
			this.notification.flags |= Notification.FLAG_FOREGROUND_SERVICE
					| Notification.FLAG_NO_CLEAR;
			
			startForeground(this.notificationID, this.notification);
			
			this.communicator.executeDownloadAsyncCommunicator(pack.url, pack.directory, pack.filename, pack.md5sum, pack.updateCallbacks, pack.finishedCallbacks);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateDownloadProgess(Integer progress) {
		this.builder.setProgress(100, progress, false);
		
		this.notification = builder.build();
		
		this.notification.flags |= Notification.FLAG_FOREGROUND_SERVICE
				| Notification.FLAG_NO_CLEAR;
		
		startForeground(this.notificationID, this.notification);
	}

	@Override
	public void downloadFinished(DownloadResponse response) {
		SharedObjectsHelper.downloads.get(response.md5sum).response = response;
		
		this.builder.setProgress(100, 100, false);
		
		this.notification = builder.build();
		
		this.notification.flags |= Notification.FLAG_FOREGROUND_SERVICE
				| Notification.FLAG_NO_CLEAR;
		
		startForeground(this.notificationID, this.notification);
	}
}

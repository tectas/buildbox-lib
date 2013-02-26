package at.tectas.buildbox.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import com.google.gson.JsonArray;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.communication.Communicator;
import at.tectas.buildbox.communication.Communicator.CallbackType;
import at.tectas.buildbox.communication.DownloadMap;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.DownloadResponse;
import at.tectas.buildbox.communication.Communicator.DownloadAsyncCommunicator;
import at.tectas.buildbox.communication.DownloadResponse.DownloadStatus;
import at.tectas.buildbox.communication.IDownloadProcessFinishedCallback;
import at.tectas.buildbox.communication.IDownloadProcessProgressCallback;

public class DownloadService extends Service implements IDownloadProcessProgressCallback, IDownloadProcessFinishedCallback {
	
	public static boolean Started = false;
	public static boolean Processing = false;
	
	private final int notificationServieID = 5492;
	private final int notificationFinishedID = 5493;
	private Notification serviceNotification = null;
	private NotificationCompat.Builder serviceBuilder = null;
	private Notification resultNotification = null;
	private NotificationCompat.Builder resultBuilder = null;
	private PendingIntent contentIntent = null;
	private Communicator communicator = new Communicator();
	private int clientsConnected = 0;
	private IBinder binder = new DonwloadServiceBinder(this);
	private DownloadMap map = null;
	private Hashtable<String, DownloadAsyncCommunicator> downloadCommunicators = new Hashtable<String, DownloadAsyncCommunicator>();
	
	public DownloadMap getMap() {
		return map;
	}

	public void setMap(DownloadMap map) {
		this.map = map;
	}
	
	@Override
	public void onCreate() {
		this.getPackageManager().setApplicationEnabledSetting(this.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
		
		Intent intent = new Intent(this, BuildBoxMainActivity.class);
		
		this.contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
		
		this.serviceBuilder = new NotificationCompat.Builder(this);
		
		this.resultBuilder = new NotificationCompat.Builder(this);
		
		this.serviceBuilder.setContentIntent(this.contentIntent);
		
		this.resultBuilder.setContentIntent(this.contentIntent);
		
		this.serviceBuilder.setSmallIcon(R.drawable.buildbox);
		
		this.serviceBuilder.setDeleteIntent(this.contentIntent);
		
		this.serviceBuilder.setContentTitle(getString(R.string.service_notification_title));
		
		this.serviceBuilder.setContentText(getString(R.string.service_notification_text));
		
		this.serviceBuilder.setOngoing(true);
		
		this.serviceBuilder.setAutoCancel(false);
		
		this.serviceNotification = this.serviceBuilder.build();
		
		this.serviceNotification.flags |= Notification.FLAG_FOREGROUND_SERVICE
				| Notification.FLAG_NO_CLEAR;
		
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DownloadService.Started = true;

		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		DownloadService.Started = false;
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		this.clientsConnected += 1;
		
		return this.binder;
	}

	@Override
	public void onRebind(Intent intent) {
		if (this.clientsConnected == 0) {
			this.clientsConnected += 1;
		}
		super.onRebind(intent);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		this.clientsConnected -= 1;
		return super.onUnbind(intent);
	}
	
	public boolean addDownloadListeners (String id, CallbackType type, IDownloadProcessProgressCallback progessListener, IDownloadProcessFinishedCallback resultListener) {
		DownloadAsyncCommunicator communicator = this.downloadCommunicators.get(id);
		boolean result = false;
		
		if (communicator != null) {
			result = communicator.addProgressListener(type, progessListener);
			result &= communicator.addResultListener(type, resultListener);
		}
		
		return result;
	}
	
	public boolean addDownloadListeners (CallbackType type, IDownloadProcessProgressCallback progessListener, IDownloadProcessFinishedCallback resultListener) {
		boolean result = false;
		
		int i = 0;
		
		for (String id: this.downloadCommunicators.keySet()) {
			if (i == 0) {
				result = this.addDownloadListeners(id, type, progessListener, resultListener);
			}
			else {
				result &= this.addDownloadListeners(id, type, progessListener, resultListener);
			}
			i++;
		}
		
		if (this.map != null) {
			for (DownloadPackage pack: this.map.values()) {
				pack.addProgressListener(type, progessListener);
				pack.addFinishedListener(type, resultListener);
			}
		}
		
		return result;
	}
	
	public void removeDownloadListeners (CallbackType type) {		
		for (String id: this.downloadCommunicators.keySet()) {
			this.downloadCommunicators.get(id).removeProgressListener(type);
			this.downloadCommunicators.get(id).removeResultListener(type);
		}
		
		if (this.map != null)
			for (DownloadPackage pack: this.map.values()) {
				pack.removeProgressListener(type);
				pack.removeFinishedListener(type);
			}
	}
	
	public boolean removeDownloadListeners (String id, CallbackType type, IDownloadProcessProgressCallback progressListener, IDownloadProcessFinishedCallback resultListener) {
		DownloadAsyncCommunicator communicator = this.downloadCommunicators.get(id);
		boolean result = false;
		
		if (communicator != null) {
			result = communicator.removeProgressListener(type);
			result &= communicator.removeResultListener(type);
		}
		
		return result;
	}
	
	public boolean removeDownloadListeners (CallbackType type, IDownloadProcessProgressCallback progressListener, IDownloadProcessFinishedCallback resultListener) {
		boolean result = false;
		
		int i = 0;
		
		for (String id: this.downloadCommunicators.keySet()) {
			if (i == 0) {
				result = this.removeDownloadListeners(id, type, progressListener, resultListener);
			}
			else {
				result &= this.removeDownloadListeners(id, type, progressListener, resultListener);
			}
			i++;
		}
		
		if (this.map != null) {
			for (DownloadPackage pack: this.map.values()) {
				pack.addProgressListener(type, progressListener);
				pack.addFinishedListener(type, resultListener);
			}
		}
		
		return result;
	}
	
	public boolean startDownload() {
		return this.startDownload(this.map);
	}
	
	public boolean startDownload(DownloadMap map) {
		if (map != null && DownloadService.Processing == false) {
			this.map = map;
			
			for (DownloadPackage pack: this.map.values()) {
				DownloadService.Processing = true;
				
				pack.addProgressListener(CallbackType.Service, this);
				pack.addFinishedListener(CallbackType.Service, this);
				
				this.serviceBuilder.setContentText(pack.filename);
				
				this.serviceBuilder.setProgress(100, 0, true);
				
				this.serviceNotification = serviceBuilder.build();
				
				this.serviceNotification.flags |= Notification.FLAG_FOREGROUND_SERVICE
						| Notification.FLAG_NO_CLEAR;
				
				startForeground(this.notificationServieID, this.serviceNotification);
				
				this.downloadCommunicators.put(pack.md5sum == null? pack.url : pack.md5sum, this.communicator.executeDownloadAsyncCommunicator(pack.url, pack.directory, pack.filename, pack.md5sum, pack.updateCallbacks, pack.finishedCallbacks));
			}
			
			return true;
		}
		
		return false;
	}
	
	public void stopDownloads() {
		for (DownloadAsyncCommunicator communicator: this.downloadCommunicators.values()) {
			communicator.cancel(true);
		}
		
		this.downloadCommunicators.clear();
	}
	
	@Override
	public void updateDownloadProgess(DownloadResponse response) {
		
		if (response != null) {
			DownloadPackage pack = this.map.get(response.md5sum == null ? response.url : response.md5sum);
			pack.response = response;
		}
		
		this.serviceBuilder.setContentText(response.fileName);
		
		this.serviceBuilder.setProgress(100, response.progress, false);
		
		this.serviceBuilder.setNumber(response.progress);
		
		this.serviceNotification = serviceBuilder.build();
		
		this.serviceNotification.flags |= Notification.FLAG_FOREGROUND_SERVICE
				| Notification.FLAG_NO_CLEAR;
		
		startForeground(this.notificationServieID, this.serviceNotification);
	}

	@Override
	public void downloadFinished(DownloadResponse response) {
		DownloadPackage packag = this.map.get(response.md5sum == null ? response.url : response.md5sum);
		
		if (packag != null) {
			packag.response = response;
		}
		
		this.resultBuilder.setSmallIcon(R.drawable.buildbox);
		
		this.resultBuilder.setContentTitle(getString(R.string.service_result_notification_title));
		
		this.resultBuilder.setContentText(getString(R.string.service_result_notification_text));
		
		NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
		
		inbox.setBigContentTitle(getString(R.string.service_result_notification_bigview_title));
		
		int finishedDownloadsCount = 0;
		
		for (DownloadPackage pack: this.map.values()) {
			if (pack.response != null) {
				if (response.status == DownloadStatus.Successful)
					inbox.addLine(response.fileName + " " + getString(R.string.service_download_finished));
				else if (response.status == DownloadStatus.Broken)
					inbox.addLine(response.fileName + " " + getString(R.string.service_download_failed));
				else if (response.status == DownloadStatus.Md5mismatch)
					inbox.addLine(response.fileName + " " + getString(R.string.service_download_mismatch));
				
				if(response.status != DownloadStatus.Pending) {
					finishedDownloadsCount++;
				}
			}
		}
		
		this.resultBuilder.setStyle(inbox);
		
		this.resultNotification = this.resultBuilder.build();
		
		this.resultNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.notify(this.notificationFinishedID, this.resultNotification);
		
		this.serviceBuilder.setProgress(100, 100, false);
		
		this.serviceNotification = this.serviceBuilder.build();
		
		this.serviceNotification.flags = Notification.FLAG_AUTO_CANCEL;
		
		if (finishedDownloadsCount != this.map.size())
			startForeground(this.notificationServieID, this.serviceNotification);
		else {
			stopForeground(true);
			
			DownloadService.Processing = false;
			
			if (this.clientsConnected == 0)
				this.serializeMap();
		}
	}
	
	public void serializeMap() {
		Handler handler = new Handler();
		
		final DownloadMap map = this.map;
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				JsonArray jsonMap = map.serializeToJsonArray();
				
				try {
					FileOutputStream stream = openFileOutput(getString(R.string.downloads_cach_filename), Context.MODE_PRIVATE);
					
					stream.write(jsonMap.toString().getBytes());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
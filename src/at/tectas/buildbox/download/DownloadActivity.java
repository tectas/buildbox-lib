package at.tectas.buildbox.download;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;
import at.tectas.buildbox.R;
import at.tectas.buildbox.communication.Communicator;
import at.tectas.buildbox.communication.DownloadKey;
import at.tectas.buildbox.communication.DownloadMap;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.DownloadStatus;
import at.tectas.buildbox.communication.callbacks.CallbackType;
import at.tectas.buildbox.communication.callbacks.MapDeserializedProcessCallback;
import at.tectas.buildbox.communication.callbacks.interfaces.DownloadBaseCallback;
import at.tectas.buildbox.communication.callbacks.interfaces.ICommunicatorCallback;
import at.tectas.buildbox.communication.callbacks.interfaces.IDeserializeMapFinishedCallback;
import at.tectas.buildbox.communication.callbacks.interfaces.IDownloadCancelledCallback;
import at.tectas.buildbox.communication.callbacks.interfaces.IDownloadFinishedCallback;
import at.tectas.buildbox.communication.callbacks.interfaces.IDownloadProgressCallback;
import at.tectas.buildbox.communication.handler.interfaces.IActivityInstallDownloadHandler;
import at.tectas.buildbox.content.items.properties.DownloadType;
import at.tectas.buildbox.fragments.FlashConfigurationDialog;
import at.tectas.buildbox.recovery.OpenRecoveryScript;
import at.tectas.buildbox.recovery.OpenRecoveryScriptConfiguration;
import at.tectas.buildbox.service.DownloadService;

public abstract class DownloadActivity extends FragmentActivity implements ICommunicatorCallback, IDeserializeMapFinishedCallback {

	public static final String TAG = "DownloadActivity";
	public static final int PACKAGE_MANAGER_RESULT = 3;
	
	protected Communicator communicator = new Communicator();
	protected DownloadMap downloads = new DownloadMap();
	protected DownloadServiceConnection serviceConnection = new DownloadServiceConnection(this);
	protected OpenRecoveryScript recoveryScript = null;
	protected int currentInstallIndex = 0;
	protected boolean downloadMapRestored = false;
	
	public abstract String getDownloadDir();
	
	public abstract DownloadBaseCallback getDownloadCallback();
	
	public abstract void setDownloadCallback(DownloadBaseCallback callback);
	
	public Communicator getCommunicator() {
		return this.communicator;
	}
	
	public synchronized DownloadMap getDownloads() {
		return this.downloads;
	}
	
	public synchronized void setDownloads(DownloadMap map) {
		if (map != null) {
			this.downloads = map;
		}
	}
	
	public DownloadServiceConnection getServiceConnection() {
		return this.serviceConnection;
	}
	
	public OpenRecoveryScript getOpenRecoveryScript() {
		return this.recoveryScript;
	}
	
	public void setOpenRecoveryScript(OpenRecoveryScript script) {
		this.recoveryScript = script;
	}
	
	public int getCurrentInstallIndex() {
		return this.currentInstallIndex;
	}
	
	public void setCurrentInstallIndex(int index) {
		this.currentInstallIndex = index;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case PACKAGE_MANAGER_RESULT:
				if (!this.downloadMapRestored) {
					this.getDownloads().clear();
					
					this.loadDownloadsMapFromCacheFile(new MapDeserializedProcessCallback(this));
					this.downloadMapRestored = true;
				}
  				break;
		}
	}
	
	public boolean allDownloadsFinished() {
		int finishedDownloads = 0;
		
		for (DownloadPackage pack: this.getDownloads().values()) {
			if (pack.getResponse() != null && pack.getResponse().status != DownloadStatus.Pending && pack.getResponse().status != DownloadStatus.Aborted) {
				finishedDownloads++;
			}
		}
		
		if (finishedDownloads == this.getDownloads().size()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean downloadMapContainsBrokenOrAborted() {
		
		for (DownloadPackage pack: this.getDownloads().values()) {
			if (pack.getResponse() != null && (pack.getResponse().status == DownloadStatus.Broken || pack.getResponse().status == DownloadStatus.Aborted)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void removeBrokenAndAbortedFromMap() {
		ArrayList<DownloadKey> keys = new ArrayList<DownloadKey>();
		
		for (DownloadKey key: this.getDownloads().keySet()) {
			DownloadPackage pack = this.getDownloads().get(key);
			
			if (pack.getResponse() != null && (pack.getResponse().status == DownloadStatus.Broken || pack.getResponse().status == DownloadStatus.Aborted)) {
				keys.add(key);
			}
		}
		
		for (DownloadKey key: keys) {
			this.getDownloads().remove(key);
		}
	}
	
	public void bindDownloadService() {
		Intent downloadServiceIntent = new Intent(this.getApplicationContext(), DownloadService.class);	
		
		if (DownloadService.Started == false) {	
			this.startService(downloadServiceIntent);
		}
		
		this.bindService(downloadServiceIntent, this.serviceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
	}
	
	public void unbindDownloadService() {
		if (this.serviceConnection.bound == true) {
			try {
				unbindService(this.serviceConnection);
				this.serviceConnection.bound = false;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startDownload() {
		if (this.serviceConnection.bound == false) {
			this.serviceConnection.executeStartDownloadCallback = true;
			this.bindDownloadService();
		}
		else {
			this.startServiceDownload();
		}
	}
	
	public void stopDownload() {
		if (this.serviceConnection.bound == false) {
			this.serviceConnection.executeStopDownloadCallback = true;
			this.bindDownloadService();
		}
		else {
			this.serviceConnection.service.stopDownloads();
		}
	}
	
	public void addDownload(DownloadPackage pack) {
		if (this.serviceConnection.bound == false) {
			this.serviceConnection.newPackage = pack;
			this.bindDownloadService();
		}
		else {
			this.serviceConnection.service.addDownload(pack);
		}
	}
	
	public void getServiceMap() {
		this.getServiceMap(true);
	}
	
	public void getServiceMap(boolean addListeners) {
		if (this.serviceConnection.bound == false) {
			this.serviceConnection.executeGetDownloadMapCallback = true;
			this.serviceConnection.addListernersAtGetDownloadMapCallback = addListeners;
			this.bindDownloadService();
		}
		else {
			this.getServiceDownloadMap(false);
		}
	}
	
	public void removeActivityCallbacks() {
		if (DownloadService.Started == true) {
			if (this.serviceConnection.bound == false) {
				this.serviceConnection.executeRemoveCallback = true;
				this.bindDownloadService();
			}
			else {
				this.removeCallbacksAndUnbind();
			}
		}
	}
	
	public abstract void startServiceDownload();
	
	public void startServiceDownload(IDownloadProgressCallback progressCallback, IDownloadFinishedCallback finishedCallback, IDownloadCancelledCallback cancelledCallback) {
		if (DownloadService.Processing == true) {
			this.getServiceDownloadMap();
		}
		else {
			this.serviceConnection.service.startDownload(this.getDownloads());
			this.serviceConnection.service.addDownloadListeners(CallbackType.UI, progressCallback, finishedCallback, cancelledCallback);
		}
	}
	
	public abstract void getServiceDownloadMap(boolean addListener);
	
	public void getServiceDownloadMap(IDownloadProgressCallback progressCallback, IDownloadFinishedCallback finishedCallback, IDownloadCancelledCallback cancelledCallback) {
		this.serviceConnection.service.addDownloadListeners(CallbackType.UI, progressCallback, finishedCallback, cancelledCallback);
		
		this.getServiceDownloadMap();
	}
	
	public void getServiceDownloadMap() {
		DownloadMap serviceMap = this.serviceConnection.service.getMap();
		
		if (serviceMap != null && serviceMap.size() != 0)
			this.setDownloads(serviceMap);
	}
	
	public void removeCallbacksAndUnbind() {
		this.serviceConnection.service.removeDownloadListeners(CallbackType.UI);
		
		this.unbindDownloadService();
	}
	
	protected void loadDownloadsMapFromCacheFile() {
		this.getDownloads().deserializeMapFromCache(this.getApplicationContext(), this);
	}

	protected void loadDownloadsMapFromCacheFile(IDeserializeMapFinishedCallback callback) {
		this.getDownloads().deserializeMapFromCache(this.getApplicationContext(), callback);
	}
	
	@Override
	public abstract void updateWithImage(ImageView view, Bitmap bitmap);

	@Override
	public abstract void updateWithJsonArray(JsonArray result);

	@Override
	public abstract void updateWithJsonObject(JsonObject result);
	
	public void iterateDownloadsToInstall() {
		for (; this.currentInstallIndex < this.getDownloads().size(); this.currentInstallIndex++) {
			DownloadPackage pack = this.getDownloads().get(this.currentInstallIndex);
			
			Log.e(TAG, "iterate == " + pack);
			
			if (pack != null && pack.installHandler != null) {
				
				pack.installHandler.setParentActivity(this);
				
				pack.installHandler.install();
				
				if (pack.installHandler instanceof IActivityInstallDownloadHandler) {
					this.currentInstallIndex++;
					break;
				}
			}
		}
		
		if (this.currentInstallIndex == this.getDownloads().size() && this.recoveryScript != null) {
			this.recoveryScript.executeAndReboot();
		}
	}
	
	public void installFiles() {		
		DownloadPackage pack = this.getDownloads().get(this.currentInstallIndex, DownloadType.zip);
		
		if (pack != null) {
			this.showFlashOptionsDialog();
		}
		else {	
			this.iterateDownloadsToInstall();
		}
	}
	
	public void showFlashOptionsDialog() {
		FlashConfigurationDialog dialog = new FlashConfigurationDialog();
		dialog.show(getFragmentManager(), this.getString(R.string.download_flash_options_title));
	}
	
	public void installZips(ArrayList<Integer> list) {
		OpenRecoveryScriptConfiguration config = new OpenRecoveryScriptConfiguration(this.getDownloadDir());
		
		for (Integer option: list){			
			if (option.equals(Integer.valueOf(0))) {
				config.backupFirst = true;
			}
			if (option.equals(Integer.valueOf(1))) {
				config.wipeData = true;
			}
			if (option.equals(Integer.valueOf(2))) {
				config.includeMd5mismatch = true;
			}
		}
		
		if (!list.contains(Integer.valueOf(0))) {
			config.backupFirst = false;
		}
		
		this.recoveryScript = new OpenRecoveryScript(config);
		
		this.iterateDownloadsToInstall();
	}
	
	public void restoreDownloadMapFromCache() {
		this.restoreDownloadMapFromCache(null);
	}
	
	public void restoreDownloadMapFromCache(IDeserializeMapFinishedCallback callback) {
		if (!this.downloadMapRestored) {
			this.getDownloads().clear();
			
			if (callback == null) {
				this.loadDownloadsMapFromCacheFile(callback);
			}
			else {
				this.loadDownloadsMapFromCacheFile();
			}
			
			this.downloadMapRestored = true;
		}
	}
}

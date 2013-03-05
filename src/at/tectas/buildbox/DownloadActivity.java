package at.tectas.buildbox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import at.tectas.buildbox.communication.Communicator;
import at.tectas.buildbox.communication.DownloadKey;
import at.tectas.buildbox.communication.DownloadMap;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.Communicator.CallbackType;
import at.tectas.buildbox.communication.DownloadResponse.DownloadStatus;
import at.tectas.buildbox.communication.ICommunicatorCallback;
import at.tectas.buildbox.communication.IDownloadCancelledCallback;
import at.tectas.buildbox.communication.IDownloadFinishedCallback;
import at.tectas.buildbox.communication.IDownloadProgressCallback;
import at.tectas.buildbox.listeners.DownloadBaseCallback;
import at.tectas.buildbox.service.DownloadService;

public abstract class DownloadActivity extends FragmentActivity implements ICommunicatorCallback {

	public static final String TAG = "DownloadActivity";
	
	protected Communicator communicator = new Communicator();
	protected DownloadMap downloads = new DownloadMap();
	protected DownloadBaseCallback callback = null;
	public DownloadServiceConnection serviceConnection = new DownloadServiceConnection(this);
	
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
			this.stopServiceDownload();
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
	
	public void stopServiceDownload() {
		this.serviceConnection.service.stopDownloads();
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
		BufferedReader stream = null;
		
		String[] files = this.fileList();
		
		boolean exists = false;
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].equals(this.getString(R.string.downloads_cach_filename))) {
				exists = true;
				break;
			}
		}
		
		if (exists == true) {
			try {
				stream = new BufferedReader(new InputStreamReader(openFileInput(getString(R.string.downloads_cach_filename))));
		
				StringBuilder builder = new StringBuilder();
				String line = "";
		
				while ((line = stream.readLine()) != null) 
				{
					builder.append(line);
				}
				
		        stream.close();
		        
		        JsonParser parser = new JsonParser();
		        
		        JsonArray elements = parser.parse(builder.toString()).getAsJsonArray();
				
		        this.setDownloads(DownloadMap.getDownloadMapFromJson(elements));
		        
		        this.deleteFile(getString(R.string.downloads_cach_filename));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public abstract void updateWithImage(ImageView view, Bitmap bitmap);

	@Override
	public abstract void updateWithJsonArray(JsonArray result);

	@Override
	public abstract void updateWithJsonObject(JsonObject result);
}

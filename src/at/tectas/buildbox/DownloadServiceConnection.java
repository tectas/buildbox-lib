package at.tectas.buildbox;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.service.DonwloadServiceBinder;
import at.tectas.buildbox.service.DownloadService;

public class DownloadServiceConnection implements ServiceConnection {
	
	public DownloadService service = null;
	public DownloadActivity activity = null;
	public boolean bound = false;
	public boolean downloadStarted = false;
	public boolean executeStartDownloadCallback = false;
	public boolean executeStopDownloadCallback = false;
	public boolean executeGetDownloadMapCallback = false;
	public boolean executeRemoveCallback = false;
	public boolean addListernersAtGetDownloadMapCallback = false;
	public DownloadPackage newPackage = null;
	
	public DownloadServiceConnection (DownloadActivity activity) {
		this.activity = activity;
	}
	
    public void onServiceConnected(ComponentName className, IBinder service) {
	    DonwloadServiceBinder binder = (DonwloadServiceBinder) service;
	    this.bound = true;
	    this.service = (DownloadService) binder.getservice();
	    
	    if (this.executeStartDownloadCallback == true) {
	    	this.activity.startServiceDownload();
	    	this.executeStartDownloadCallback = false;
	    }
	    else if (this.executeStopDownloadCallback == true) {
	    	this.activity.stopDownload();
	    	this.executeStopDownloadCallback = false;
	    }
	    else if (this.executeGetDownloadMapCallback == true) {
	    	this.activity.getServiceDownloadMap(this.addListernersAtGetDownloadMapCallback);
	    	this.executeGetDownloadMapCallback = false;
	    	this.addListernersAtGetDownloadMapCallback = false;
	    }
	    else if (this.executeRemoveCallback == true) {
	    	this.activity.removeCallbacksAndUnbind();
	    	this.executeRemoveCallback = false;
	    }
	    else if (this.newPackage != null) {
	    	this.activity.addDownload(newPackage);
	    	this.newPackage = null;
	    }
	}
	
    public void startDownload() {
    	this.activity.startServiceDownload();
    }
    
    public void stopDownload() {
    	this.activity.stopDownload();
    }
    
    public void getServiceDownloadMap() {
    	this.activity.getServiceMap();
    }
    
	public void onServiceDisconnected(ComponentName className) {
		this.bound = false;
		this.service = null;
	}
}

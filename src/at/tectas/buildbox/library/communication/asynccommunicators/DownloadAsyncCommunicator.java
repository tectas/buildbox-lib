package at.tectas.buildbox.library.communication.asynccommunicators;

import java.util.Hashtable;

import at.tectas.buildbox.library.communication.Communicator;
import at.tectas.buildbox.library.communication.DownloadPackage;
import at.tectas.buildbox.library.communication.DownloadResponse;
import at.tectas.buildbox.library.communication.DownloadStatus;
import at.tectas.buildbox.library.communication.asynccommunicators.interfaces.IDownloadAsyncCommunicator;
import at.tectas.buildbox.library.communication.callbacks.CallbackType;
import at.tectas.buildbox.library.communication.callbacks.interfaces.IDownloadCancelledCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.IDownloadFinishedCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.IDownloadProgressCallback;

public class DownloadAsyncCommunicator extends IDownloadAsyncCommunicator {
	
	public String ID = null;
	private Communicator communicator = null;
	private Hashtable<CallbackType, IDownloadProgressCallback> updateListener = new Hashtable<CallbackType, IDownloadProgressCallback>();
	private Hashtable<CallbackType, IDownloadFinishedCallback> finishedListener = new Hashtable<CallbackType, IDownloadFinishedCallback>();
	private Hashtable<CallbackType, IDownloadCancelledCallback> cancelledListener = new Hashtable<CallbackType, IDownloadCancelledCallback>();
	
	private DownloadAsyncCommunicator (Communicator communicator, String ID) {
		this.ID = ID;
		this.communicator = communicator;
	}
	
	public DownloadAsyncCommunicator (Communicator communicator, String ID, IDownloadProgressCallback updateCallback, IDownloadFinishedCallback finishedCallback, IDownloadCancelledCallback cancelCallback) {
		this(communicator, ID);
		this.updateListener.put(CallbackType.Service, updateCallback);
		
		this.finishedListener.put(CallbackType.Service, finishedCallback);
		
		this.cancelledListener.put(CallbackType.Service, cancelCallback);
	}
	
	public DownloadAsyncCommunicator (Communicator communicator, String ID, Hashtable<CallbackType, IDownloadProgressCallback> updateCallback, Hashtable<CallbackType, IDownloadFinishedCallback> finishedCallback, Hashtable<CallbackType, IDownloadCancelledCallback> cancelCallback) {
		this(communicator, ID);
		this.updateListener = updateCallback;
		this.finishedListener = finishedCallback;
		this.cancelledListener = cancelCallback;
	}
	
	public synchronized boolean removeProgressListener (CallbackType type) {
		if (this.updateListener.containsKey(type)) {
			IDownloadProgressCallback removedCallback = this.updateListener.remove(type);
			if (removedCallback != null) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public synchronized boolean addProgressListener (CallbackType type, IDownloadProgressCallback callback) {
		if (this.updateListener.containsKey(type)) {
			this.updateListener.remove(type);
		}
		
		IDownloadProgressCallback newCallback = this.updateListener.put(type, callback);
		
		if (newCallback != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public synchronized boolean removeResultListener(CallbackType type) {
		if (this.finishedListener.containsKey(type)) {
			
			IDownloadFinishedCallback removedCallback = this.finishedListener.remove(type);
			
			if (removedCallback != null) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public synchronized boolean addResultListener(CallbackType type, IDownloadFinishedCallback callback) {
		if (this.finishedListener.containsKey(type))
			this.finishedListener.remove(type);
		
		IDownloadFinishedCallback newCallback = this.finishedListener.put(type, callback);
			
		if (newCallback != null) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	public synchronized boolean removeCancelledListener (CallbackType type) {
		if (this.cancelledListener.containsKey(type)) {
			IDownloadCancelledCallback removedCallback = this.cancelledListener.remove(type);
			if (removedCallback != null) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public synchronized boolean addCancelledListener (CallbackType type, IDownloadCancelledCallback callback) {
		if (this.cancelledListener.containsKey(type)) {
			this.cancelledListener.remove(type);
		}
		
		IDownloadCancelledCallback newCallback = this.cancelledListener.put(type, callback);
		
		if (newCallback != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected DownloadResponse doInBackground(DownloadPackage... params) {
		try {
			return this.communicator.downloadFileToSd(params[0], this);
		}
		catch (Exception e) {
			return new DownloadResponse();
		}
	}
	
	public void indirectPublishProgress(DownloadResponse response) {
		this.publishProgress(response);
	}
	
	protected void onProgressUpdate(DownloadResponse... response) {
		if (this.updateListener != null && this.updateListener.size() != 0)
			for (CallbackType callbackKey: this.updateListener.keySet()) {
				
				IDownloadProgressCallback listener = this.updateListener.get(callbackKey);
				
				if (listener != null) {
					listener.updateDownloadProgess(response[0]);
				}
			}
     }
	
	@Override
	protected void onPostExecute(DownloadResponse result) {
		if (this.finishedListener != null && this.finishedListener.size() != 0) {
			for (CallbackType callbackKey: this.finishedListener.keySet()) {
				
				IDownloadFinishedCallback listener = this.finishedListener.get(callbackKey);
				
				if (listener != null) {
					listener.downloadFinished(result);
				}
			}
		}
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled(DownloadResponse result) {
		
		if (result == null) {
			result = new DownloadResponse(new DownloadPackage(), DownloadStatus.Aborted);
			
			if (this.ID.contains("/")) {
				result.pack.url = this.ID;
			}
			else {
				result.pack.md5sum = this.ID;
			}
		}
		else {
			if (result.pack == null) {
				result.status = DownloadStatus.Aborted;
				result.pack = new DownloadPackage();
				
				if (this.ID.contains("/")) {
					result.pack.url = this.ID;
				}
				else {
					result.pack.md5sum = this.ID;
				}
			}
		}
		
		for (CallbackType callbackKey: this.cancelledListener.keySet()) {
			
			IDownloadCancelledCallback listener = this.cancelledListener.get(callbackKey);
			
			if (listener != null) {
				listener.downloadCancelled(result);
			}
		}
	}
}

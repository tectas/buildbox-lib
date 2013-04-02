package at.tectas.buildbox.communication.callbacks.interfaces;

import at.tectas.buildbox.download.DownloadActivity;

public abstract class DownloadBaseCallback implements IDownloadProgressCallback, IDownloadFinishedCallback, IDownloadCancelledCallback {
	protected DownloadActivity activity = null;
	
	protected DownloadBaseCallback(DownloadActivity activity) {
		this.activity = activity;
	}
}

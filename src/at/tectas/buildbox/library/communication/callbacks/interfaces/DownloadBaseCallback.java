package at.tectas.buildbox.library.communication.callbacks.interfaces;

import at.tectas.buildbox.library.download.DownloadActivity;

public abstract class DownloadBaseCallback implements IDownloadProgressCallback, IDownloadFinishedCallback, IDownloadCancelledCallback {
	protected DownloadActivity activity = null;
	
	protected DownloadBaseCallback(DownloadActivity activity) {
		this.activity = activity;
	}
}

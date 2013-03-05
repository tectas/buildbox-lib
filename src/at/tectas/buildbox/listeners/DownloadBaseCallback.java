package at.tectas.buildbox.listeners;

import at.tectas.buildbox.DownloadActivity;
import at.tectas.buildbox.communication.IDownloadCancelledCallback;
import at.tectas.buildbox.communication.IDownloadFinishedCallback;
import at.tectas.buildbox.communication.IDownloadProgressCallback;

public abstract class DownloadBaseCallback implements IDownloadProgressCallback, IDownloadFinishedCallback, IDownloadCancelledCallback {
	protected DownloadActivity activity = null;
	
	protected DownloadBaseCallback(DownloadActivity activity) {
		this.activity = activity;
	}
}

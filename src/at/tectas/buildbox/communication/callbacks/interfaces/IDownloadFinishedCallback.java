package at.tectas.buildbox.communication.callbacks.interfaces;

import at.tectas.buildbox.communication.DownloadResponse;

public interface IDownloadFinishedCallback {
	public void downloadFinished(DownloadResponse response);
}

package at.tectas.buildbox.library.communication.callbacks.interfaces;

import at.tectas.buildbox.library.communication.DownloadResponse;

public interface IDownloadFinishedCallback {
	public void downloadFinished(DownloadResponse response);
}

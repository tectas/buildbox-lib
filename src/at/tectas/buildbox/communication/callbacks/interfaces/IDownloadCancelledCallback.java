package at.tectas.buildbox.communication.callbacks.interfaces;

import at.tectas.buildbox.communication.DownloadResponse;

public interface IDownloadCancelledCallback {
	public void downloadCancelled(DownloadResponse response);
}

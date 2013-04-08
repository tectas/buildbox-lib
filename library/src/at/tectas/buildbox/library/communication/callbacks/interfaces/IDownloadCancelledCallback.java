package at.tectas.buildbox.library.communication.callbacks.interfaces;

import at.tectas.buildbox.library.communication.DownloadResponse;

public interface IDownloadCancelledCallback {
	public void downloadCancelled(DownloadResponse response);
}

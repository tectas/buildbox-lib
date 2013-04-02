package at.tectas.buildbox.communication.callbacks.interfaces;

import at.tectas.buildbox.communication.DownloadResponse;

public interface IDownloadProgressCallback {
	public void updateDownloadProgess(DownloadResponse response);
}

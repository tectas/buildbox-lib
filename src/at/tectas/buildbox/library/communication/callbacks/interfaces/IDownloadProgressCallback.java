package at.tectas.buildbox.library.communication.callbacks.interfaces;

import at.tectas.buildbox.library.communication.DownloadResponse;

public interface IDownloadProgressCallback {
	public void updateDownloadProgess(DownloadResponse response);
}

package at.tectas.buildbox.communication;

import android.os.AsyncTask;

public abstract class IDownloadAsyncCommunicator extends AsyncTask<String, DownloadResponse, DownloadResponse> {
	public abstract void indirectPublishProgress(DownloadResponse response);
}

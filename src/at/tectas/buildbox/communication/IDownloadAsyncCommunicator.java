package at.tectas.buildbox.communication;

import android.os.AsyncTask;

public abstract class IDownloadAsyncCommunicator extends AsyncTask<String, Integer, DownloadResponse> {
	public abstract void indirectPublishProgress(Integer progress);
}

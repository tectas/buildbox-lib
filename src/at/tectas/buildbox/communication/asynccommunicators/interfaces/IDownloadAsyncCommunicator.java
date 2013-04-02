package at.tectas.buildbox.communication.asynccommunicators.interfaces;

import android.os.AsyncTask;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.DownloadResponse;

public abstract class IDownloadAsyncCommunicator extends AsyncTask<DownloadPackage, DownloadResponse, DownloadResponse> {
	public abstract void indirectPublishProgress(DownloadResponse response);
}

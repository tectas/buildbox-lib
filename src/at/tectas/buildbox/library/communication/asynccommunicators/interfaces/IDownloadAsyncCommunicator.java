package at.tectas.buildbox.library.communication.asynccommunicators.interfaces;

import android.os.AsyncTask;
import at.tectas.buildbox.library.communication.DownloadPackage;
import at.tectas.buildbox.library.communication.DownloadResponse;

public abstract class IDownloadAsyncCommunicator extends AsyncTask<DownloadPackage, DownloadResponse, DownloadResponse> {
	public abstract void indirectPublishProgress(DownloadResponse response);
}

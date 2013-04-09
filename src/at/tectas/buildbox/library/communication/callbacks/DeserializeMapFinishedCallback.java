package at.tectas.buildbox.library.communication.callbacks;

import at.tectas.buildbox.library.communication.callbacks.interfaces.IDeserializeMapFinishedCallback;
import at.tectas.buildbox.library.download.DownloadActivity;

public class DeserializeMapFinishedCallback implements IDeserializeMapFinishedCallback {
	
	protected DownloadActivity activity = null;
	
	public DeserializeMapFinishedCallback(DownloadActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public void mapDeserializedCallback() {
		activity.iterateDownloadsToInstall();
	}
}

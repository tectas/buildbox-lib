package at.tectas.buildbox.communication.callbacks;

import at.tectas.buildbox.communication.callbacks.interfaces.IDeserializeMapFinishedCallback;
import at.tectas.buildbox.download.DownloadActivity;

public class MapDeserializedProcessCallback implements IDeserializeMapFinishedCallback {
	
	protected DownloadActivity activity = null;
	
	public MapDeserializedProcessCallback(DownloadActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public void mapDeserializedCallback() {
		activity.iterateDownloadsToInstall();
	}
}

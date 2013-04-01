package at.tectas.buildbox.listeners;

import at.tectas.buildbox.DownloadActivity;
import at.tectas.buildbox.communication.IDeserializeMapFinishedCallback;

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

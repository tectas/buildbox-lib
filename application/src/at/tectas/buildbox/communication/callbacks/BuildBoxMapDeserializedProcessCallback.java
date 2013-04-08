package at.tectas.buildbox.communication.callbacks;

import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.library.communication.callbacks.MapDeserializedProcessCallback;
import at.tectas.buildbox.library.download.DownloadActivity;

public class BuildBoxMapDeserializedProcessCallback extends MapDeserializedProcessCallback {

	private BuildBoxMapDeserializedProcessCallback(DownloadActivity activity) {
		super(activity);
	}
	
	public BuildBoxMapDeserializedProcessCallback(BuildBoxMainActivity activity) {
		super(activity);
	}
	
	@Override
	public void mapDeserializedCallback() {
		if (this.activity instanceof BuildBoxMainActivity) {
			BuildBoxMainActivity buildBoxActivity = (BuildBoxMainActivity) this.activity;
			
			buildBoxActivity.refreshDownloadsView();
		}
		
		super.mapDeserializedCallback();
	}
}

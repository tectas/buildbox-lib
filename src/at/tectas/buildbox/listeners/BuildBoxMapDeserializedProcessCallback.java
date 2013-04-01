package at.tectas.buildbox.listeners;

import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.DownloadActivity;

public class BuildBoxMapDeserializedProcessCallback extends MapDeserializedProcessCallback {

	private BuildBoxMapDeserializedProcessCallback(DownloadActivity activity) {
		super(activity);
	}
	
	public BuildBoxMapDeserializedProcessCallback(BuildBoxMainActivity activity) {
		super(activity);
	}
	
	@Override
	public void mapDeserializedCallback() {
		super.mapDeserializedCallback();
		
		if (this.activity instanceof BuildBoxMainActivity) {
			BuildBoxMainActivity buildBoxActivity = (BuildBoxMainActivity) this.activity;
			
			buildBoxActivity.addDownloadsTab();
		}
	}
}

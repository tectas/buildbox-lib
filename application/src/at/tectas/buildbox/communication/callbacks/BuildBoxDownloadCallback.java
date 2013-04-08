package at.tectas.buildbox.communication.callbacks;

import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.library.communication.DownloadResponse;
import at.tectas.buildbox.library.communication.callbacks.interfaces.DownloadBaseCallback;
import at.tectas.buildbox.library.download.DownloadActivity;

public class BuildBoxDownloadCallback extends DownloadBaseCallback {	
	public BuildBoxDownloadCallback (DownloadActivity activity) {
		super(activity);
	}
	
	protected void updateActivtiyList() {
		((BuildBoxMainActivity)this.activity).getDownloadsMapandUpdateList();
	}
	
	@Override
	public void updateDownloadProgess(DownloadResponse response) {
		this.updateActivtiyList();
	}
	
	@Override
	public void downloadCancelled(DownloadResponse response) {
		this.updateActivtiyList();
	}
	
	@Override
	public void downloadFinished(DownloadResponse response) {
		this.updateActivtiyList();
	}
}

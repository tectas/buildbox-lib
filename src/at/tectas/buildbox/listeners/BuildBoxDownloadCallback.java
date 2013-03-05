package at.tectas.buildbox.listeners;

import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.DownloadActivity;
import at.tectas.buildbox.communication.DownloadResponse;

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

package at.tectas.buildbox.recovery;

import at.tectas.buildbox.communication.DownloadMap;

public class OpenRecoveryScriptConfiguration {
	public boolean wipeData = false;
	public boolean includeMd5mismatch = false;
	public boolean backupFirst = true;
	public String directoryPath = null;
	public DownloadMap downloads = null;
	
	public OpenRecoveryScriptConfiguration (String directoryPath, DownloadMap downloads) {
		this.directoryPath = directoryPath;
		this.downloads = downloads;
	}
}

package at.tectas.buildbox.communication;

import at.tectas.buildbox.recovery.OpenRecoveryScript;

public class ZipInstallHandler implements IInstallDownloadHandler {
	public DownloadPackage packag = null;
	public OpenRecoveryScript script = null;
	
	public ZipInstallHandler (OpenRecoveryScript script, DownloadPackage packag) {
		this.script = script;
		this.packag = packag;
	}
	
	@Override
	public void installDownload() {
		if (this.script != null && this.packag != null) {
			this.script.addScriptHead();
			this.script.addIntallToScript(this.packag);
		}
	}
}

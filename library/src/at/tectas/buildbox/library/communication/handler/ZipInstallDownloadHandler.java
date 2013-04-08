package at.tectas.buildbox.library.communication.handler;

import android.app.Activity;
import at.tectas.buildbox.library.communication.DownloadPackage;
import at.tectas.buildbox.library.communication.handler.interfaces.IInstallDownloadHandler;
import at.tectas.buildbox.library.download.DownloadActivity;
import at.tectas.buildbox.library.recovery.OpenRecoveryScript;

public class ZipInstallDownloadHandler implements IInstallDownloadHandler {
	public DownloadPackage packag = null;
	public OpenRecoveryScript script = null;
	
	@Override
	public void setParentActivity(Activity activity) {
		if (activity instanceof DownloadActivity) {
			this.script = ((DownloadActivity)activity).getOpenRecoveryScript();
		}
	}
	
	public ZipInstallDownloadHandler (DownloadPackage packag) {
		this.packag = packag;
	}
	
	@Override
	public void install() {
		if (this.script != null && this.packag != null) {
			this.script.addScriptHead();
			this.script.addIntallToScript(this.packag);
		}
	}
}

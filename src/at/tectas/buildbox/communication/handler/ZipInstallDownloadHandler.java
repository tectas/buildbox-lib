package at.tectas.buildbox.communication.handler;

import android.app.Activity;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.handler.interfaces.IInstallDownloadHandler;
import at.tectas.buildbox.recovery.OpenRecoveryScript;

public class ZipInstallDownloadHandler implements IInstallDownloadHandler {
	public DownloadPackage packag = null;
	public OpenRecoveryScript script = null;
	
	@Override
	public void setParentActivity(Activity activity) {
		if (activity instanceof BuildBoxMainActivity) {
			this.script = ((BuildBoxMainActivity)activity).getOpenRecoveryScript();
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

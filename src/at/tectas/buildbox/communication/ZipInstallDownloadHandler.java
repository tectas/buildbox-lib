package at.tectas.buildbox.communication;

import android.app.Activity;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.recovery.OpenRecoveryScript;

public class ZipInstallDownloadHandler implements IInstallDownloadHandler {
	public DownloadPackage packag = null;
	public OpenRecoveryScript script = null;
	
	@Override
	public void setParentActivity(Activity activity) {
		if (activity instanceof BuildBoxMainActivity) {
			this.script = ((BuildBoxMainActivity)activity).recoveryScript;
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

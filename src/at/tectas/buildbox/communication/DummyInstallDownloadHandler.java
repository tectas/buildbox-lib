package at.tectas.buildbox.communication;

import android.app.Activity;

public class DummyInstallDownloadHandler implements IInstallDownloadHandler {

	@Override
	public void install() {
		return;
	}

	@Override
	public void setParentActivity(Activity activity) {
		return;
	}

}

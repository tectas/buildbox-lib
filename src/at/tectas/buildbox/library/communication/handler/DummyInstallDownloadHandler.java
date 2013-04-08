package at.tectas.buildbox.library.communication.handler;

import android.app.Activity;
import at.tectas.buildbox.library.communication.handler.interfaces.IInstallDownloadHandler;

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

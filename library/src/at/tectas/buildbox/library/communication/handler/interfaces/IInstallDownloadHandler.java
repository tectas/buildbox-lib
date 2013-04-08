package at.tectas.buildbox.library.communication.handler.interfaces;

import android.app.Activity;

public interface IInstallDownloadHandler {
	public void setParentActivity(Activity activity);
	public void install();
}

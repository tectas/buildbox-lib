package at.tectas.buildbox.communication;

import android.app.Activity;

public interface IInstallDownloadHandler {
	public void setParentActivity(Activity activity);
	public void install();
}

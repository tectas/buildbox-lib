package at.tectas.buildbox.helpers;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import at.tectas.buildbox.adapters.DownloadPackageAdapter;
import at.tectas.buildbox.content.ItemList;

public class SharedObjectsHelper {

	public static ItemList contentItems = null;
	public static int viewPagerIndex = 0;
	public static Fragment fragment = null;
	public static Hashtable<String, Bitmap> remoteDrawables = new Hashtable<String, Bitmap>();
	public static DownloadMap downloads = new DownloadMap();
	public static DownloadPackageAdapter downloadAdapter = null;
	public static boolean downloadTabAdded = false;
	
	public static ItemList getContentItems() {
		return contentItems;
	}
}

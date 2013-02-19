package at.tectas.buildbox.helpers;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import at.tectas.buildbox.content.ItemList;

public class SharedObjectsHelper {

	public static ItemList contentItems = null;
	public static int viewPagerIndex = 0;
	public static Fragment fragment = null;
	public static Hashtable<String, Bitmap> remoteDrawables = new Hashtable<String, Bitmap>();
	
	public static ItemList getContentItems() {
		return contentItems;
	}
}

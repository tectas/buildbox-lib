package at.tectas.buildbox.library.helpers;

import java.util.HashSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import at.tectas.buildbox.R;
import at.tectas.buildbox.library.content.items.properties.DownloadType;

public class PropertyHelper {
	
	public Context context = null;
	public SharedPreferences pref = null;
	public String romUrl = null;
	public String presetContentUrl = null;
	public String version = null;
	public String downloadDir = null;
	public String deviceModel = null;
	public DownloadType type = null;
	public HashSet<String> contentUrls = null;
	
	public PropertyHelper (Context context) {
		this.context = context;
		this.pref = PreferenceManager.getDefaultSharedPreferences(this.context);
		this.romUrl = this.getRomUrl();
		this.presetContentUrl = this.getPresetContentUrl();
		this.version = this.getVersion();
		this.downloadDir = this.getDownloadDirectory();
		this.deviceModel = this.getDeviceModel();
		this.type = this.getDownloadType();
		this.contentUrls = this.getUserContentUrls();
	}
	
	public String getRomUrl() {
		String rom = null;
		
		if (pref.getBoolean(this.context.getString(R.string.preference_ignore_build_prop), false) == false) {
			rom = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_rom_url_property));
		}
			
		rom = (PropertyHelper.stringIsNullOrEmpty(rom) == true? context.getString(R.string.default_rom_url): rom);
		
		return rom;
	}
	
	public String getPresetContentUrl() {
		String content = null;
		
		if (pref.getBoolean(this.context.getString(R.string.preference_ignore_build_prop), false) == false) {
			content = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_content_url_property));
		}
		
		content = (PropertyHelper.stringIsNullOrEmpty(content)? context.getString(R.string.default_content_url): content);
		
		return content;
	}
	
	public HashSet<String> getUserContentUrls() {

		return new HashSet<String>(pref.getStringSet(context.getString(R.string.preference_content_urls_property), new HashSet<String>()));
	}
 	
	public String getVersion() {
		String version = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_rom_version_property));
		
		return version;
	}
	
	public String getDownloadDirectory() {
		String downloadDir = pref.getString(context.getString(R.string.preference_dir_property), null);
		
		if (downloadDir == null) {		
			downloadDir = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_download_dir));
			
			if (PropertyHelper.stringIsNullOrEmpty(downloadDir)) {
				downloadDir = Environment.getExternalStorageDirectory().getPath() + "/" + context.getString(R.string.default_sd_directory);
			}
			
			Editor editor = pref.edit();
			
			editor.putString(context.getString(R.string.preference_dir_property), downloadDir);
			
			editor.commit();
		}
		
		if (!downloadDir.endsWith("/")) {
			downloadDir = downloadDir + "/";
		}
		
		return downloadDir;
	}
	
	public String getDeviceModel() {
		return ShellHelper.getBuildPropProperty(context.getString(R.string.device_model_property));
	}
	
	public DownloadType getDownloadType() {
		try {
			String downloadType = ShellHelper.getBuildPropProperty(context.getString(R.string.item_download_default_type));
			
			if (!PropertyHelper.stringIsNullOrEmpty(downloadType))
				return DownloadType.valueOf(downloadType);
			else
				return null;
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static int compareVersions(String localVersion, String remoteVersion) {
		if ((PropertyHelper.stringIsNullOrEmpty(localVersion) == true) && (PropertyHelper.stringIsNullOrEmpty(remoteVersion) == true))
			return 0;
			
		if (PropertyHelper.stringIsNullOrEmpty(localVersion) == true)
			return 1;
		
		if (PropertyHelper.stringIsNullOrEmpty(remoteVersion) == true)
			return -1;
		
		if (localVersion.equals(remoteVersion))
			return 0;
		
		String[] localVersionArray = localVersion.split("\\.");
		String[] remoteVersionArray = remoteVersion.split("\\.");
		
		for (int i = 0; i < localVersionArray.length && i < remoteVersionArray.length; i++) {
			boolean localIsLonger = localVersionArray[i].length() > remoteVersionArray[i].length();
			
			String longer = localIsLonger == true ? localVersionArray[i] : remoteVersionArray[i];
			String shorter = localIsLonger == false ? localVersionArray[i] : remoteVersionArray[i];
			
			for (int k = shorter.length(); k < longer.length(); k++) {
				
				if (localIsLonger == true) {
					remoteVersionArray[i] = "0" + remoteVersionArray[i];
				}
				else {
					localVersionArray[i] = "0" + localVersionArray[i];
				}
			}
			
			if (localVersionArray[i].equals(remoteVersionArray[i])) {
				continue;
			}
			else {
				if (localVersionArray[i].length() < remoteVersionArray[i].length()) {
					return 1;
				}
				else if (localVersionArray[i].length() > remoteVersionArray[i].length()) {
					return -1;
				}
				else {
					for (int j = 0; j < localVersionArray[i].length() && j < remoteVersionArray[i].length(); j++) {
						if (localVersionArray[i].charAt(j) == remoteVersionArray[i].charAt(j)) {
							continue;
						}
						else if (localVersionArray[i].charAt(j) > remoteVersionArray[i].charAt(j)) {
							return -1;
						}
						else {
							return 1;
						}
					}
				}
			}
		}
		
		boolean localIsLonger = localVersionArray.length > remoteVersionArray.length;
		
		String[] longerArray = localIsLonger == true ? localVersionArray : remoteVersionArray;
		String[] shorterArray = localIsLonger == false ? localVersionArray : remoteVersionArray;
		
		for (int i = shorterArray.length; i < longerArray.length; i++) {
			
			if (longerArray[i].equals("0")) {
				continue;
			}
			else {
				if (longerArray[i].length() == 1) {
					return 1;
				}
				else {
					for (int j = 0; j < longerArray[i].length(); j++) {
						if (longerArray[i].charAt(j) == '0') {
							continue;
						}
						else {
							if (localIsLonger == true) {
								return -1;
							}
							else {
								return 1;
							}
						}
					}
				}
			}
		}
		
		return 0;
	}
	
	public static boolean stringIsNullOrEmpty(String string) {
		return (string == null || string.isEmpty() || string.length() == 0);
	}
}

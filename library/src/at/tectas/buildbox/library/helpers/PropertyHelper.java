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
	
	public PropertyHelper (Context context) {
		this.context = context;
		this.pref = PreferenceManager.getDefaultSharedPreferences(this.context);
		this.romUrl = this.getRomUrl();
		this.presetContentUrl = this.getPresetContentUrl();
		this.version = this.getVersion();
		this.downloadDir = this.getDownloadDirectory();
		this.deviceModel = this.getDeviceModel();
		this.type = this.getDownloadType();
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
	
	public static boolean stringIsNullOrEmpty(String string) {
		return (string == null || string.isEmpty() || string.length() == 0);
	}
}

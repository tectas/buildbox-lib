package at.tectas.buildbox.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import at.tectas.buildbox.R;

public class PropertyHelper {
	
	public Context context = null;
	
	public SharedPreferences pref = null;
	
	public PropertyHelper (Context context) {
		this.context = context;
		this.pref = PreferenceManager.getDefaultSharedPreferences(this.context);
	}
	
	public String getRomUrl() {
		String rom = null;
		
		if (pref.getBoolean(this.context.getString(R.string.preference_ignore_build_prop), false) == false) {
			rom = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_rom_url_property));
		}
			
		rom = (PropertyHelper.stringIsNullOrEmpty(rom) == true? context.getString(R.string.default_rom_url): rom);
		
		return rom;
	}
	
	public String getContentUrl() {
		String content = null;
		
		if (pref.getBoolean(this.context.getString(R.string.preference_ignore_build_prop), false) == false) {
			content = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_content_url_property));
		}
		
		content = (PropertyHelper.stringIsNullOrEmpty(content) == true? context.getString(R.string.default_content_url): content);
		
		return content;
	}
	
	public String getVersion() {
		String version = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_rom_version_property));
		
		return version;
	}
	
	public String getDownloadDirectory() {
		String downloadDir = pref.getString(context.getString(R.string.preference_dir_property), null);
		
		if (downloadDir == null) {		
			downloadDir = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_download_dir));
			
			if (PropertyHelper.stringIsNullOrEmpty(downloadDir) == true) {
				downloadDir = Environment.getExternalStorageDirectory().getPath() + "/" + context.getString(R.string.default_sd_directory);
			}
			
			Editor editor = pref.edit();
			
			editor.putString(context.getString(R.string.preference_dir_property), downloadDir);
			
			editor.commit();
		}
		
		return downloadDir;
	}
	
	public static boolean stringIsNullOrEmpty(String string) {
		return (string == null || string.isEmpty() || string.length() == 0);
	}
}

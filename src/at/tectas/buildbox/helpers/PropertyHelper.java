package at.tectas.buildbox.helpers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import at.tectas.buildbox.R;

public class PropertyHelper {
	
	private static final String TAG = "PropertyHelper";
	public Context context = null;
	
	public PropertyHelper (Context context) {
		this.context = context;
	}
	
	public String getRomUrl() {
		String rom = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_rom_url_property));
		
		rom = (rom == null? context.getString(R.string.default_rom_url): rom);
		
		return rom;
	}
	
	public String getContentUrl() {
		String content = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_content_url_property));
		
		content = (content == null ? context.getString(R.string.default_content_url): content);
		
		return content;
	}
	
	public String getVersion() {
		String version = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_rom_version_property));
		
		return version;
	}
	
	public String getDownloadDirectory() {
		String downloadDir = ShellHelper.getBuildPropProperty(context.getString(R.string.kitchen_download_dir));
		
		if (downloadDir == null) {
			downloadDir = Environment.getExternalStorageDirectory().getPath() + "/" + context.getString(R.string.default_sd_directory);
		}
		
		return downloadDir;
	}
}

package at.tectas.buildbox;

import java.io.File;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class BuildBoxPreferenceActivity extends PreferenceActivity {
	protected static final String TAG = "BuildBoxPreferenceActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.activity_preferences);
		
		EditTextPreference dirPreference = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.preference_dir_property));
		
		final BuildBoxPreferenceActivity activity = this;
		
		dirPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String value = String.valueOf(newValue);
				
				boolean accessible = activity.checkDirectoryPathAccess(value);
				
				if (accessible == false) {
					Toast.makeText(activity, activity.getString(R.string.preference_path_change_error_text), Toast.LENGTH_LONG).show();
				}
				
				return accessible;
			}
		});
	}
	
	public boolean checkDirectoryPathAccess(String directoryPath) {
		
		File folder = new File(directoryPath);
		
		if (folder.exists()) {
			if (folder.isDirectory() && folder.canRead() && folder.canWrite())
				return true;
			else 
				return false;
		}
		else {
			String[] folders = directoryPath.split("/");
			
			if (folders.length == 0) {
				return false;
			}
			
			String currentFolder = folders[folders.length - 1];
			
			if (directoryPath.endsWith("/")) {
				currentFolder = currentFolder + "/";
			}
			
			String parentFolder = directoryPath.replace(folders[folders.length - 1], "");
			
			return this.checkDirectoryPathAccess(parentFolder);
		}
	}
}

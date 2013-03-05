package at.tectas.buildbox;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class BuildBoxPreferenceActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.activity_preferences);
	}
}

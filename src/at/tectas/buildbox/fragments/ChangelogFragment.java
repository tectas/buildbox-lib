package at.tectas.buildbox.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.tectas.buildbox.R;

public class ChangelogFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.changelog_fragment, container, false);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null) {
			ArrayList<String> changelog = arguments.getStringArrayList(getString(R.string.changelog_property));
			
			StringBuilder builder = new StringBuilder();
			
			for (int i = 0; i < changelog.size(); i++) {
				builder.append(changelog.get(i));
				
				if (i < changelog.size())
					builder.append("\n");
			}
			
			TextView textView = (TextView) view.findViewById(R.id.changelog_text);
			textView.setText(builder.toString());
		}
		
		return view;
	}
}

package at.tectas.buildbox.fragments;

import java.util.ArrayList;

import junit.framework.TestFailure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
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
			
			for (String log: changelog) {
				builder.append(log);
				builder.append("\n");
			}
			
			TextView textView = new TextView(this.getActivity().getApplicationContext());
			textView.setText(builder.toString());
			view.addView(textView);
		}
		
		return view;
	}
}

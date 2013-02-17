package at.tectas.buildbox.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.tectas.buildbox.R;
import at.tectas.buildbox.helpers.BrowserUrlListener;

public class WebPageUrlFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TextView view = (TextView) inflater.inflate(R.layout.webpage_url_fragment, container, false);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null && !arguments.isEmpty()) {
			String url = arguments.getString(getString(R.string.webpages_property));
			view.setText(url);
			view.setTag(url);
			
			view.setOnClickListener(new BrowserUrlListener());
		}
		
		return view;
	}
}

package at.tectas.buildbox.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.tectas.buildbox.R;
import at.tectas.buildbox.listeners.BrowserUrlListener;

public class DeveloperItemFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TextView view = (TextView) inflater.inflate(R.layout.developers_item_fragment, container, false);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null && !arguments.isEmpty()) {
			String name = arguments.getString(getString(R.string.developer_names_property));
			view.setText(name);
			
			String url = arguments.getString(getString(R.string.developers_donationurls_property));
			
			if (url != null && !url.isEmpty()) {
				view.setTag(url);
				
				view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.donate_button, 0);
				
				view.setOnClickListener(new BrowserUrlListener());
			}	
		}
		
		return view;
	}
}

package at.tectas.buildbox.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.tectas.buildbox.R;

public class WebPagesBaseFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.webpages_base_fragment, container, false);
		
		Bundle arguments = this.getArguments();
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		if (arguments != null && !arguments.isEmpty()) {
			ArrayList<String> urls = arguments.getStringArrayList(getString(R.string.webpages_property));
			
			for (String url: urls) {
				WebPageUrlFragment hpUrl = new WebPageUrlFragment();
				Bundle urlBundle = new Bundle();
				urlBundle.putString(getString(R.string.webpages_property), url);
				
				hpUrl.setArguments(urlBundle);
				
				ft.add(R.id.webpages_base_layout, hpUrl);
			}
			
			ft.commit();
		}
		
		return view;
	}
}

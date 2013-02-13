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

public class HomePagesBaseFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.homepages_base_fragment, container, false);
		
		Bundle arguments = this.getArguments();
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		if (arguments != null) {
			ArrayList<String> urls = arguments.getStringArrayList(getString(R.string.homepages_property));
			
			for (String url: urls) {
				HomePagesUrlFragment hpUrl = new HomePagesUrlFragment();
				Bundle urlBundle = new Bundle();
				urlBundle.putString(getString(R.string.homepages_property), url);
				
				hpUrl.setArguments(urlBundle);
				
				ft.add(R.id.homepages_base_layout, hpUrl);
			}
			
			ft.commit();
		}
		
		return view;
	}
}

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

public class DevelopersBaseFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.developers_base_fragment, container, false);
		
		Bundle arguments = this.getArguments();
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		if (arguments != null) {
			ArrayList<String> names = arguments.getStringArrayList(getString(R.string.developer_names_property));
			ArrayList<String> urls = arguments.getStringArrayList(getString(R.string.developers_donationurls_property));
			
			for (int i = 0; i < names.size() && i < urls.size(); i++) {
				DeveloperItemFragment developer = new DeveloperItemFragment();
				Bundle developerBundle = new Bundle();
				
				developerBundle.putString(getString(R.string.developer_names_property), names.get(i));
				developerBundle.putString(getString(R.string.developers_donationurls_property), urls.get(i));
				
				developer.setArguments(developerBundle);
				
				ft.add(R.id.developers_base_fragment, developer);
			}
			
			ft.commit();
		}
		
		return view;
	}
}

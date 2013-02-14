package at.tectas.buildbox.fragments;

import java.util.ArrayList;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.tectas.buildbox.R;
import at.tectas.buildbox.helpers.ViewHelper;

public class DetailFragment extends Fragment {
	public static final String TAG = "DetailFragment";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.detail_fragment, container, false);
		
		ViewHelper helper = new ViewHelper(view);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null && !arguments.isEmpty()) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			helper.changeTextViewText(R.id.title, arguments.getString(getString(R.string.title_property), "Stock"));
			
			helper.changeTextViewText(R.id.version, arguments.getString(getString(R.string.version_property), ""));
			
			helper.changeTextViewText(R.id.description, arguments.getString(getString(R.string.description_property)));
			
			ArrayList<String> changelog = arguments.getStringArrayList(getString(R.string.changelog_property));
			
			if (changelog != null) {
				ChangelogFragment fragment = new ChangelogFragment();
				Bundle argument = new Bundle();
				argument.putStringArrayList(getString(R.string.changelog_property), changelog);
				
				fragment.setArguments(argument);
				ft.add(R.id.detail_main_layout, fragment);
			}
			
			String md5sum = arguments.getString(getString(R.string.md5sum_property));
			
			if (md5sum != null && !md5sum.isEmpty()) {				
				Md5sumFragment fragment = new Md5sumFragment();
				Bundle argument = new Bundle();
				argument.putString(getString(R.string.md5sum_property), md5sum);
				
				fragment.setArguments(argument);
				ft.add(R.id.detail_main_layout, fragment);
			}
			
			ArrayList<String> homePages = arguments.getStringArrayList(getString(R.string.webpages_property));
			
			if (homePages != null) {
				
				WebPagesBaseFragment fragment = new WebPagesBaseFragment();
				
				Bundle argument = new Bundle();
				argument.putStringArrayList(getString(R.string.webpages_property), homePages);
				
				fragment.setArguments(argument);
				ft.add(R.id.detail_main_layout, fragment);
			}
			
			Bundle developers = arguments.getBundle(getString(R.string.developers_property));
			
			if (developers != null && !developers.isEmpty()) {
				DevelopersBaseFragment fragment = new DevelopersBaseFragment();
				
				fragment.setArguments(developers);
				ft.add(R.id.detail_main_layout, fragment);
			}
			
			ArrayList<String> images = arguments.getStringArrayList(getString(R.string.imageurls_property));

			if (images != null && !images.isEmpty()) {
				ImagesBaseFragment fragment = new ImagesBaseFragment();
				
				Bundle argument = new Bundle();
				argument.putStringArrayList(getString(R.string.imageurls_property), images);
				
				fragment.setArguments(argument);
				ft.add(R.id.detail_main_layout, fragment);
			}
			
			ft.commit();
		}
		
		return view;
	}
}

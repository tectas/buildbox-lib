package at.tectas.buildbox.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import at.tectas.buildbox.R;
import at.tectas.buildbox.helpers.ViewHelper;

public class DetailFragment extends Fragment implements View.OnClickListener {
	public static final String TAG = "DetailFragment";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ScrollView view = (ScrollView) inflater.inflate(R.layout.detail_fragment, container, false);
		
		ViewHelper helper = new ViewHelper(view);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null) {
			helper.changeTextViewText(R.id.titleView, arguments.getString(getString(R.string.title_property), "Stock"));
			
			helper.changeTextViewText(R.id.version, arguments.getString(getString(R.string.version_property), ""));
			
			helper.changeTextViewText(R.id.description, arguments.getString(getString(R.string.description_property)));
			
			ArrayList<String> changelog = arguments.getStringArrayList(getString(R.string.changelog_property));
			
			if (changelog.size() == 0)
				view.removeView(view.findViewById(R.id.changelogContainer));
			else
				helper.changeTextViewText(R.id.changelog, changelog);
			
			String md5sum = arguments.getString(getString(R.string.md5sum_property));
			
			if (md5sum == null)
				view.removeView(view.findViewById(R.id.md5sumContainer));
			else
				helper.changeTextViewText(R.id.md5sum, md5sum);
			
			ArrayList<String> homePages = arguments.getStringArrayList(getString(R.string.homepages_property));
			
			if (homePages.size() != 0) {
				ViewGroup homePagesLayout = helper.getLayout(R.id.homePages);
				
				for (String homePage: homePages) {
					TextView dummy = new TextView(this.getActivity().getApplicationContext());
					dummy.setText(homePage);
					homePagesLayout.addView(dummy);
					
					dummy.setOnClickListener(this);
				}
			}
			else {
				view.removeView(view.findViewById(R.id.homePages));
			}
			
			Bundle developers = arguments.getBundle(getString(R.string.developers_property));
			
			if (developers != null) {
				ArrayList<String> developerNames = developers.getStringArrayList(getString(R.string.developer_names_property));
				ArrayList<String> developerUrls = developers.getStringArrayList(getString(R.string.developers_donationurls_property));
				
				
			}
		}
		
		return view;
	}

	@Override
	public void onClick(View v) {
		String url = (String) ((TextView)v).getText();
		
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			url = "http://" + url;
		
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}
}

package at.tectas.buildbox.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import at.tectas.buildbox.MainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.communication.Communicator;
import at.tectas.buildbox.helpers.DownloadKey;
import at.tectas.buildbox.helpers.DownloadPackage;
import at.tectas.buildbox.helpers.SharedObjectsHelper;
import at.tectas.buildbox.helpers.ViewHelper;
import at.tectas.buildbox.listeners.BrowserUrlListener;

public class DetailFragment extends Fragment implements OnClickListener {
	public static final String TAG = "DetailFragment";
	
	private ViewGroup relatedView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.relatedView = (ViewGroup) inflater.inflate(R.layout.detail_fragment, container, false);
		
		DownloadPackage pack = new DownloadPackage();
		
		ViewGroup layoutView = (ViewGroup) this.relatedView.findViewById(R.id.detail_main_layout);
		
		ViewHelper helper = new ViewHelper(this.relatedView);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null) {
			
			String title = arguments.getString(getString(R.string.title_property), "Stock");
			
			pack.filename = title;
			
			helper.changeTextViewText(R.id.title, title);
			
			helper.changeTextViewText(R.id.version, arguments.getString(getString(R.string.version_property), ""));
			
			helper.changeTextViewText(R.id.description, arguments.getString(getString(R.string.description_property)));
			
			ArrayList<String> changelog = arguments.getStringArrayList(getString(R.string.changelog_property));
			
			if (changelog != null && !changelog.isEmpty()) {
				View childView = inflater.inflate(R.layout.changelog_fragment, layoutView, false);
				
				StringBuilder builder = new StringBuilder();
				
				for (int i = 0; i < changelog.size(); i++) {
					builder.append(changelog.get(i));
					
					if (i < changelog.size())
						builder.append("\n");
				}
				
				TextView textView = (TextView) childView.findViewById(R.id.changelog_text);
				textView.setText(builder.toString());
				
				layoutView.addView(childView);
			}
			
			String md5sum = arguments.getString(getString(R.string.md5sum_property));
			
			if (md5sum != null && !md5sum.isEmpty()) {
				pack.md5sum = md5sum;
				
				View childView = inflater.inflate(R.layout.md5sum_fragment, layoutView, false);
				
				TextView textView = (TextView) childView.findViewById(R.id.md5sum);
				textView.setText(md5sum);
				
				layoutView.addView(childView);
			}
			
			ArrayList<String> homePages = arguments.getStringArrayList(getString(R.string.webpages_property));
			
			if (homePages != null && !homePages.isEmpty()) {
				ViewGroup childView = (ViewGroup) inflater.inflate(R.layout.webpages_base_fragment, layoutView, false);
				
				for (String url: homePages) {
					TextView textView = (TextView) inflater.inflate(R.layout.webpage_url_fragment, childView, false);
					
					textView.setText(url);
					
					textView.setTag(url);
					
					textView.setOnClickListener(new BrowserUrlListener());
					
					childView.addView(textView);
				}
				
				layoutView.addView(childView);
			}
			
			Bundle developers = arguments.getBundle(getString(R.string.developers_property));
			
			if (developers != null && !developers.isEmpty()) {
				ArrayList<String> names = developers.getStringArrayList(getString(R.string.developer_names_property));
				ArrayList<String> urls = developers.getStringArrayList(getString(R.string.developers_donationurls_property));
				
				if (names != null && !names.isEmpty() && urls != null && !urls.isEmpty()) {
					ViewGroup childView = (ViewGroup) inflater.inflate(R.layout.developers_base_fragment, layoutView, false);
					
					for (int i = 0; i < names.size() && i < urls.size(); i++) {
						TextView textView = (TextView) inflater.inflate(R.layout.developers_item_fragment, childView, false);
						
						textView.setText(names.get(i));
						
						String url = urls.get(i);
						
						childView.setTag(url);
						
						if (url != null && !url.isEmpty()) {
							textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.donate_button, 0);
							
							textView.setOnClickListener(new BrowserUrlListener());
						}
						
						childView.addView(textView);
					}
					
					layoutView.addView(childView);
				}
			}
			
			ArrayList<String> images = arguments.getStringArrayList(getString(R.string.imageurls_property));

			if (images != null && !images.isEmpty()) {
				ViewGroup childView = (ViewGroup) inflater.inflate(R.layout.images_base_fragment, layoutView, false);
				
				for (String url: images) {
					ImageView imageView = (ImageView) inflater.inflate(R.layout.image_item_fragment, childView, false);
					
					imageView.setTag(url);
					
					if (SharedObjectsHelper.remoteDrawables.containsKey((String) url)) {
						imageView.setImageBitmap(SharedObjectsHelper.remoteDrawables.get((String)url));
					}
					else {
						imageView.setImageResource(R.drawable.spinner);
						
						RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
						
						animation.setDuration(1000);
						
						animation.setRepeatCount(Animation.INFINITE);
						
						animation.setRepeatMode(Animation.INFINITE);
						
						animation.setInterpolator(new LinearInterpolator());
						
						imageView.startAnimation(animation);
						
						Communicator communicator = ((MainActivity)this.getActivity()).getCommunicator();
						
						communicator.executeBitmapAsyncCommunicator(url, imageView, ((MainActivity)this.getActivity()));
					}
					
					childView.addView(imageView);
				}
				
				layoutView.addView(childView);
			}
			
			String url = arguments.getString(getString(R.string.url_property));
			
			if (url != null && !url.isEmpty()) {				
				ViewGroup buttonLayout = (ViewGroup) this.relatedView.findViewById(R.id.detail_button_layout);
				
				pack.url = url;
				
				pack.directory = ((MainActivity)getActivity()).getDownloadDir();
				
				Button downloadButton = (Button) inflater.inflate(R.layout.download_button, buttonLayout, false);
				
				downloadButton.setText("Download");
				
				downloadButton.setTag(pack);
				
				downloadButton.setOnClickListener(this);
				
				buttonLayout.addView(downloadButton);
			}
		}
		
		return this.relatedView;
	}
	
	@Override
	public void onClick(View v) {
		MainActivity activity = (MainActivity) getActivity();
		
		final View button = v;
		
		if (!SharedObjectsHelper.downloadTabAdded) {
			activity.addDownloadsTab();
			SharedObjectsHelper.downloadTabAdded = true;
		}
		
 		DownloadPackage pack = (DownloadPackage) button.getTag();
		
 		String key = pack.md5sum == null? pack.url : pack.md5sum;
 		
 		if (!SharedObjectsHelper.downloads.containsKey(key)) {
 			if (SharedObjectsHelper.downloadAdapter != null) {
 				SharedObjectsHelper.downloadAdapter.add(pack);
 			}
 			else {
 	 			SharedObjectsHelper.downloads.put(key, pack);
 			}
 		}
	}
}

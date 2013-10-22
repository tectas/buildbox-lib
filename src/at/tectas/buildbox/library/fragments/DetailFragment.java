package at.tectas.buildbox.library.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import at.tectas.buildbox.library.R;
import at.tectas.buildbox.library.communication.Communicator;
import at.tectas.buildbox.library.communication.DownloadPackage;
import at.tectas.buildbox.library.communication.handler.DummyInstallDownloadHandler;
import at.tectas.buildbox.library.content.items.properties.DownloadType;
import at.tectas.buildbox.library.download.DownloadActivity;
import at.tectas.buildbox.library.helpers.PropertyHelper;
import at.tectas.buildbox.library.helpers.ViewHelper;
import at.tectas.buildbox.library.listeners.BrowserUrlListener;
import at.tectas.buildbox.library.listeners.ItemDownloadButtonListener;

public class DetailFragment extends Fragment {
	public static final String TAG = "DetailFragment";
	
	private ViewGroup relatedView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.relatedView = (ViewGroup) inflater.inflate(R.layout.detail_fragment, container, false);
		
		DownloadActivity activity = (DownloadActivity) getActivity();
		
		DownloadPackage pack = new DownloadPackage();
		
		ViewGroup layoutView = (ViewGroup) this.relatedView.findViewById(R.id.detail_main_layout);
		
		ViewHelper helper = new ViewHelper(this.relatedView);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null) {
			
			String title = arguments.getString(getString(R.string.title_property), "Stock");
			
			pack.title = title;
			
			helper.changeTextViewText(R.id.title, title);
			
			String version = arguments.getString(getString(R.string.version_property));
			
			if (version != null) {
				helper.changeTextViewText(R.id.version, arguments.getString(getString(R.string.version_property), null));
			}
			else {
				TextView view = (TextView) layoutView.findViewById(R.id.version);
				
				view.setVisibility(TextView.GONE);
			}
				
			String description = arguments.getString(getString(R.string.description_property));
			
			if (PropertyHelper.stringIsNullOrEmpty(description) == true) {
				TextView head = (TextView) layoutView.findViewById(R.id.description_head);
				TextView view = (TextView) layoutView.findViewById(R.id.description);
				
				head.setVisibility(TextView.GONE);
				view.setVisibility(TextView.GONE);
			}
			else {
				helper.changeTextViewText(R.id.description, description);
			}
			
			ArrayList<String> changelog = arguments.getStringArrayList(getString(R.string.changelog_property));
			
			if (changelog != null && changelog.isEmpty() == false && changelog.size() > 0) {
				View childView = inflater.inflate(R.layout.changelog_fragment, layoutView, false);
				
				StringBuilder builder = new StringBuilder();
				
				for (int i = 0; i < changelog.size(); i++) {
					builder.append(helper.getSpannedFromHtmlString(changelog.get(i)));
					
					if (i < changelog.size())
						builder.append("\n");
				}
				
				TextView textView = (TextView) childView.findViewById(R.id.changelog_text);
				textView.setText(builder.toString());
				
				layoutView.addView(childView);
			}
			
			 String md5sum = arguments.getString(getString(R.string.md5sum_property));
			       
			if (PropertyHelper.stringIsNullOrEmpty(md5sum) == false) {
				pack.md5sum = md5sum;
			}
				
			ArrayList<String> homePages = arguments.getStringArrayList(getString(R.string.webpages_property));
			
			if (homePages != null && homePages.size() > 0) {
				ViewGroup childView = (ViewGroup) inflater.inflate(R.layout.webpages_base_fragment, layoutView, false);
				
				for (String url: homePages) {
					TextView textView = (TextView) inflater.inflate(R.layout.webpage_url_fragment, childView, false);
					
					textView.setText(helper.getSpannedFromHtmlString("<u>" + url + "</u>"));
					
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
				
				if (names != null && names.size() > 0 && urls != null && urls.size() > 0) {
					ViewGroup childView = (ViewGroup) inflater.inflate(R.layout.developers_base_fragment, layoutView, false);
					
					for (int i = 0; i < names.size() && i < urls.size(); i++) {
						TextView textView = (TextView) inflater.inflate(R.layout.developers_item_fragment, childView, false);
						
						String url = urls.get(i);
						
						textView.setGravity(Gravity.CENTER_HORIZONTAL);
						
						if (PropertyHelper.stringIsNullOrEmpty(url) == false) {
							textView.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
							
							textView.setText(helper.getSpannedFromHtmlString("<u>" + names.get(i) + "</u>"));
							
							textView.setTag(url);
							
							textView.setOnClickListener(new BrowserUrlListener());
						}
						else {
							textView.setText(names.get(i));
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
					
					if (activity.getRemoteDrawables().containsKey((String) url)) {
						imageView.setImageBitmap(activity.getRemoteDrawables().get((String)url));
					}
					else {
						imageView.setImageResource(R.drawable.spinner);
						
						RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
						
						animation.setDuration(1000);
						
						animation.setRepeatCount(Animation.INFINITE);
						
						animation.setRepeatMode(Animation.INFINITE);
						
						animation.setInterpolator(new LinearInterpolator());
						
						imageView.startAnimation(animation);
						
						Communicator communicator = activity.getCommunicator();
						
						communicator.executeBitmapAsyncCommunicator(url, imageView, activity);
					}
					
					childView.addView(imageView);
				}
				
				layoutView.addView(childView);
			}
			
			String url = arguments.getString(getString(R.string.url_property));
			
			DownloadType downloadType = null;
			
			try {
				downloadType = DownloadType.valueOf(arguments.getString(getString(R.string.item_download_type_property)));
			}
			catch (IllegalArgumentException e) {
				
			}
			
			pack.type = downloadType;
			
			if ((downloadType != null && downloadType.equals(DownloadType.web)) || PropertyHelper.stringIsNullOrEmpty(url)) {
				ViewGroup buttonLayout = (ViewGroup) this.relatedView.findViewById(R.id.detail_button_layout);
				
				Button downloadButton = (Button) inflater.inflate(R.layout.download_button, buttonLayout, false);
				
				downloadButton.setText(R.string.item_web_button_text);
				
				downloadButton.setOnClickListener(new BrowserUrlListener());
				
				String page = null;
				
				if (PropertyHelper.stringIsNullOrEmpty(url) == false) {
					page = url;
				}
				else if (homePages != null && homePages.size() != 0) {
					page = homePages.get(0);
				}
				
				if (PropertyHelper.stringIsNullOrEmpty(page) == false) {
					downloadButton.setTag(page);
					
					buttonLayout.addView(downloadButton);
					
					pack.installHandler = new DummyInstallDownloadHandler();
				}
			}
			else {
				ViewGroup buttonLayout = (ViewGroup) this.relatedView.findViewById(R.id.detail_button_layout);
				
				pack.url = url;
				
				String filename = "";
				
				String[] splittedUrl = url.split("/");
				
				String[] urlParameters = splittedUrl[splittedUrl.length -1].split("\\?");
				
				splittedUrl = urlParameters[urlParameters.length - 1].split("&");
				
				for (String parameter: splittedUrl) {
					if (parameter.contains(".")) {
						String[] keyValuePair = parameter.split("=");
						
						filename = keyValuePair[keyValuePair.length - 1].replace("\"", "").replace("'", "").replace(";", "");
					}
				}
				
				if (filename.equals("")) {
					String[] keyValuePair = splittedUrl[splittedUrl.length - 1].split("=");
					
					filename = keyValuePair[keyValuePair.length - 1].replace("\"", "").replace("'", "").replace(";", "");
				}
				
				pack.setFilename(filename);
				
				pack.setDirectory(activity.getDownloadDir());
				
				Button downloadButton = (Button) inflater.inflate(R.layout.download_button, buttonLayout, false);
				
				downloadButton.setText(R.string.download_detail_button_text);
				
				downloadButton.setTag(pack);
				
				downloadButton.setOnClickListener(new ItemDownloadButtonListener(activity));
				
				buttonLayout.addView(downloadButton);
				
			}
		}
		
		return this.relatedView;
	}

}

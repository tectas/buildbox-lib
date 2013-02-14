package at.tectas.buildbox.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import at.tectas.buildbox.R;

public class DeveloperItemFragment extends Fragment implements OnClickListener {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TextView view = (TextView) inflater.inflate(R.layout.developers_item_fragment, container, false);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null && !arguments.isEmpty()) {
			String name = arguments.getString(getString(R.string.developer_names_property));
			view.setText(name);
			
			String url = arguments.getString(getString(R.string.developers_donationurls_property));
			
			if (url != null && !url.isEmpty()) {
				view.setHint(url);
				
				view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.donate_button, 0);
				
				view.setOnClickListener(this);
			}	
		}
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		String url = (String) ((TextView)v).getHint();
		
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			url = "http://" + url;
		
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}
}

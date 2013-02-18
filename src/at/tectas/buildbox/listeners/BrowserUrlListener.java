package at.tectas.buildbox.listeners;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class BrowserUrlListener implements OnClickListener {
	@Override
	public void onClick(View v) {
		String url = (String) ((TextView)v).getTag();
		
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			url = "http://" + url;
		
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		v.getContext().startActivity(browserIntent);
	}
}

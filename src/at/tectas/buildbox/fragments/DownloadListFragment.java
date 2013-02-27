package at.tectas.buildbox.fragments;

import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.adapters.DownloadPackageAdapter;
import at.tectas.buildbox.service.DownloadService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class DownloadListFragment extends Fragment implements OnClickListener{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.download_list_fragment, container, false);
		
		BuildBoxMainActivity activity = (BuildBoxMainActivity) getActivity();
		
		ListView list = (ListView) view.findViewById(R.id.download_list);
		
		DownloadPackageAdapter adapter = new DownloadPackageAdapter(getActivity());
		
		activity.downloadAdapter = adapter;
		
		list.setAdapter(activity.downloadAdapter);
		
		Button button = (Button) view.findViewById(R.id.download_all_button);
		
		if (DownloadService.Processing == true) {
			button.setText(R.string.download_stop_button_text);
		}
		else {
			button.setText(R.string.download_all_button_text);
		}
		button.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {		
		BuildBoxMainActivity activity = (BuildBoxMainActivity) this.getActivity();
		
		Button button = (Button)v;
		
		if (DownloadService.Processing == true) {
			activity.stopDownload();
			button.setText(R.string.download_all_button_text);
		}
		else {
			activity.startDownload();
			button.setText(R.string.download_stop_button_text);
		}
	}
}

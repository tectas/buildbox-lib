package at.tectas.buildbox.fragments;

import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.adapters.DownloadPackageAdapter;
import at.tectas.buildbox.listeners.ListDownloadButtonListener;
import at.tectas.buildbox.service.DownloadService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class DownloadListFragment extends Fragment {

	private ListView list = null;
	private BuildBoxMainActivity activity = null;
	
	@Override
	public void onDestroyView() {		
		unregisterForContextMenu(list);
		
		super.onDestroyView();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.download_list_fragment, container, false);
		
		activity = (BuildBoxMainActivity) getActivity();
		
		list = (ListView) view.findViewById(R.id.download_list);
		
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
		button.setOnClickListener(new ListDownloadButtonListener(activity));
	
		registerForContextMenu(list);
		
		return view;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = activity.getMenuInflater();
		inflater.inflate(R.menu.download_queue_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		
		switch (item.getItemId()) {
			case R.id.remove:
				activity.getDownloads().remove(info.position);
				if (activity.downloadAdapter != null)
					activity.downloadAdapter.notifyDataSetChanged();
				return true;
			case R.id.remove_broken:
				activity.removeBrokenAndAbortedFromMap();
				return true;
		}
		
		return super.onContextItemSelected(item);
	}
}

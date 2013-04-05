package at.tectas.buildbox.fragments;

import com.mobeta.android.dslv.DragSortListView;

import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.msteam.R;
import at.tectas.buildbox.adapters.DownloadPackageAdapter;
import at.tectas.buildbox.listeners.ListDownloadButtonListener;
import at.tectas.buildbox.service.DownloadService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class DownloadListFragment extends Fragment {

	private static final String TAG = "DownloadListFragment";
	private DragSortListView list = null;
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
		
		list = (DragSortListView) view.findViewById(R.id.download_list);
		
		ViewGroup buttonLayout = (ViewGroup)view.findViewById(R.id.download_button_layout);
		
		DownloadPackageAdapter adapter = new DownloadPackageAdapter(getActivity(), buttonLayout);
		
		BuildBoxDragSortController controller = new BuildBoxDragSortController(list, adapter);
		
		activity.setDownloadPackageAdapter(adapter);
		
		list.setAdapter(adapter);
		
		list.setFloatViewManager(controller);
		
		list.setOnTouchListener(controller);
		
		list.setDragEnabled(true);
		
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
		
		Log.e(TAG, String.valueOf(info.position));
		
		switch (item.getItemId()) {
			case R.id.remove:
				activity.getDownloads().remove(info.position);
				if (activity.getDownloadPackageAdapter() != null)
					activity.getDownloadPackageAdapter().notifyDataSetChanged();
				return true;
		}
		
		return super.onContextItemSelected(item);
	}
}

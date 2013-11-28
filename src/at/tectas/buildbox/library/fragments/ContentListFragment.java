package at.tectas.buildbox.library.fragments;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import at.tectas.buildbox.library.R;
import at.tectas.buildbox.library.adapters.ItemArrayAdapter;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.download.DownloadActivity;

@SuppressLint("ValidFragment")
public class ContentListFragment extends SherlockFragment {
	private ItemArrayAdapter adapter = null;

	public ContentListFragment() {
		super();
	}

	public ContentListFragment(ItemArrayAdapter adapter) {
		super();
		this.adapter = adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.list_fragment,
				container, false);

		ListView list = (ListView) view.findViewById(R.id.list_view);

		Bundle arguments = this.getArguments();

		int arrayIndex = 0;

		if (arguments != null) {
			arrayIndex = arguments.getInt("index");
		}

		DownloadActivity activity = (DownloadActivity) getActivity();

		list.setAdapter(adapter == null ? new ItemArrayAdapter(activity,
				R.id.ListItemTextView, new ArrayList<Item>(activity
						.getContentItems().get(arrayIndex).getChildren()),
				getChildFragmentManager()) : this.adapter);

		return view;
	}
}

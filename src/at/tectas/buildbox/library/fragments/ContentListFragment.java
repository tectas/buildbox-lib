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
	private int listItemViewId = R.id.ListItemImageView;
	private ArrayList<Item> items = null;

	public ContentListFragment() {
		super();
	}

	@Deprecated
	public ContentListFragment(ItemArrayAdapter adapter) {
		super();
		this.adapter = adapter;
	}

	public void setListItemViewId(int id) {
		this.listItemViewId = id;
	}

	public void setListItems(ArrayList<Item> items) {
		this.items = items;
	}

	public ArrayList<Item> getListItems() {
		return this.items;
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

		this.items = this.items == null ? activity.getContentItems()
				.get(arrayIndex).getChildren() : this.items;

		list.setAdapter(adapter == null ? new ItemArrayAdapter(activity,
				this.listItemViewId, this.items, getChildFragmentManager())
				: this.adapter);

		return view;
	}
}

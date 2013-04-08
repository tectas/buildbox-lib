package at.tectas.buildbox.library.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import at.tectas.buildbox.R;
import at.tectas.buildbox.library.adapters.ItemArrayAdapter;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.content.items.ParentItem;
import at.tectas.buildbox.library.download.DownloadActivity;

public class ContentListFragment extends Fragment {
	private ItemArrayAdapter adapter = null;
	
	public ContentListFragment() {
		super();
	}
	
	public ContentListFragment(ItemArrayAdapter adapter) {
		this();
		this.adapter = adapter;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.list_fragment, container, false);
		
		ListView list = (ListView)view.findViewById(R.id.list_view);
		
		Bundle arguments = this.getArguments();
		
		int arrayIndex = 0;
		
		if (arguments != null) {
			arrayIndex = arguments.getInt("index");
		}
		
		DownloadActivity activity = (DownloadActivity) getActivity();
		
		list.setAdapter(
				adapter == null ? 
						new ItemArrayAdapter(
								activity, 
								R.id.ListItemTextView, 
								new ArrayList<Item>(
										((ParentItem)activity.getContentItems()
												.get(arrayIndex))
												.childs), 
								getChildFragmentManager()) : 
						this.adapter
				);
		
		return view;
	}
}

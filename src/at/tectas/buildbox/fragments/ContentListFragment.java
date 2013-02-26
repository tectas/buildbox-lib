package at.tectas.buildbox.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.adapters.ItemArrayAdapter;
import at.tectas.buildbox.content.ParentItem;
import at.tectas.buildbox.content.Item;
import android.app.Activity;

public class ContentListFragment extends Fragment {
	private ItemArrayAdapter adapter = null;
	
	public ContentListFragment() {
		super();
	}
	
	public ContentListFragment(ItemArrayAdapter adapter) {		
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
		
		list.setAdapter(
				adapter == null ? 
						new ItemArrayAdapter(
								(Activity)this.getActivity(), 
								R.id.ListItemTextView, 
								new ArrayList<Item>(
										((ParentItem)((BuildBoxMainActivity)this.getActivity()).contentItems
												.get(arrayIndex))
												.childs), 
								getChildFragmentManager()) : 
						this.adapter
				);
		
		return view;
	}
}

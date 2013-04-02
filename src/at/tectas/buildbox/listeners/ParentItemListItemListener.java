package at.tectas.buildbox.listeners;

import java.util.ArrayList;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.adapters.ItemArrayAdapter;
import at.tectas.buildbox.content.items.Item;
import at.tectas.buildbox.fragments.ContentListFragment;

public class ParentItemListItemListener implements OnClickListener {
	
	private BuildBoxMainActivity activity = null;
	private ArrayList<Item> items = null;
	private FragmentManager manager = null;
	
	public ParentItemListItemListener (BuildBoxMainActivity activity, ArrayList<Item> items, FragmentManager manager) {
		this.activity = activity;
		this.items = items;
		this.manager = manager;
	}
	
	@Override
	public void onClick(View v) {
		FragmentTransaction fragmentTransaction = manager.beginTransaction();
		
		ContentListFragment fragment = new ContentListFragment(new ItemArrayAdapter(this.activity, R.id.ListItemTextView, this.items, this.manager));

	    fragmentTransaction.replace(R.id.list_layout, fragment);

	    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);   

	    fragmentTransaction.addToBackStack(null);
	    
	    fragmentTransaction.commit();
	}
}

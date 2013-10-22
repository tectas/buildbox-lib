package at.tectas.buildbox.library.listeners;

import java.util.ArrayList;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import at.tectas.buildbox.library.R;
import at.tectas.buildbox.library.adapters.ItemArrayAdapter;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.download.DownloadActivity;
import at.tectas.buildbox.library.fragments.ContentListFragment;

public class ParentItemListItemListener implements OnClickListener {
	
	private DownloadActivity activity = null;
	private ArrayList<Item> items = null;
	private FragmentManager manager = null;
	
	public ParentItemListItemListener (DownloadActivity activity, ArrayList<Item> items, FragmentManager manager) {
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

package at.tectas.buildbox.library.listeners;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import at.tectas.buildbox.R;
import at.tectas.buildbox.library.content.items.DetailItem;
import at.tectas.buildbox.library.fragments.DetailFragment;

public class ChildItemListItemListener implements OnClickListener {
	
	private FragmentManager manager = null;
	private DetailItem  item = null;
	
	public ChildItemListItemListener (DetailItem item, FragmentManager manager) {
		this.item = item;
		this.manager = manager;
	}
	
	@Override
	public void onClick(View v) {
		FragmentTransaction fragmentTransaction = manager.beginTransaction();
		
		DetailFragment fragment = new DetailFragment();
		
		fragment.setArguments(item.parseItemToBundle());

	    fragmentTransaction.replace(R.id.list_layout, fragment);

	    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);   

	    fragmentTransaction.addToBackStack(null);
	    
	    fragmentTransaction.commit();
	}

}

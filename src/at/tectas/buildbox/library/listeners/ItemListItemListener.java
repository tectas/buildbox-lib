package at.tectas.buildbox.library.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import at.tectas.buildbox.library.R;
import at.tectas.buildbox.library.adapters.ItemArrayAdapter;
import at.tectas.buildbox.library.communication.Communicator;
import at.tectas.buildbox.library.communication.asynccommunicators.JSONElementAsyncCommunicatorResult;
import at.tectas.buildbox.library.communication.callbacks.interfaces.ICommunicatorCallback;
import at.tectas.buildbox.library.content.ItemList;
import at.tectas.buildbox.library.content.items.DetailItem;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.content.items.ParentItem;
import at.tectas.buildbox.library.download.DownloadActivity;
import at.tectas.buildbox.library.fragments.ContentListFragment;
import at.tectas.buildbox.library.fragments.DetailFragment;

public class ItemListItemListener implements OnClickListener,
		ICommunicatorCallback {

	private FragmentManager manager = null;
	private DetailItem item = null;
	private ParentItem parent = null;
	private ItemList list = null;
	private String url = null;
	private DownloadActivity activity = null;
	private Communicator communicator = null;

	public ItemListItemListener(DownloadActivity activity, ParentItem parent,
			FragmentManager manager, String url, Communicator communicator) {
		this.parent = parent;
		this.url = url;
		this.manager = manager;
		this.activity = activity;
		this.communicator = communicator;
	}

	public ItemListItemListener(DownloadActivity activity, ParentItem parent,
			FragmentManager manager, String url) {
		this(activity, parent, manager, url, new Communicator());
	}

	public ItemListItemListener(DownloadActivity activity, ParentItem parent,
			FragmentManager manager, DetailItem item) {
		this(activity, parent, manager, null, null);
		this.item = item;
	}

	public ItemListItemListener(DownloadActivity activity, ParentItem parent,
			FragmentManager manager, ItemList itemList) {
		this(activity, parent, manager, null, null);
		this.list = itemList;
	}

	@Override
	public void onClick(View v) {
		if (item != null) {
			replaceDetailFragment(item);
		} else if (list != null) {
			replaceContentListFragment(list);
		} else {
			loadItem(this.url);
		}
	}

	public void replaceFragemntCommit(Fragment fragment) {
		FragmentTransaction fragmentTransaction = manager.beginTransaction();

		fragmentTransaction.replace(R.id.list_layout, fragment);

		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

		fragmentTransaction.addToBackStack(null);

		fragmentTransaction.commit();
	}

	public void loadItem(String url) {
		communicator.executeJSONElementAsyncCommunicator(url, this);
	}

	@Override
	public void updateImage(ImageView view, Bitmap bitmap) {
	}

	@Override
	public void updateJsonArray(JsonArray result) {
		ItemList list = activity.getItemParser().parseJson(result);

		for (Item item : list) {
			item.addToParent(parent);
		}

		replaceContentListFragment(list);
	}

	@Override
	public void updateJsonObject(JsonObject result) {
		DetailItem detail = (DetailItem) activity.getItemParser()
				.parseJsonItem(item, result);

		detail.addToParent(parent);

		replaceDetailFragment(detail);
	}

	public void replaceContentListFragment(ItemList items) {
		ContentListFragment fragment = new ContentListFragment(
				new ItemArrayAdapter(this.activity, R.id.ListItemTextView,
						items, this.manager));

		replaceFragemntCommit(fragment);
	}

	public void replaceDetailFragment(DetailItem item) {
		DetailFragment fragment = new DetailFragment();

		fragment.setArguments(item.parseItemToBundle());

		replaceFragemntCommit(fragment);
	}

	@Override
	public void updateJsonElement(JSONElementAsyncCommunicatorResult result) {
		try {
			this.updateJsonObject(result.element.getAsJsonObject());
		} catch (Exception e) {
			this.updateJsonArray(result.element.getAsJsonArray());
		}
	}
}

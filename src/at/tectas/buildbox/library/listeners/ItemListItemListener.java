package at.tectas.buildbox.library.listeners;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
import at.tectas.buildbox.library.download.DownloadActivity;
import at.tectas.buildbox.library.fragments.ContentListFragment;
import at.tectas.buildbox.library.fragments.DetailFragment;

public class ItemListItemListener implements OnClickListener,
		ICommunicatorCallback {

	public static int staticLayoutID = -1;
	private FragmentManager manager = null;
	private Item item = null;
	private Item parent = null;
	private ArrayList<Item> list = null;
	private String url = null;
	private DownloadActivity activity = null;
	private Communicator communicator = null;
	private int layoutID = -1;

	public ItemListItemListener(DownloadActivity activity, Item parent,
			FragmentManager manager, String url, Communicator communicator) {
		this.parent = parent;
		this.url = url;
		this.manager = manager;
		this.activity = activity;
		this.communicator = communicator;
	}

	public ItemListItemListener(DownloadActivity activity, Item parent,
			FragmentManager manager, ArrayList<Item> list,
			Communicator communicator) {
		this(activity, parent, manager, (String) null, communicator);
		this.list = list;
	}

	public ItemListItemListener(DownloadActivity activity, Item parent,
			FragmentManager manager, Item item) {
		this(activity, parent, manager, (String) null, null);
		this.item = item;
	}

	public ItemListItemListener(DownloadActivity activity, Item parent,
			FragmentManager manager, ArrayList<Item> itemList) {
		this(activity, parent, manager, itemList, null);
		this.list = itemList;
	}

	public ItemListItemListener(DownloadActivity activity,
			FragmentManager manager) {
		this(activity, null, manager, (String) null, null);
	}

	public ItemListItemListener() {

	}

	public void setParent(Item item) {
		this.parent = item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setItemList(ArrayList<Item> list) {
		this.list = list;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setLayoutID(int id) {
		this.layoutID = id;
	}

	public void setActivity(DownloadActivity activity) {
		this.activity = activity;
	}

	public void setFragmentManager(FragmentManager manager) {
		this.manager = manager;
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

	public void replaceFragemntCommit(SherlockFragment fragment) {
		FragmentTransaction fragmentTransaction = manager.beginTransaction();

		fragmentTransaction.replace(layoutID == -1 ? (staticLayoutID == -1 ? R.id.list_layout : staticLayoutID ) : layoutID,
				fragment);

		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

		fragmentTransaction.addToBackStack(null);

		fragmentTransaction.commit();

		activity.afterFragmentChange(fragment);
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

	public void replaceContentListFragment(ArrayList<Item> items) {
		ContentListFragment fragment = new ContentListFragment(
				new ItemArrayAdapter(this.activity, R.id.ListItemTextView,
						items, this.manager));

		replaceFragemntCommit(fragment);
	}

	public void replaceDetailFragment(Item item) {
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

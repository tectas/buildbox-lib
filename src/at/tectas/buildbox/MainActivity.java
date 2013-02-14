package at.tectas.buildbox;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.animation.Animation;
import android.widget.ImageView;
import at.tectas.buildbox.adapters.TabsAdapter;
import at.tectas.buildbox.communication.Communicator;
import at.tectas.buildbox.communication.ICommunicatorCallback;
import at.tectas.buildbox.content.DetailItem;
import at.tectas.buildbox.content.Item;
import at.tectas.buildbox.content.ItemList;
import at.tectas.buildbox.fragments.ContentListFragment;
import at.tectas.buildbox.fragments.DetailFragment;
import at.tectas.buildbox.helpers.JsonParser;
import at.tectas.buildbox.R;

@SuppressLint("DefaultLocale")
public class MainActivity extends FragmentActivity implements ICommunicatorCallback {
	
	public static final String TAG = "Main";

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private String romUrl = null;
	private String version = null;
	private String contentUrl = null;
	private String downloadDir = null;
	private ActionBar bar = null;
	private TabsAdapter adapter = null;
	private DetailItem romItem = null;
	private ItemList contentItems = null;
	private Communicator communicator = new Communicator();
	
	public Communicator getCommunicator() {
		return this.communicator;
	}
	
	public String getRomUrl() {
		return this.romUrl;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public DetailItem getRomItem() {
		return this.romItem;
	}
	
	public ItemList getContentItems() {
		return contentItems;
	}

	public String getContentUrl() {
		return this.contentUrl;
	}
	
	public String getDownloadDir() {
		return this.downloadDir;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		Item.setActivity(this);
		
		String rom = System.getProperty(getString(R.string.kitchen_rom_url_property), null);
		
		String content = System.getProperty(getString(R.string.kitchen_content_url_property), null);
		
		this.romUrl = (rom == null? getString(R.string.default_rom_url): rom);
	
		this.version = System.getProperty(getString(R.string.kitchen_rom_version_property), null);
	
		this.contentUrl = (content == null ? getString(R.string.default_content_url): content);
	
		this.downloadDir = System.getProperty(getString(R.string.kitchen_download_dir), null);
		
		if ((this.romUrl == null || this.version == null) && this.contentUrl == null) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this.getBaseContext());
			builder.setCancelable(true);
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					System.exit(RESULT_CANCELED);		
				}
			});
			builder.setMessage(R.string.app_cancel_message);
			builder.create().show();
		}
		
		try {
			if (this.romUrl != null)
				this.communicator.executeJSONObjectAsyncCommunicator(this.romUrl, this);			

			//if (this.contentUrl != null)
			//	this.communicator.executeJSONArrayAsyncCommunicator(this.contentUrl, this);
		} 
		catch (Exception e) {
			if (e != null && e.getMessage() != null)
				Log.e(MainActivity.TAG, e.getMessage());
			
			for (StackTraceElement element: e.getStackTrace()) {
				if (element != null)
					Log.e(MainActivity.TAG, element.toString() + " " + element.getLineNumber());
			}
		}
		
		this.bar = getActionBar();
		//this.bar.setDisplayShowTitleEnabled(false);
		this.bar.setDisplayShowHomeEnabled(false);
		this.bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		this.bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		
		this.adapter = new TabsAdapter(this, (ViewPager)findViewById(R.id.pager));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void updateWithJSONObject(JSONObject result) {
		try {
			this.romItem = (DetailItem) JsonParser.parseJsonToItem(result);
			
			this.adapter.addTab(
					this.bar.newTab().setText("Rom"), 
					DetailFragment.class, 
					this.romItem.parseItemToBundle());

		} catch (Exception e) {
			Log.e(MainActivity.TAG, e.getMessage());
		}
	}

	@Override
	public void updateWithJSONArray(JSONArray result) {
		try {
			this.contentItems = JsonParser.parseJson(result);
			
			for (Item item : this.contentItems.values()) {
				this.adapter.addTab(
						this.bar.newTab().setText(item.title), 
						ContentListFragment.class, 
						item.parseItemToBundle());
			}
		}
		catch (Exception e) {
			Log.e(MainActivity.TAG, e.getMessage().toString());
		}
	}

	@Override
	public void updateWithImage(ImageView view, Bitmap bitmap) {
		try {
			if (view != null) {
				Animation animation = view.getAnimation();
				animation.cancel();
				view.setImageBitmap(bitmap);
			}
		}
		catch (Exception e) {
			Log.e(MainActivity.TAG, e.getMessage());
		}
	}

}

package at.tectas.buildbox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.ImageView;
import at.tectas.buildbox.adapters.DownloadPackageAdapter;
import at.tectas.buildbox.adapters.TabsAdapter;
import at.tectas.buildbox.content.DetailItem;
import at.tectas.buildbox.content.Item;
import at.tectas.buildbox.content.ItemList;
import at.tectas.buildbox.fragments.ContentListFragment;
import at.tectas.buildbox.fragments.DetailFragment;
import at.tectas.buildbox.fragments.DownloadListFragment;
import at.tectas.buildbox.helpers.JsonItemParser;
import at.tectas.buildbox.helpers.PropertyHelper;
import at.tectas.buildbox.listeners.BuildBoxDownloadCallback;
import at.tectas.buildbox.recovery.OpenRecoveryScript;
import at.tectas.buildbox.recovery.OpenRecoveryScriptConfiguration;
import at.tectas.buildbox.service.DownloadService;
import at.tectas.buildbox.R;

@SuppressLint("DefaultLocale")
public class BuildBoxMainActivity extends DownloadActivity {
	ViewPager mViewPager;
	
	private String romUrl = null;
	private String version = null;
	private String contentUrl = null;
	private String downloadDir = null;
	private PropertyHelper helper = null;
	public ActionBar bar = null;
	private TabsAdapter adapter = null;
	private DetailItem romItem = null;
	public DownloadPackageAdapter downloadAdapter = null;
	public ItemList contentItems = null;
	public int viewPagerIndex = 0;
	public Fragment fragment = null;
	public Hashtable<String, Bitmap> remoteDrawables = new Hashtable<String, Bitmap>();
	
	public String getRomUrl() {
		return this.romUrl;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public DetailItem getRomItem() {
		return this.romItem;
	}

	public String getContentUrl() {
		return this.contentUrl;
	}
	
	public String getDownloadDir() {
		return this.downloadDir;
	}
	
	public void initialize() {
		Item.setActivity(this);
		
		this.helper = new PropertyHelper(this.getApplicationContext());
		
		this.romUrl = this.helper.getRomUrl();
	
		this.version = this.helper.getVersion();
	
		this.contentUrl = this.helper.getContentUrl();
		
		this.downloadDir = this.helper.getDownloadDirectory();
		
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
			
			if (this.contentUrl != null)
				this.communicator.executeJSONArrayAsyncCommunicator(this.contentUrl, this);
		} 
		catch (Exception e) {
			if (e != null && e.getMessage() != null)
				Log.e(DownloadActivity.TAG, e.getMessage());
			
			for (StackTraceElement element: e.getStackTrace()) {
				if (element != null)
					Log.e(DownloadActivity.TAG, element.toString() + " " + element.getLineNumber());
			}
		}
		
		this.startUpdateAlarm();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		this.initialize();
		
		this.bar = getActionBar();
		this.bar.setDisplayShowHomeEnabled(false);
		this.bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		this.bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		
		this.adapter = new TabsAdapter(this, (ViewPager)findViewById(R.id.pager));
	}
	
	@Override
	protected void onStop() {
		this.removeActivityCallbacks();
		
		super.onStop();
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.settings:
				Intent i = new Intent(this, BuildBoxPreferenceActivity.class);
	            startActivity(i);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void startUpdateAlarm() {
		Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 1);
        
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	}
	
	@Override
	public void removeBrokenAndAbortedFromMap() {
		super.removeBrokenAndAbortedFromMap();
		
		if (this.downloadAdapter != null) {
			this.downloadAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void getServiceDownloadMap() {
		super.getServiceDownloadMap();
		
		if (this.getDownloads().size() != 0) {
			if (!this.bar.getTabAt(this.bar.getTabCount() - 1).getText().equals("Downloads")) {
				this.addDownloadsTab();
			}
			
			if (this.downloadAdapter != null) {
				this.downloadAdapter.notifyDataSetChanged();
			}
		}
	}
	
	@Override
	public void getServiceDownloadMap(boolean addListeners) {
		if (addListeners == true) {
			if (this.callback == null) {
				this.callback = new BuildBoxDownloadCallback(this);
			}
			
			super.getServiceDownloadMap(this.callback, this.callback, this.callback);
		}
		else {
			super.getServiceDownloadMap();
		}
	}
	
	@Override
	public void updateWithJsonObject(JsonObject result) {
		try {
			this.romItem = (DetailItem) JsonItemParser.parseJsonToItem(result);
			
			this.adapter.addTab(
					this.bar.newTab().setText("Rom"), 
					DetailFragment.class, 
					this.romItem.parseItemToBundle());

		} 
		catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		if (this.contentUrl == null)
			if (DownloadService.Started) {
				this.getServiceMap();
			}
			else {
				this.loadDownloadsMapFromCacheFile();
			}
	}
	
	@Override
	public void updateWithJsonArray(JsonArray result) {
		
		try {
			this.contentItems = JsonItemParser.parseJson(result);
			
			int i = 0;
			
			for (Item item : this.contentItems) {
				Bundle bundle = new Bundle();
				
				bundle.putInt("index", i);
				
				this.adapter.addTab(
						this.bar.newTab().setText(item.title), 
						ContentListFragment.class, 
						bundle);
				
				i++;
			}
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
		if (DownloadService.Started) {
			this.getServiceMap();
		}
		else {
			this.loadDownloadsMapFromCacheFile();
		}
	}

	@Override
	public void updateWithImage(ImageView view, Bitmap bitmap) {
		try {
			if (view != null) {
				Animation animation = view.getAnimation();
				if (animation != null && animation.hasStarted())
					animation.cancel();
				
				if (!this.remoteDrawables.containsKey((String)view.getTag())) {
					this.remoteDrawables.put((String)view.getTag(), bitmap);
				}
				else {
					bitmap = this.remoteDrawables.get((String)view.getTag());
				}
				
				view.setImageBitmap(bitmap);
			}
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
	    if (this.fragment == null || !this.fragment.getChildFragmentManager().popBackStackImmediate()) {
	    	if (!this.getFragmentManager().popBackStackImmediate())
	    		finish();
	    }
	}
	
	public void addTab(String title, Class<?> clss, Bundle bundle) {
		this.adapter.addTab(this.bar.newTab().setText(title), clss, bundle);
	}
	
	public void addDownloadsTab() {
		this.addTab("Downloads", DownloadListFragment.class, new Bundle());
	}
	
	public void getDownloadsMapandUpdateList () {
		this.getServiceMap(false);
		
		if (this.getDownloads().size() != 0) {
			if (!this.bar.getTabAt(this.bar.getTabCount() - 1).getText().equals("Downloads"))
				this.addDownloadsTab();
			
			if (this.downloadAdapter != null) {
				this.downloadAdapter.notifyDataSetChanged();
			}
		}
	}
	
	public void setupFlashProcess(ArrayList<Integer> list) {
		OpenRecoveryScriptConfiguration config = new OpenRecoveryScriptConfiguration(this.downloadDir, this.getDownloads());
		
		for (Integer option: list){			
			if (option.equals(Integer.valueOf(0))) {
				config.backupFirst = true;
			}
			if (option.equals(Integer.valueOf(1))) {
				config.wipeData = true;
			}
			if (option.equals(Integer.valueOf(2))) {
				config.includeMd5mismatch = true;
			}
		}
		
		if (!list.contains(Integer.valueOf(0))) {
			config.backupFirst = false;
		}
		
		OpenRecoveryScript script = new OpenRecoveryScript(config);
		script.writeScriptFile();
	}

	@Override
	public void startServiceDownload() {
		if (this.callback == null) {
			this.callback = new BuildBoxDownloadCallback(this);
		}
		
		super.startServiceDownload(this.callback, this.callback, this.callback);
	}
}

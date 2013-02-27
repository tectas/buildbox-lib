package at.tectas.buildbox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Hashtable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.animation.Animation;
import android.widget.ImageView;
import at.tectas.buildbox.adapters.DownloadPackageAdapter;
import at.tectas.buildbox.adapters.TabsAdapter;
import at.tectas.buildbox.communication.Communicator;
import at.tectas.buildbox.communication.DownloadMap;
import at.tectas.buildbox.communication.DownloadResponse;
import at.tectas.buildbox.communication.ICommunicatorCallback;
import at.tectas.buildbox.communication.IDownloadCancelledCallback;
import at.tectas.buildbox.communication.IDownloadFinishedCallback;
import at.tectas.buildbox.communication.IDownloadProgressCallback;
import at.tectas.buildbox.communication.Communicator.CallbackType;
import at.tectas.buildbox.content.DetailItem;
import at.tectas.buildbox.content.Item;
import at.tectas.buildbox.content.ItemList;
import at.tectas.buildbox.fragments.ContentListFragment;
import at.tectas.buildbox.fragments.DetailFragment;
import at.tectas.buildbox.fragments.DownloadListFragment;
import at.tectas.buildbox.helpers.JsonItemParser;
import at.tectas.buildbox.helpers.PropertyHelper;
import at.tectas.buildbox.service.DownloadService;
import at.tectas.buildbox.R;

@SuppressLint("DefaultLocale")
public class BuildBoxMainActivity extends FragmentActivity implements ICommunicatorCallback, IDownloadProgressCallback, IDownloadFinishedCallback, IDownloadCancelledCallback {
	
	public static final String TAG = "Main";

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private String romUrl = null;
	private String version = null;
	private String contentUrl = null;
	private String downloadDir = null;
	private PropertyHelper helper = new PropertyHelper(this);
	public ActionBar bar = null;
	private TabsAdapter adapter = null;
	private DetailItem romItem = null;
	private Communicator communicator = new Communicator();
	private DownloadMap downloads = new DownloadMap();
	public DownloadPackageAdapter downloadAdapter = null;
	public ItemList contentItems = null;
	public int viewPagerIndex = 0;
	public Fragment fragment = null;
	public Hashtable<String, Bitmap> remoteDrawables = new Hashtable<String, Bitmap>();
	
	public DownloadServiceConnection serviceConnection = new DownloadServiceConnection(this);
	
	public Communicator getCommunicator() {
		return this.communicator;
	}
	
	public synchronized DownloadMap getDownloads() {
		return this.downloads;
	}
	
	public synchronized void setDownloads(DownloadMap map) {
		if (map != null) {
			this.downloads = map;
		}
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

	public String getContentUrl() {
		return this.contentUrl;
	}
	
	public String getDownloadDir() {
		return this.downloadDir;
	}
	
	public void initialize() {
		Item.setActivity(this);
		
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
				Log.e(BuildBoxMainActivity.TAG, e.getMessage());
			
			for (StackTraceElement element: e.getStackTrace()) {
				if (element != null)
					Log.e(BuildBoxMainActivity.TAG, element.toString() + " " + element.getLineNumber());
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
	
	public void startUpdateAlarm() {
		Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 1);
        
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	}
	
	public void bindDownloadService() {
		Intent downloadServiceIntent = new Intent(this.getApplicationContext(), DownloadService.class);	
		
		if (DownloadService.Started == false) {	
			this.startService(downloadServiceIntent);
		}
		
		this.bindService(downloadServiceIntent, this.serviceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
	}
	
	public void unbindDownloadService() {
		if (this.serviceConnection.bound == true) {
			try {
				unbindService(this.serviceConnection);
				this.serviceConnection.bound = false;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startDownload() {
		if (this.serviceConnection.bound == false) {
			this.serviceConnection.executeStartDownloadCallback = true;
			this.bindDownloadService();
		}
		else {
			this.startServiceDownload();
		}
	}
	
	public void stopDownload() {
		if (this.serviceConnection.bound == false) {
			this.serviceConnection.executeStopDownloadCallback = true;
			this.bindDownloadService();
		}
		else {
			this.stopServiceDownload();
		}
	}
	
	public void getServiceMap() {
		this.getServiceMap(true);
	}
	
	public void getServiceMap(boolean addListeners) {
		if (this.serviceConnection.bound == false) {
			this.serviceConnection.executeGetDownloadMapCallback = true;
			this.serviceConnection.addListernersAtGetDownloadMapCallback = addListeners;
			this.bindDownloadService();
		}
		else {
			this.getServiceDownloadMap(false);
		}
	}
	
	public void removeActivityCallbacks() {
		if (this.serviceConnection.bound == false) {
			this.serviceConnection.executeRemoveCallback = true;
			this.bindDownloadService();
		}
		else {
			this.removeCallbacksAndUnbind();
		}
	}
	
	public void startServiceDownload() {
		if (DownloadService.Processing == true) {
			this.getServiceDownloadMap();
		}
		else {
			this.serviceConnection.service.startDownload(this.getDownloads());
			this.serviceConnection.service.addDownloadListeners(CallbackType.UI, this, this, this);
		}
	}
	
	public void stopServiceDownload() {
		this.serviceConnection.service.stopDownloads();
	}
	
	public void getServiceDownloadMap() {
		this.getServiceDownloadMap(true);
	}
	
	public void getServiceDownloadMap(boolean addListeners) {
		if (addListeners == true)
			this.serviceConnection.service.addDownloadListeners(CallbackType.UI, this, this, this);
		
		DownloadMap serviceMap = this.serviceConnection.service.getMap();
		
		if (serviceMap != null && serviceMap.size() != 0)
			this.setDownloads(serviceMap);
		
		if (this.getDownloads().size() != 0) {
			if (!this.bar.getTabAt(this.bar.getTabCount() - 1).getText().equals("Downloads")) {
				this.addDownloadsTab();
			}
			
			if (this.downloadAdapter != null) {
				this.downloadAdapter.notifyDataSetChanged();
			}
		}
	}
	
	public void removeCallbacksAndUnbind() {
		this.serviceConnection.service.removeDownloadListeners(CallbackType.UI);
		
		this.unbindDownloadService();
	}
	
	private void loadDownloadsMapFromCacheFile() {
		BufferedReader stream = null;
		
		String[] files = this.fileList();
		
		boolean exists = false;
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].equals(this.getString(R.string.downloads_cach_filename))) {
				exists = true;
				break;
			}
		}
		
		if (exists == true) {
			try {
				stream = new BufferedReader(new InputStreamReader(openFileInput(getString(R.string.downloads_cach_filename))));
		
				StringBuilder builder = new StringBuilder();
				String line = "";
		
				while ((line = stream.readLine()) != null) 
				{
					builder.append(line);
				}
				
		        stream.close();
		        
		        JsonParser parser = new JsonParser();
		        
		        JsonArray elements = parser.parse(builder.toString()).getAsJsonArray();
				
		        this.setDownloads(DownloadMap.getDownloadMapFromJson(elements));
		        
		        this.deleteFile(getString(R.string.downloads_cach_filename));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	
	@Override
	public void downloadFinished(DownloadResponse response) {
		this.getDownloadsMapandUpdateList();
	}

	@Override
	public void updateDownloadProgess(DownloadResponse response) {
		this.getDownloadsMapandUpdateList();
	}

	@Override
	public void downloadCancelled(DownloadResponse response) {
		this.getDownloadsMapandUpdateList();
	}
}

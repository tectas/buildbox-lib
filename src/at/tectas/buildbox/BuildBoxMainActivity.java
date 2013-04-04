package at.tectas.buildbox;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import at.tectas.buildbox.adapters.DownloadPackageAdapter;
import at.tectas.buildbox.adapters.TabsAdapter;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.DownloadResponse;
import at.tectas.buildbox.communication.DownloadStatus;
import at.tectas.buildbox.communication.callbacks.BuildBoxDownloadCallback;
import at.tectas.buildbox.communication.callbacks.BuildBoxMapDeserializedProcessCallback;
import at.tectas.buildbox.communication.callbacks.interfaces.DownloadBaseCallback;
import at.tectas.buildbox.content.ItemList;
import at.tectas.buildbox.content.items.DetailItem;
import at.tectas.buildbox.content.items.Item;
import at.tectas.buildbox.content.items.JsonItemParser;
import at.tectas.buildbox.content.items.properties.DownloadType;
import at.tectas.buildbox.download.DownloadActivity;
import at.tectas.buildbox.fragments.ContentListFragment;
import at.tectas.buildbox.fragments.DetailFragment;
import at.tectas.buildbox.fragments.DownloadListFragment;
import at.tectas.buildbox.helpers.PropertyHelper;
import at.tectas.buildbox.preferences.BuildBoxPreferenceActivity;
import at.tectas.buildbox.receiver.UpdateReceiver;
import at.tectas.buildbox.service.DownloadService;
import at.tectas.buildbox.R;

@SuppressLint("DefaultLocale")
public class BuildBoxMainActivity extends DownloadActivity {
	public static final int PICK_FILE_RESULT = 1;
	public static final int SETTINGS_RESULT = 2;
	
	ViewPager mViewPager;
	
	protected PropertyHelper helper = null;
	protected Dialog splashScreen = null;
	protected Hashtable<String, File> backupList = null;
	public ActionBar bar = null;
	protected TabsAdapter adapter = null;
	protected DetailItem romItem = null;
	protected DownloadPackageAdapter downloadAdapter = null;
	protected ItemList contentItems = null;
	protected HashSet<String> contentUrls = new HashSet<String>();
	protected Hashtable<String, Bitmap> remoteDrawables = new Hashtable<String, Bitmap>();
	protected JsonItemParser parser = null;
	protected DownloadBaseCallback downloadCallback = null;
	
	@Override
	public DownloadBaseCallback getDownloadCallback() {
		return this.downloadCallback;
	}
	
	@Override
	public void setDownloadCallback(DownloadBaseCallback callback) {
		this.downloadCallback = callback;
	}
	
	public DownloadPackageAdapter getDownloadPackageAdapter() {
		return this.downloadAdapter;
	}
	
	public void setDownloadPackageAdapter(DownloadPackageAdapter adapter) {
		this.downloadAdapter = adapter;
	}
	
	public Hashtable<String, Bitmap> getRemoteDrawables() {
		return this.remoteDrawables;
	}
	
	public void setRemoteDrawables(Hashtable<String, Bitmap> drawables) {
		this.remoteDrawables = drawables;
	}
	
	public ItemList getContentItems() {
		return this.contentItems;
	}
	
	public Fragment getCurrentFragment() {
		return this.adapter.getCurrentFragment();
	}
	
	public Hashtable<String, File> getBackupList() {
		return this.backupList;
	}
	
	public void setBackupList(Hashtable<String, File> backups) {
		this.backupList = backups;
	}
	
	public DetailItem getRomItem() {
		return this.romItem;
	}
	
	@Override
	public String getDownloadDir() {
		return this.helper.downloadDir;
	}
	
	public void initialize() {
		Item.setActivity(this);
		
		this.helper = new PropertyHelper(this.getApplicationContext());
		
		this.parser = new JsonItemParser(this, this.helper.deviceModel);
		
		this.contentUrls = this.helper.getUserContentUrls();
		
		if (PropertyHelper.stringIsNullOrEmpty(this.helper.romUrl) && PropertyHelper.stringIsNullOrEmpty(this.helper.presetContentUrl) && this.contentUrls.size() == 0) {
			this.addDownloadsTab();
		}
		
		try {
			if (!PropertyHelper.stringIsNullOrEmpty(this.helper.romUrl))
				this.communicator.executeJSONObjectAsyncCommunicator(this.helper.romUrl, this);			
			
			if (!PropertyHelper.stringIsNullOrEmpty(this.helper.presetContentUrl))
				this.communicator.executeJSONArrayAsyncCommunicator(this.helper.presetContentUrl, this);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String url: this.contentUrls) {
			this.communicator.executeJSONArrayAsyncCommunicator(url, this);
		}
		
		this.startUpdateAlarm();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/*StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()
        .detectDiskWrites()
        .detectNetwork()
        .penaltyLog()
        .build());
		*/
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		this.showSplashscreen();
		
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
		
		this.getDownloads().serializeMapToCache(getApplicationContext());
		
		this.downloadMapRestored = false;
		
		super.onStop();
	};

	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		if (!this.downloadMapRestored) {
			this.getDownloads().clear();
			
			if (this.bar.getTabCount() != 0) {
				this.getDownloads().deserializeMapFromCache(getApplicationContext(), this);
			}
			
			this.downloadMapRestored = true;
		}
	}
	
	@Override
	public void startServiceDownload() {
		if (this.downloadCallback == null) {
			this.downloadCallback = new BuildBoxDownloadCallback(this);
		}
		
		super.startServiceDownload(this.downloadCallback, this.downloadCallback, this.downloadCallback);
	}
	
	@Override
	public void onBackPressed() {
	    if (this.adapter.getCurrentFragment() == null || !this.adapter.getCurrentFragment().getChildFragmentManager().popBackStackImmediate()) {
	    	if (!this.getFragmentManager().popBackStackImmediate())
	    		finish();
	    }
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (this.backupList == null) {
			this.fillBackupList();
		}
		
		if (this.backupList.size() == 0) {
			menu.getItem(3).setVisible(false);
		}
		else {
			menu.getItem(3).setVisible(true);
		}
		
		if (this.bar.getTabCount() == 0 || !this.bar.getTabAt(this.adapter.getViewPagerIndex()).getText().equals("Downloads")) {
			menu.getItem(0).setVisible(false);
			menu.getItem(1).setVisible(false);
			menu.getItem(2).setVisible(false);
		}
		else {
			
			if (this.downloadMapContainsBrokenOrAborted()) {
				menu.getItem(0).setVisible(true);
				menu.getItem(1).setVisible(true);
				menu.getItem(2).setVisible(false);
			}
			else if (this.getDownloads().size() == 0) {
				menu.getItem(0).setVisible(false);
				menu.getItem(1).setVisible(false);
				menu.getItem(2).setVisible(false);
			}
			else {
				menu.getItem(0).setVisible(false);
				menu.getItem(1).setVisible(true);
				menu.getItem(2).setVisible(true);
			}
		}
		
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		
		getMenuInflater().inflate(R.menu.download_view_menu, menu);
		
		return true;
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		final BuildBoxMainActivity activity = this;
		
		switch (item.getItemId()) {
			case R.id.settings:
				Intent preferenceIntent = new Intent(this, BuildBoxPreferenceActivity.class);
	            startActivityForResult(preferenceIntent, BuildBoxMainActivity.SETTINGS_RESULT);
	            return true;
			case R.id.remove_broken:
				this.removeBrokenAndAbortedFromMap();
				return true;
			case R.id.remove_all:
				this.downloads.clear();
				this.downloadAdapter.notifyDataSetChanged();
				return true;
			case R.id.add_external:
				 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	             intent.setType("file/*");
	             startActivityForResult(intent,PICK_FILE_RESULT);
				return true;
			case R.id.backup_queue:
				final EditText backupFilenameField = new EditText(this);
				Date now = new Date();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH);
				backupFilenameField.setText(format.format(now));
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
				
				alertBuilder.setTitle(R.string.backup_dialog_title);
				alertBuilder.setMessage(R.string.backup_dialog_text);
				alertBuilder.setView(backupFilenameField);
				
				alertBuilder.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						String filename = backupFilenameField.getText().toString();
						
						if (!PropertyHelper.stringIsNullOrEmpty(filename)) {
							
							File backupDirectory = new File(activity.getDownloadDir() + getString(R.string.kitchen_backup_directory_name));
									
							if (!backupDirectory.exists()) {
								boolean succuessful = backupDirectory.mkdirs();
								
								if (!succuessful) {
									try {
										throw new IOException("Couldn't create directory: " + backupDirectory);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
							
							activity.getDownloads().serializeMapToStorage(activity, backupDirectory.getPath() + "/" + filename + getString(R.string.backup_file_extension));
							
							activity.fillBackupList();
						}
					}
				});
				
				alertBuilder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				alertBuilder.create().show();
				return true;
			case R.id.restore_queue:
				this.fillBackupList();
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				builder.setTitle(getString(R.string.restore_alert_title));
				
				final String[] keys = new String[this.getBackupList().size()];
				
				int i = 0;
				
				for (String key: this.getBackupList().keySet()) {
					
					keys[i] = key.replace(getString(R.string.backup_file_extension), "");
					i++;
				}
				
				Arrays.sort(keys);
				
				builder.setSingleChoiceItems(keys, -1, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
						int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
						
						if (selectedPosition < activity.getBackupList().size()) {
							
							activity.getDownloads().deserializeMapFromStorage(activity, activity.getBackupList().get(keys[selectedPosition] + getString(R.string.backup_file_extension)).getPath(), activity);
						}
					}
				});
				
				builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				builder.create().show();
				
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
  			case PICK_FILE_RESULT:
				String filePath = data.getData().getPath();
				
				if (filePath != null) {
					String[] splittedPath = filePath.split("/");
					
					String fileName = splittedPath[splittedPath.length - 1];
					
					DownloadPackage pack = new DownloadPackage();
					pack.title = fileName;
					
					pack.url = fileName;
					
					DownloadResponse response = new DownloadResponse();
					response.progress = 100;
					response.status = DownloadStatus.Done;
					
					pack.setResponse(response);
					
					pack.setFilename(fileName);
					pack.setDirectory(filePath.replace(fileName, ""));
					pack.md5sum = fileName;
					
					String extension = pack.getResponse().mime;
					
					if (extension != null && extension.toLowerCase().equals(DownloadType.zip.name())) {
						pack.type = DownloadType.zip;
					}
					else {
						pack.type = DownloadType.other;
					}
					
					if (!this.downloadMapRestored) {
						this.getDownloads().clear();
						
						this.loadDownloadsMapFromCacheFile();
						this.downloadMapRestored = true;
					}
					
					this.getDownloads().put(pack);
					
					this.addDownloadsTab();
				}
  				break;
  			case SETTINGS_RESULT:
  				this.getDownloads().serializeMapToCache(this);
  				
  				this.finish();
  				
  				Intent intent = new Intent(this.getApplicationContext(), BuildBoxMainActivity.class);
  				
  				this.startActivity(intent);
  				break;
			case PACKAGE_MANAGER_RESULT:
				if (!this.downloadMapRestored) {
					this.getDownloads().clear();
					
					this.downloadMapRestored = true;
					this.loadDownloadsMapFromCacheFile(new BuildBoxMapDeserializedProcessCallback(this));
				}
  				break;
   		}
	}
	
	@Override
	public void mapDeserializedCallback() {
		if (this.getDownloads().size() != 0) {
			this.addDownloadsTab();
		}
	}
	
	@Override
	protected void loadDownloadsMapFromCacheFile() {
		super.loadDownloadsMapFromCacheFile();
	};
	
	@Override
	public void removeBrokenAndAbortedFromMap() {
		super.removeBrokenAndAbortedFromMap();
		
		this.addDownloadsTab();
	}
	
	@Override
	public void getServiceDownloadMap() {
		super.getServiceDownloadMap();
		
		if (this.getDownloads().size() != 0) {
			
			this.addDownloadsTab();
		}
	}
	
	@Override
	public void getServiceDownloadMap(boolean addListeners) {
		if (addListeners == true) {
			if (this.downloadCallback == null) {
				this.downloadCallback = new BuildBoxDownloadCallback(this);
			}
			
			super.getServiceDownloadMap(this.downloadCallback, this.downloadCallback, this.downloadCallback);
		}
		else {
			super.getServiceDownloadMap();
		}
	}
	
	@Override
	public void updateWithJsonObject(JsonObject result) {
		try {
			this.romItem = (DetailItem) this.parser.parseJsonToItem(result);
			
			if (this.romItem == null && this.helper.presetContentUrl == null) {
				this.addDownloadsTab();
			}
			
			this.adapter.addTab(
					this.bar.newTab().setText("Rom"), 
					DetailFragment.class, 
					this.romItem.parseItemToBundle());

		} 
		catch (NullPointerException e) {
			e.printStackTrace();
			if (this.helper.presetContentUrl == null) {
				this.addDownloadsTab();
			}
		}
		
		if (PropertyHelper.stringIsNullOrEmpty(this.helper.presetContentUrl)) {
			if (DownloadService.Started) {
				this.getServiceMap();
			}
			else {
				this.loadDownloadsMapFromCacheFile();
			}
			
			this.removeSplashscreen();
		}
	}
	
	@Override
	public void updateWithJsonArray(JsonArray result) {
		
		try {
			this.contentItems = this.parser.parseJson(result);
			
			int i = 0;
			
			if ((this.contentItems == null || this.contentItems.size() == 0) && this.romItem == null) {
				this.addDownloadsTab();
				return;
			}
			
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
			this.addDownloadsTab();
		}
		
		if (DownloadService.Started) {
			this.getServiceMap();
		}
		else {
			this.loadDownloadsMapFromCacheFile();
		}
		
		this.removeSplashscreen();
	}

	@Override
	public void updateWithImage(ImageView view, Bitmap bitmap) {
		try {
			if (view != null) {				
				if (!this.remoteDrawables.containsKey((String)view.getTag())) {
					this.remoteDrawables.put((String)view.getTag(), bitmap);
				}
				else {
					bitmap = this.remoteDrawables.get((String)view.getTag());
				}
				
				view.setImageBitmap(bitmap);
				
				Animation animation = view.getAnimation();
				if (animation != null)
					animation.cancel();
			}
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public void startUpdateAlarm() {
		Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 1);
        
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	}
	
	public void addTab(String title, Class<?> clss, Bundle bundle) {
		this.adapter.addTab(this.bar.newTab().setText(title), clss, bundle);
	}
	
	public void addDownloadsTab() {
		if (this.bar.getTabCount() == 0 || !this.bar.getTabAt(this.bar.getTabCount() - 1).getText().equals("Downloads")) {
			this.addTab("Downloads", DownloadListFragment.class, new Bundle());
		}
		
        if (this.downloadAdapter != null) {
        	this.downloadAdapter.notifyDataSetChanged();
        }
	}
	
	public void getDownloadsMapandUpdateList () {
		this.getServiceMap(false);
		
		if (this.getDownloads().size() != 0) {
			
			this.addDownloadsTab();
		}
	}

	protected void showSplashscreen() {
		this.splashScreen = new Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen);
		this.splashScreen.setContentView(R.layout.splashscreen_dialog);
		this.splashScreen.setCancelable(false);
		this.splashScreen.show();
	     
	    final BuildBoxMainActivity activity = this;
	    final Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
	      @Override
	      public void run() {
	    	  activity.removeSplashscreen();
	      }
	    }, 10000);
	}
	
	public void removeSplashscreen() {
		if (this.splashScreen != null) {
			this.splashScreen.dismiss();
			this.splashScreen = null;
		}
	}
	
	protected void fillBackupList() {
		File rootDirectory = new File(this.getDownloadDir());
		
		if (!rootDirectory.exists()) {
			rootDirectory.mkdir();
		}
		
		File queueDirectoy = new File(rootDirectory, getString(R.string.kitchen_backup_directory_name));
		
		if (!queueDirectoy.exists()) {
			queueDirectoy.mkdirs();
		}
		
		File[] fileList = queueDirectoy.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(getString(R.string.backup_file_extension)))
					return true;
				else
					return false;
			}
		});
		
		this.backupList = new Hashtable<String, File>();
		
		for (File file: fileList) {
			this.backupList.put(file.getName(), file);
		}
	}
}

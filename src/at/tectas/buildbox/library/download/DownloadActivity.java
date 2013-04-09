package at.tectas.buildbox.library.download;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import at.tectas.buildbox.R;
import at.tectas.buildbox.library.communication.Communicator;
import at.tectas.buildbox.library.communication.DownloadKey;
import at.tectas.buildbox.library.communication.DownloadMap;
import at.tectas.buildbox.library.communication.DownloadPackage;
import at.tectas.buildbox.library.communication.DownloadResponse;
import at.tectas.buildbox.library.communication.DownloadStatus;
import at.tectas.buildbox.library.communication.callbacks.CallbackType;
import at.tectas.buildbox.library.communication.callbacks.DeserializeMapFinishedCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.DownloadBaseCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.ICommunicatorCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.IDeserializeMapFinishedCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.IDownloadCancelledCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.IDownloadFinishedCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.IDownloadProgressCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.ISerializeMapFinishedCallback;
import at.tectas.buildbox.library.communication.handler.interfaces.IActivityInstallDownloadHandler;
import at.tectas.buildbox.library.content.ItemList;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.content.items.JsonItemParser;
import at.tectas.buildbox.library.content.items.properties.DownloadType;
import at.tectas.buildbox.library.fragments.FlashConfigurationDialog;
import at.tectas.buildbox.library.helpers.PropertyHelper;
import at.tectas.buildbox.library.preferences.BuildBoxPreferenceActivity;
import at.tectas.buildbox.library.receiver.UpdateReceiver;
import at.tectas.buildbox.library.recovery.OpenRecoveryScript;
import at.tectas.buildbox.library.recovery.OpenRecoveryScriptConfiguration;
import at.tectas.buildbox.library.service.DownloadService;

public abstract class DownloadActivity extends FragmentActivity implements ICommunicatorCallback, IDeserializeMapFinishedCallback, ISerializeMapFinishedCallback {

	public static final String TAG = "DownloadActivity";
	public static final int PICK_FILE_RESULT = 1;
	public static final int SETTINGS_RESULT = 2;
	public static final int PACKAGE_MANAGER_RESULT = 3;
	
	protected Communicator communicator = new Communicator();
	protected DownloadMap downloads = new DownloadMap();
	protected PropertyHelper helper = null;
	protected JsonItemParser parser = null;
	protected BaseAdapter dowloadViewAdapter = null;
	protected DownloadServiceConnection serviceConnection = new DownloadServiceConnection(this);
	protected OpenRecoveryScript recoveryScript = null;
	protected Hashtable<String, File> backupList = null;
	protected int currentInstallIndex = 0;
	protected boolean downloadMapRestored = false;
	
	public abstract String getDownloadDir();
	
	public abstract DownloadBaseCallback getDownloadCallback();
	
	public abstract void setDownloadCallback(DownloadBaseCallback callback);
	
	public abstract Hashtable<String, Bitmap> getRemoteDrawables();
	
	public abstract Fragment getCurrentFragment();
	
	public abstract ItemList getContentItems();
	
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
	
	public DownloadServiceConnection getServiceConnection() {
		return this.serviceConnection;
	}
	
	public OpenRecoveryScript getOpenRecoveryScript() {
		return this.recoveryScript;
	}
	
	public void setOpenRecoveryScript(OpenRecoveryScript script) {
		this.recoveryScript = script;
	}
	
	public int getCurrentInstallIndex() {
		return this.currentInstallIndex;
	}
	
	public void setCurrentInstallIndex(int index) {
		this.currentInstallIndex = index;
	}
	
	public void setDownloadViewAdapter(BaseAdapter adapter) {
		this.dowloadViewAdapter = adapter;
	}
	
	public BaseAdapter getDownloadViewAdapter() {
		return this.dowloadViewAdapter;
	}
	
	public Hashtable<String, File> getBackupList() {
		return this.backupList;
	}
	
	public void setBackupList(Hashtable<String, File> backups) {
		this.backupList = backups;
	}
	
	public void initialize() {
		Item.setActivity(this);
		
		this.helper = new PropertyHelper(this.getApplicationContext());
		
		this.parser = new JsonItemParser(this, this.helper.deviceModel);
		
		if (PropertyHelper.stringIsNullOrEmpty(this.helper.romUrl) && PropertyHelper.stringIsNullOrEmpty(this.helper.presetContentUrl) && this.helper.contentUrls.size() == 0) {
			this.refreshDownloadsView();
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
		
		for (String url: this.helper.contentUrls) {
			this.communicator.executeJSONArrayAsyncCommunicator(url, this);
		}
		
		this.startUpdateAlarm();
	}
	
	@Override
	protected void onStop() {		
		this.removeActivityCallbacks();
		
		if (!this.isFinishing()) {
			this.getDownloads().serializeMapToCache(getApplicationContext());
		}
		
		this.downloadMapRestored = false;
		
		super.onStop();
	};
	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		if (!this.downloadMapRestored) {
			this.getDownloads().clear();
			
			if (DownloadService.Started) {
				this.getServiceMap(true);
			}
			else {
				this.restoreDownloadMapFromCache(this);
			}
			
			this.downloadMapRestored = true;
		}
	}
	
	@Override
	protected void onDestroy() {
		this.getApplicationContext().deleteFile(getString(R.string.downloads_cache_filename));
		
		super.onDestroy();
	};
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case PACKAGE_MANAGER_RESULT:
				if (!this.downloadMapRestored) {
					this.getDownloads().clear();
					
					this.loadDownloadsMapFromCacheFile(new DeserializeMapFinishedCallback(this));
					this.downloadMapRestored = true;
				}
  				break;
  			case SETTINGS_RESULT:
  				this.getDownloads().serializeMapToCache(getApplicationContext());
  				
  				this.finish();
  				
  				Intent intent = new Intent(this.getApplicationContext(), this.getClass());
  				
  				this.startActivity(intent);
  				break;
  			case PICK_FILE_RESULT:
  				if (data != null) {
					String filePath = data.getData().getPath();
					
					if (!PropertyHelper.stringIsNullOrEmpty(filePath)) {
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
						
						if (!this.downloadMapRestored) {
							this.getDownloads().clear();
							
							this.loadDownloadsMapFromCacheFile();
							this.downloadMapRestored = true;
						}
						
						this.getDownloads().put(pack);
						
						this.refreshDownloadsView();
					}
  				}
  				break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		final DownloadActivity activity = this;
		
		int itemId = item.getItemId();
		if (itemId == R.id.settings) {
			Intent preferenceIntent = new Intent(this, BuildBoxPreferenceActivity.class);
			startActivityForResult(preferenceIntent, DownloadActivity.SETTINGS_RESULT);
			return true;
		}
		else if (itemId == R.id.remove_broken) {
			this.removeBrokenAndAbortedFromMap();
			return true;
		}
		else if (itemId == R.id.remove_all) {
			this.downloads.clear();
			this.refreshDownloadsView();
			return true;
		}
		else if (itemId == R.id.add_external) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("file/*");
			startActivityForResult(intent,DownloadActivity.PICK_FILE_RESULT);
			return true;
		}
		else if (itemId == R.id.backup_queue) {
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
						
						activity.getDownloads().serializeMapToStorage(activity, backupDirectory.getPath() + "/" + filename + getString(R.string.backup_file_extension), activity);
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
		}
		else if (itemId == R.id.restore_queue) {
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
	public void mapDeserializedCallback() {
		if (this.getDownloads().size() != 0) {
			this.refreshDownloadsView();
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
	
	public boolean allDownloadsFinished() {
		int finishedDownloads = 0;
		
		for (DownloadPackage pack: this.getDownloads().values()) {
			if (pack.getResponse() != null && pack.getResponse().status != DownloadStatus.Pending && pack.getResponse().status != DownloadStatus.Aborted) {
				finishedDownloads++;
			}
		}
		
		if (finishedDownloads == this.getDownloads().size()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean downloadMapContainsBrokenOrAborted() {
		
		for (DownloadPackage pack: this.getDownloads().values()) {
			if (pack.getResponse() != null && (pack.getResponse().status == DownloadStatus.Broken || pack.getResponse().status == DownloadStatus.Aborted)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void removeBrokenAndAbortedFromMap() {
		ArrayList<DownloadKey> keys = new ArrayList<DownloadKey>();
		
		for (DownloadKey key: this.getDownloads().keySet()) {
			DownloadPackage pack = this.getDownloads().get(key);
			
			if (pack.getResponse() != null && (pack.getResponse().status == DownloadStatus.Broken || pack.getResponse().status == DownloadStatus.Aborted)) {
				keys.add(key);
			}
		}
		
		for (DownloadKey key: keys) {
			this.getDownloads().remove(key);
		}
		
		this.refreshDownloadsView();
	}
	
	public void refreshDownloadsView() {		
        if (this.dowloadViewAdapter != null) {
        	this.dowloadViewAdapter.notifyDataSetChanged();
        }
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
			this.serviceConnection.service.stopDownloads();
		}
	}
	
	public void addDownload(DownloadPackage pack) {
		if (DownloadService.Processing == true) {
			if (this.serviceConnection.bound == false) {
				this.serviceConnection.newPackage = pack;
				this.bindDownloadService();
			}
			else {
				this.serviceConnection.service.addDownload(pack);
			}
		}
		else {
			this.getDownloads().put(pack);
		}
		
		this.refreshDownloadsView();
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
			this.getServiceDownloadMap(addListeners);
		}
	}
	
	public void removeActivityCallbacks() {
		if (DownloadService.Started == true) {
			if (this.serviceConnection.bound == false) {
				this.serviceConnection.executeRemoveCallback = true;
				this.bindDownloadService();
			}
			else {
				this.removeCallbacksAndUnbind();
			}
		}
	}
	
	public abstract void startServiceDownload();
	
	public void startServiceDownload(IDownloadProgressCallback progressCallback, IDownloadFinishedCallback finishedCallback, IDownloadCancelledCallback cancelledCallback) {
		if (DownloadService.Processing == true) {
			this.getServiceDownloadMap();
		}
		else {
			this.serviceConnection.service.startDownload(this.getDownloads());
			this.serviceConnection.service.addDownloadListeners(CallbackType.UI, progressCallback, finishedCallback, cancelledCallback);
		}
	}
	
	public abstract void getServiceDownloadMap(boolean addListener);
	
	public void getServiceDownloadMap(IDownloadProgressCallback progressCallback, IDownloadFinishedCallback finishedCallback, IDownloadCancelledCallback cancelledCallback) {
		this.serviceConnection.service.addDownloadListeners(CallbackType.UI, progressCallback, finishedCallback, cancelledCallback);
		
		this.getServiceDownloadMap();
	}
	
	public void getServiceDownloadMap() {
		DownloadMap serviceMap = this.serviceConnection.service.getMap();
		
		if (serviceMap != null && serviceMap.size() != 0) {
			this.setDownloads(serviceMap);
			this.refreshDownloadsView();
			this.getApplicationContext().deleteFile(getString(R.string.downloads_cache_filename));
		}
		else {
			this.loadDownloadsMapFromCacheFile();
		}
	}
	
	public void removeCallbacksAndUnbind() {
		this.serviceConnection.service.removeDownloadListeners(CallbackType.UI);
		
		this.unbindDownloadService();
	}
	
	protected void loadDownloadsMapFromCacheFile() {
		this.getDownloads().deserializeMapFromCache(this.getApplicationContext(), this);
	}

	protected void loadDownloadsMapFromCacheFile(IDeserializeMapFinishedCallback callback) {
		this.getDownloads().deserializeMapFromCache(this.getApplicationContext(), callback);
	}
	
	@Override
	public abstract void updateWithImage(ImageView view, Bitmap bitmap);

	@Override
	public abstract void updateWithJsonArray(JsonArray result);

	@Override
	public abstract void updateWithJsonObject(JsonObject result);
	
	public void iterateDownloadsToInstall() {
		for (; this.currentInstallIndex < this.getDownloads().size(); this.currentInstallIndex++) {
			DownloadPackage pack = this.getDownloads().get(this.currentInstallIndex);
			
			if (pack != null && pack.installHandler != null) {
				
				pack.installHandler.setParentActivity(this);
				
				pack.installHandler.install();
				
				if (pack.installHandler instanceof IActivityInstallDownloadHandler) {
					this.currentInstallIndex++;
					break;
				}
			}
		}
		
		if (this.currentInstallIndex == this.getDownloads().size() && this.recoveryScript != null) {
			this.recoveryScript.executeAndReboot();
		}
	}
	
	public void installFiles() {		
		DownloadPackage pack = this.getDownloads().get(this.currentInstallIndex, DownloadType.zip);
		
		if (pack != null) {
			this.showFlashOptionsDialog();
		}
		else {	
			this.iterateDownloadsToInstall();
		}
	}
	
	public void showFlashOptionsDialog() {
		FlashConfigurationDialog dialog = new FlashConfigurationDialog();
		dialog.show(getFragmentManager(), this.getString(R.string.download_flash_options_title));
	}
	
	public void installZips(ArrayList<Integer> list) {
		OpenRecoveryScriptConfiguration config = new OpenRecoveryScriptConfiguration(this.getDownloadDir());
		
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
		
		this.recoveryScript = new OpenRecoveryScript(config);
		
		this.iterateDownloadsToInstall();
	}
	
	public void restoreDownloadMapFromCache() {
		this.restoreDownloadMapFromCache(null);
	}
	
	public void restoreDownloadMapFromCache(IDeserializeMapFinishedCallback callback) {
		if (!this.downloadMapRestored) {
			this.getDownloads().clear();
			
			if (callback == null) {
				this.loadDownloadsMapFromCacheFile(callback);
			}
			else {
				this.loadDownloadsMapFromCacheFile();
			}
			
			this.downloadMapRestored = true;
		}
	}
	
	public void getDownloadsMapandUpdateList () {
		this.getServiceMap(false);
		
		if (this.getDownloads().size() != 0) {
			
			this.refreshDownloadsView();
		}
	}
	
	protected void fillBackupList() {
		this.fillBackupList(false);
	}
	
	protected void fillBackupList(boolean force) {
		if (this.backupList == null || force) {
			
			File rootDirectory = new File(this.getDownloadDir());
			
			if (!rootDirectory.exists()) {
				rootDirectory.mkdirs();
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
			
			this.invalidateOptionsMenu();
		}
	}
	
	@Override
	public void mapSerializedCallback() {
		this.fillBackupList(true);
	}
}

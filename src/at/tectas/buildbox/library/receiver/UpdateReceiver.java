package at.tectas.buildbox.library.receiver;

import java.util.Calendar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.ImageView;
import at.tectas.buildbox.R;
import at.tectas.buildbox.library.communication.Communicator;
import at.tectas.buildbox.library.communication.callbacks.interfaces.ICommunicatorCallback;
import at.tectas.buildbox.library.content.items.DetailItem;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.content.items.JsonItemParser;
import at.tectas.buildbox.library.download.DownloadActivity;
import at.tectas.buildbox.library.helpers.PropertyHelper;

public class UpdateReceiver extends BroadcastReceiver implements ICommunicatorCallback {
	
	private final int updateNotificationID = 5494;
	private Communicator communicator = new Communicator();
	private PropertyHelper helper = null;
	private JsonItemParser parser = null;
	private DetailItem romItem = null;
	private Context context = null;
	private SharedPreferences pref = null;
	private PowerManager.WakeLock wakelock = null; 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		this.helper = new PropertyHelper(context);
		this.pref = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext());;
		this.parser = new JsonItemParser(context, this.helper.deviceModel);
		
		this.getRomItem();
	}
	
	@SuppressLint("Wakelock")
	public void getRomItem() {
		String romUrl = this.helper.getRomUrl();
		
		if (!PropertyHelper.stringIsNullOrEmpty(romUrl)) {
			try {
				PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	            this.wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
	            wakelock.acquire();
	            
				this.communicator.executeJSONObjectAsyncCommunicator(romUrl, this);
			}
			catch (Exception e) {
				e.printStackTrace();
				wakelock.release();
			}
		}
	}
	
	public void cancelAlarm() {
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
	}
	public void setNewAlarm(int calendarProperty, int interval) {
		Calendar cal = Calendar.getInstance();
        cal.add(calendarProperty, interval);
        
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	}
	
	public void setNewAlarm() {
		this.setNewAlarm(Calendar.HOUR, 6);
	}
	
	@Override
	public void updateWithImage(ImageView view, Bitmap bitmap) {
		
	}

	@Override
	public void updateWithJsonArray(JsonArray result) {
		
	}

	@Override
	public void updateWithJsonObject(JsonObject result) {
		try {			
			if (Item.getActivity() == null) {
				Item.setActivity(this.context);
			}
			
			String updateInterval = this.pref.getString(this.context.getString(R.string.preference_interval_property), null);
			
			if (updateInterval == null) {
				Editor editor = this.pref.edit();
				
				updateInterval = "6";
				
				editor.putString(this.context.getString(R.string.preference_interval_property), updateInterval);
				
				editor.commit();
			}
			
			if (updateInterval.equals("-1") == false) {
				this.romItem = (DetailItem) this.parser.parseJsonToItem(result);
				
				String lastCheckedVersion = this.pref.getString(this.context.getString(R.string.preference_last_checked_version), null);
				
				if (PropertyHelper.stringIsNullOrEmpty(lastCheckedVersion)) {
					Editor editor = this.pref.edit();
					
					editor.putString(this.context.getString(R.string.preference_last_checked_version), this.romItem.version);
					
					editor.commit();
				}
				
				if (PropertyHelper.compareVersions(this.romItem.version, lastCheckedVersion) != 0) {
					String version = this.helper.getVersion();
					
					int comparsion = PropertyHelper.compareVersions(version, this.romItem.version);
					
					if (comparsion == 1) {
						this.notifyUpdate();
					}
					else {
						this.setNewAlarm();
					}
				}
				else {
					this.setNewAlarm();
				}
			}
		}
		catch (Exception e) {
			this.setNewAlarm(Calendar.MINUTE, 1);
			e.printStackTrace();
		}
		wakelock.release();
	}
	
	public void notifyUpdate() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context);
		
		builder.setSmallIcon(R.drawable.buildbox);
		
		builder.setContentTitle(this.context.getString(R.string.update_notification_title));
		
		builder.setContentText(this.context.getString(R.string.update_notification_text));
		
		Intent intent = new Intent(this.context, DownloadActivity.class);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
		
		builder.setContentIntent(pendingIntent);
		
		Notification notification = builder.build();
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.notify(this.updateNotificationID, notification);
	}
}

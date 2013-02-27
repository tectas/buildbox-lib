package at.tectas.buildbox;

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
import android.graphics.Bitmap;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.ImageView;
import at.tectas.buildbox.communication.Communicator;
import at.tectas.buildbox.communication.ICommunicatorCallback;
import at.tectas.buildbox.content.DetailItem;
import at.tectas.buildbox.content.Item;
import at.tectas.buildbox.helpers.JsonItemParser;
import at.tectas.buildbox.helpers.PropertyHelper;

public class UpdateReceiver extends BroadcastReceiver implements ICommunicatorCallback {
	
	private final int updateNotificationID = 5494;
	private Communicator communicator = new Communicator();
	private PropertyHelper helper = null;
	private DetailItem romItem = null;
	private Context context = null;
	private PowerManager.WakeLock wakelock = null; 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		this.helper = new PropertyHelper(context);
		
		this.getRomItem();
	}
	
	@SuppressLint("Wakelock")
	public void getRomItem() {
		String romUrl = this.helper.getRomUrl();
		
		if (romUrl != null) {
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
			
			this.romItem = (DetailItem) JsonItemParser.parseJsonToItem(result);
		
			String version = this.helper.getVersion();
			
			int comparsion = this.compareVersions(version, this.romItem.version);
			
			if (comparsion == 1) {
				this.notifyUpdate();
			}
			else {
				this.setNewAlarm();
			}
		}
		catch (Exception e) {
			this.setNewAlarm(Calendar.MINUTE, 1);
			e.printStackTrace();
		}
		wakelock.release();
	}
	
	public int compareVersions(String localVersion, String remoteVersion) {
		if ((localVersion == null) && (remoteVersion == null))
			return 0;
			
		if (localVersion == null)
			return 1;
		
		if (remoteVersion == null)
			return -1;
		
		String[] localVersionArray = localVersion.split("\\.");
		String[] remoteVersionArray = remoteVersion.split("\\.");
		
		for (int i = 0; i < localVersionArray.length && i < remoteVersionArray.length; i++) {
			if (localVersionArray[i].equals(remoteVersionArray[i])) {
				continue;
			}
			else {
				if (localVersionArray[i].length() < remoteVersionArray[i].length()) {
					return 1;
				}
				else if (localVersionArray[i].length() > remoteVersionArray[i].length()) {
					return -1;
				}
				else {
					for (int j = 0; j < localVersionArray[i].length() && j < remoteVersionArray[i].length(); j++) {
						if (localVersionArray[i].charAt(j) == remoteVersionArray[i].charAt(j)) {
							continue;
						}
						else if (localVersionArray[i].charAt(j) > remoteVersionArray[i].charAt(j)) {
							return -1;
						}
						else {
							return 1;
						}
					}
				}
			}
		}
		
		String[] longerArray = localVersionArray.length > remoteVersionArray.length ? localVersionArray : remoteVersionArray;
		
		String[] shorterArray = localVersionArray.length > remoteVersionArray.length ? localVersionArray : remoteVersionArray;
		
		for (int i = shorterArray.length - 1; i < longerArray.length; i++) {
			if (longerArray[i].equals("0")) {
				continue;
			}
			else {
				if (longerArray[i].length() == 1) {
					return 1;
				}
				else {
					for (int j = 0; j < longerArray[i].length(); j++) {
						if (localVersionArray[i].charAt(j) == '0') {
							continue;
						}
						else {
							return 1;
						}
					}
				}
			}
		}
		
		return 0;
	}
	
	public void notifyUpdate() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context);
		
		builder.setSmallIcon(R.drawable.buildbox);
		
		builder.setContentTitle(this.context.getString(R.string.update_notification_title));
		
		builder.setContentText(this.context.getString(R.string.update_notification_text));
		
		Intent intent = new Intent(this.context, BuildBoxMainActivity.class);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
		
		builder.setContentIntent(pendingIntent);
		
		Notification notification = builder.build();
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.notify(this.updateNotificationID, notification);
	}
}

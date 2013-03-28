package at.tectas.buildbox.helpers;

import android.content.Context;
import at.tectas.buildbox.R;
import at.tectas.buildbox.content.DownloadType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonHelper {
	
	public String tryGetStringFromJson(String property, JsonObject json) {
		String result = null;
		JsonElement dummy = json.get(property);
		
		if (dummy != null && dummy.isJsonPrimitive())
			result = dummy.getAsString();
		
		return result;
	}
	
	public int tryGetIntFromJson(String property, JsonObject json) {
		int result = Integer.MIN_VALUE;
		JsonElement dummy = json.get(property);
		
		if (dummy != null && dummy.isJsonPrimitive())
			result = dummy.getAsInt();
		
		return result;
	}
	
	public JsonArray tryGetJsonArrayFromJson(String property, JsonObject json) {
		JsonElement dummy = null;
		dummy = json.get(property);
		
		JsonArray dummyArray = null;
		
		if (dummy != null && dummy.isJsonArray())
			dummyArray = dummy.getAsJsonArray();
		
		return dummyArray;
	}
	
	public JsonObject tryGetJsonObjectFromJson(String property, JsonObject json) {
		JsonElement dummy = null;
		dummy = json.get(property);
		
		JsonObject dummyArray = null;
		
		if (dummy != null && dummy.isJsonObject())
			dummyArray = dummy.getAsJsonObject();
		
		return dummyArray;
	}
	
	public DownloadType tryGetDownloadType(Context context, JsonObject json) {
		DownloadType result = null;
		JsonElement dummy = json.get(context.getString(R.string.item_download_type_property));
		
		if (dummy != null && dummy.isJsonPrimitive()) {
			try {
				result = DownloadType.valueOf(dummy.getAsString());
			}
			catch (IllegalArgumentException e) {
				result = DownloadType.other;
			}
		}
		else {
			try {
				result = DownloadType.valueOf(ShellHelper.getBuildPropProperty(context.getString(R.string.item_download_default_type)));
			}
			catch (IllegalArgumentException e) {
				
			}
			
			if (result == null) {
				result = DownloadType.other;
			}
		}
		
		return result;
	}
}

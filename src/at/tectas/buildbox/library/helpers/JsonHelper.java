package at.tectas.buildbox.library.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import at.tectas.buildbox.R;
import at.tectas.buildbox.library.content.items.DetailItem;
import at.tectas.buildbox.library.content.items.properties.DownloadType;

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
	
	@SuppressLint("DefaultLocale")
	public DownloadType tryGetDownloadType(Context context, DetailItem item, JsonObject json, DownloadType defaultType) {
		JsonElement dummy = json.get(context.getString(R.string.item_download_type_property));
		
		if (dummy != null && dummy.isJsonPrimitive()) {
			try {
				return DownloadType.valueOf(dummy.getAsString().toLowerCase().trim());
			}
			catch (IllegalArgumentException e) {
				return this.tryGetDownloadTypeFromItem(item, defaultType);
			}
		}
		else {
			return this.tryGetDownloadTypeFromItem(item, defaultType);
		}
	}
	
	public DownloadType tryGetDownloadTypeFromItem(DetailItem item, DownloadType defaultType) {
		if (item.url == null && item.homePages != null && item.homePages.size() > 0) {
			return DownloadType.web;
		}
		else {
			return defaultType;
		}
	}
}

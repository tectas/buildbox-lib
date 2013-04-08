package at.tectas.buildbox.library.helpers;

import com.google.gson.JsonObject;

public interface IJsonSerialize {
	public static JsonSerializer serializer = new JsonSerializer();
	
	public abstract JsonObject serializeToJson();
}

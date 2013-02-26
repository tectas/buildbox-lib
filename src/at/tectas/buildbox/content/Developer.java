package at.tectas.buildbox.content;

import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Developer {
	public String Name;
	public String DonationUrl;
	
	public Developer(JsonObject json) {
		Set<Entry<String, JsonElement>> names = json.entrySet();
		
		for (Entry<String, JsonElement> name: names) {
			this.Name = name.getKey();
			this.DonationUrl = json.get(this.Name).getAsString();
		}
	}
}

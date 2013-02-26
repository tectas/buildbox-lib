package at.tectas.buildbox.content;

import java.util.ArrayList;

import com.google.gson.JsonObject;

import android.os.Bundle;
import at.tectas.buildbox.R;

public class DetailItem extends Item {	
	public String description;
	public String version;
	public String url;
	public String md5sum;
	public ArrayList<Developer> developers = new ArrayList<Developer>();
	public ArrayList<String> homePages = new ArrayList<String>();
	public ArrayList<String> imageUrls = new ArrayList<String>();
	public ArrayList<String> changelog = new ArrayList<String>();
	
	public DetailItem (Item parent, JsonObject json) {
		super(parent, json);

		this.description = Item.helper.tryGetStringFromJson(Item.activity.getString(R.string.description_property), json);
		
		this.version = Item.helper.tryGetStringFromJson(Item.activity.getString(R.string.version_property), json);
		
		this.url = Item.helper.tryGetStringFromJson(Item.activity.getString(R.string.url_property), json);
	
		this.md5sum = Item.helper.tryGetStringFromJson(Item.activity.getString(R.string.md5sum_property), json);
		
		this.tryGetArrayFromJson(Item.activity.getString(R.string.developers_property), json, ArrayTypes.DEVELOPERS);
		
		this.tryGetArrayFromJson(Item.activity.getString(R.string.webpages_property), json, ArrayTypes.HOMEPAGES);
		
		this.tryGetArrayFromJson(Item.activity.getString(R.string.imageurls_property), json, ArrayTypes.IMAGEURLS);
		
		this.tryGetArrayFromJson(Item.activity.getString(R.string.changelog_property), json, ArrayTypes.CHANGELOG);
		
		this.type = ItemTypes.DetailItem;
	}
	
	public DetailItem (JsonObject json) {
		this(null, json);
	}
	
	@Override
	public Bundle parseItemToBundle() {
		Bundle result = super.parseItemToBundle();
		
		result.putString(Item.activity.getString(R.string.item_type_property), Item.activity.getString(R.string.item_detail_type));
		
		result.putString(Item.activity.getString(R.string.description_property), this.description);
		result.putString(Item.activity.getString(R.string.version_property), this.version);
		result.putString(Item.activity.getString(R.string.md5sum_property), this.md5sum);
		result.putString(Item.activity.getString(R.string.url_property), this.url);
		
		result.putStringArrayList(Item.activity.getString(R.string.changelog_property), this.changelog);
		
		result.putStringArrayList(Item.activity.getString(R.string.webpages_property), this.homePages);
		
		result.putStringArrayList(Item.activity.getString(R.string.imageurls_property), this.imageUrls);
		
		if (this.developers.size() > 0) {
			Bundle developers = new Bundle();
			
			ArrayList<String> names = new ArrayList<String>();
			
			ArrayList<String> donation = new ArrayList<String>();
			
			for (int i = 0; i < this.developers.size(); i++) {
				Developer current = this.developers.get(i);
				
				names.add(current.Name);
				
				donation.add(current.DonationUrl == null?"": current.DonationUrl);
			}
			
			developers.putStringArrayList(Item.activity.getString(R.string.developer_names_property), names);
			
			developers.putStringArrayList(Item.activity.getString(R.string.developers_donationurls_property), donation);
			
			result.putBundle(Item.activity.getString(R.string.developers_property), developers);
		}
		
		return result;
	}
}

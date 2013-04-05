package at.tectas.buildbox.content.items;

import java.util.ArrayList;

import com.google.gson.JsonObject;

import android.os.Bundle;
import at.tectas.buildbox.msteam.R;
import at.tectas.buildbox.content.items.properties.ArrayTypes;
import at.tectas.buildbox.content.items.properties.Developer;
import at.tectas.buildbox.content.items.properties.DownloadType;
import at.tectas.buildbox.content.items.properties.ItemTypes;

public class DetailItem extends Item {
	
	public String description;
	public String version;
	public String url;
	public String md5sum;
	public DownloadType downloadType;
	public ArrayList<Developer> developers = new ArrayList<Developer>();
	public ArrayList<String> homePages = new ArrayList<String>();
	public ArrayList<String> imageUrls = new ArrayList<String>();
	public ArrayList<String> changelog = new ArrayList<String>();
	
	public DetailItem (Item parent, JsonObject json) {
		super(parent, json);
		
		if (this.title == null && parent != null) {
			this.title = parent.title;
		}
		
		this.description = Item.helper.tryGetStringFromJson(Item.context.getString(R.string.description_property), json);
		
		this.version = Item.helper.tryGetStringFromJson(Item.context.getString(R.string.version_property), json);
		
		if (this.version == null && this.parent != null && ((ChildItem)this.parent).version != null) {
			this.version = ((ChildItem)this.parent).version;
		}
		
		this.url = Item.helper.tryGetStringFromJson(Item.context.getString(R.string.url_property), json);
	
		this.md5sum = Item.helper.tryGetStringFromJson(Item.context.getString(R.string.md5sum_property), json);
		
		this.tryGetArrayFromJson(Item.context.getString(R.string.developers_property), json, ArrayTypes.DEVELOPERS);
		
		this.tryGetArrayFromJson(Item.context.getString(R.string.webpages_property), json, ArrayTypes.HOMEPAGES);
		
		this.tryGetArrayFromJson(Item.context.getString(R.string.imageurls_property), json, ArrayTypes.IMAGEURLS);
		
		this.tryGetArrayFromJson(Item.context.getString(R.string.changelog_property), json, ArrayTypes.CHANGELOG);
		
		this.downloadType = Item.helper.tryGetDownloadType(Item.context, this, json, Item.parser.defaultType);
		
		this.type = ItemTypes.DetailItem;
	}
	
	public DetailItem (JsonObject json) {
		this(null, json);
	}
	
	@Override
	public Bundle parseItemToBundle() {
		Bundle result = super.parseItemToBundle();
		
		result.putString(Item.context.getString(R.string.item_type_property), Item.context.getString(R.string.item_detail_type));
		
		if (this.downloadType != null)
			result.putString(Item.context.getString(R.string.item_download_type_property), this.downloadType.name());
		
		result.putString(Item.context.getString(R.string.description_property), this.description);
		result.putString(Item.context.getString(R.string.version_property), this.version);
		result.putString(Item.context.getString(R.string.md5sum_property), this.md5sum);
		result.putString(Item.context.getString(R.string.url_property), this.url);
		
		result.putStringArrayList(Item.context.getString(R.string.changelog_property), this.changelog);
		
		result.putStringArrayList(Item.context.getString(R.string.webpages_property), this.homePages);
		
		result.putStringArrayList(Item.context.getString(R.string.imageurls_property), this.imageUrls);
		
		if (this.developers.size() > 0) {
			Bundle developers = new Bundle();
			
			ArrayList<String> names = new ArrayList<String>();
			
			ArrayList<String> donation = new ArrayList<String>();
			
			for (int i = 0; i < this.developers.size(); i++) {
				Developer current = this.developers.get(i);
				
				names.add(current.Name);
				
				donation.add(current.DonationUrl == null?"": current.DonationUrl);
			}
			
			developers.putStringArrayList(Item.context.getString(R.string.developer_names_property), names);
			
			developers.putStringArrayList(Item.context.getString(R.string.developers_donationurls_property), donation);
			
			result.putBundle(Item.context.getString(R.string.developers_property), developers);
		}
		
		return result;
	}
}

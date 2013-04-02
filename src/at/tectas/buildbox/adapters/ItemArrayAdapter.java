package at.tectas.buildbox.adapters;

import java.util.ArrayList;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.communication.Communicator;
import at.tectas.buildbox.content.items.ChildItem;
import at.tectas.buildbox.content.items.DetailItem;
import at.tectas.buildbox.content.items.Item;
import at.tectas.buildbox.content.items.ParentItem;
import at.tectas.buildbox.content.items.properties.ItemTypes;
import at.tectas.buildbox.listeners.ChildItemListItemListener;
import at.tectas.buildbox.listeners.ParentItemListItemListener;

public class ItemArrayAdapter extends ArrayAdapter<at.tectas.buildbox.content.items.Item> {
	
	private final static String TAG = "ItemArrayAdapter";
	private final BuildBoxMainActivity context;
	private final Item[] items;
	private FragmentManager manager = null;
	
	static class ViewHolder {
		public TextView text;
		public ImageView image;
	}
	
	public ItemArrayAdapter(BuildBoxMainActivity context, int textViewResourceId,
			Item[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.items = objects;
	}
	
	public ItemArrayAdapter(BuildBoxMainActivity context, int textViewResourceId,
			ArrayList<Item> objects, FragmentManager manager) {
		this(context, textViewResourceId, objects.toArray(new Item[objects.size()]));
		
		this.manager = manager;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = this.context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_item, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.ListItemTextView);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.ListItemImageView);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		Item item = null;
		
		if (position < this.items.length)
			item = this.items[position];
		
		if (item != null) {
		    holder.text.setText(item.title);
		    
		    try {
		        String thumbnail = (((ParentItem)item).thumbnailUrl);
		        
		        holder.image.setTag(thumbnail);
		        
		        if (thumbnail != null && this.context.getRemoteDrawables().containsKey(thumbnail)) {
		        	holder.image.setImageBitmap(this.context.getRemoteDrawables().get(thumbnail));
		        }
		        else if (thumbnail != null){
			        holder.image.setImageResource(R.drawable.spinner);
					
					RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					
					animation.setDuration(1000);
					
					animation.setRepeatCount(Animation.INFINITE);
					
					animation.setRepeatMode(Animation.INFINITE);
					
					animation.setInterpolator(new LinearInterpolator());
					
					holder.image.startAnimation(animation);
					
					Communicator communicator = this.context.getCommunicator();
					
					communicator.executeBitmapAsyncCommunicator(thumbnail, holder.image, this.context);
		        }
		        else {
		        	holder.image.setVisibility(ImageView.GONE);
		        }
		    }
		    catch (Exception e) {
		        Log.e(ItemArrayAdapter.TAG, " " + e.getMessage());
		        for (StackTraceElement element: e.getStackTrace()) {
		        	Log.e(ItemArrayAdapter.TAG, element.toString());
		        }
		    }
		    
		    if (item.type == ItemTypes.ParentItem) {
		    	
		    	ParentItem child = (ParentItem) item;
			    
			    rowView.setOnClickListener(new ParentItemListItemListener(this.context, child.childs, manager));
		    }
		    
		    else if (item.type == ItemTypes.ChildItem) {
		    	
		    	ChildItem child = (ChildItem) item;
		    	
		    	rowView.setOnClickListener(new ChildItemListItemListener((DetailItem) child.childs.get(0), manager));
		    }
		}
		return rowView;
	}
}

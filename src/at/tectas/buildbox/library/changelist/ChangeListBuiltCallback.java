package at.tectas.buildbox.library.changelist;

import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import at.tectas.buildbox.library.fragments.ChangeListDialog;
import at.tectas.buildbox.library.R;

public class ChangeListBuiltCallback implements IChangeListBuiltCallback {
	
	protected Activity context = null;
	
	public ChangeListBuiltCallback(Context context) {
		if (context instanceof Activity) {
			this.context = (Activity) context;
		}
	}
	
	@Override
	public void notifyBuilt(HashMap<ChangeType, String> changes) {
		if (context != null) {
			if (changes != null && changes.size() > 0) {
				Bundle arguments = new Bundle();
				
				for (Entry<ChangeType, String> entry: changes.entrySet()) {
					arguments.putString(entry.getKey().name(), entry.getValue());
				}
				
				ChangeListDialog dialog = new ChangeListDialog();
				
				dialog.setArguments(arguments);
				
				dialog.show(this.context.getFragmentManager(), this.context.getString(R.string.changelist_dialog_title));
			}
		}
	}
}

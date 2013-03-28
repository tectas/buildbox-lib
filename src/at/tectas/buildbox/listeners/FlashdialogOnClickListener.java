package at.tectas.buildbox.listeners;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.util.Log;
import at.tectas.buildbox.BuildBoxMainActivity;

public class FlashdialogOnClickListener implements DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnClickListener {
	private static final String TAG = "FlashdialogOnClickListener";

	public ArrayList<Integer> list = new ArrayList<Integer>();
	
	public BuildBoxMainActivity activity = null;
	
	public FlashdialogOnClickListener(BuildBoxMainActivity activity) {
		this.activity = activity;
		list.add(Integer.valueOf(0));
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		if (isChecked == true && !list.contains(which)) {
			list.add(Integer.valueOf(which));
		}
		else if (list.contains(which)) {
			list.remove(Integer.valueOf(which));
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Log.e(TAG, "click");
		dialog.dismiss();
		this.activity.installZips(this.list);
	}
}

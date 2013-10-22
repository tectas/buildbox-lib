package at.tectas.buildbox.library.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.tectas.buildbox.library.R;
import at.tectas.buildbox.library.changelist.ChangeType;
import at.tectas.buildbox.library.helpers.PropertyHelper;

public class ChangeListDialog extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		
		builder.setTitle(R.string.changelist_dialog_title);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null && arguments.size() > 0) {			
			ViewGroup view = (ViewGroup)this.getActivity().getLayoutInflater().inflate(R.layout.changelist_dialog, null, false);
			
			String changeText = arguments.getString(ChangeType.added.name());
			
			TextView textView = null;
			
			if (!PropertyHelper.stringIsNullOrEmpty(changeText)) {
				((TextView) view.findViewById(R.id.changelist_added_head)).setVisibility(View.VISIBLE);
				
				textView = (TextView) view.findViewById(R.id.changelist_added);
			
				textView.setText(changeText);
				
				textView.setVisibility(View.VISIBLE);
			}
			
			changeText = arguments.getString(ChangeType.updated.name());
			
			if (!PropertyHelper.stringIsNullOrEmpty(changeText)) {
				((TextView) view.findViewById(R.id.changelist_updated_head)).setVisibility(View.VISIBLE);
				
				textView = (TextView) view.findViewById(R.id.changelist_updated);
				
				textView.setText(changeText);
				
				textView.setVisibility(View.VISIBLE);
			}
			
			changeText = arguments.getString(ChangeType.downgraded.name());
			
			if (!PropertyHelper.stringIsNullOrEmpty(changeText)) {
				((TextView) view.findViewById(R.id.changelist_downgraded_head)).setVisibility(View.VISIBLE);
				
				textView = (TextView) view.findViewById(R.id.changelist_downgraded);
				
				textView.setText(changeText);
				
				textView.setVisibility(View.VISIBLE);
			}
			
			changeText = arguments.getString(ChangeType.removed.name());
			
			if (!PropertyHelper.stringIsNullOrEmpty(changeText)) {
				((TextView) view.findViewById(R.id.changelist_removed_head)).setVisibility(View.VISIBLE);
				
				textView = (TextView) view.findViewById(R.id.changelist_removed);
				
				textView.setText(changeText);

				textView.setVisibility(View.VISIBLE);
			}
			
			builder.setView(view);
			
			return builder.create();
		}
		
		return new Dialog(this.getActivity());
	}
}

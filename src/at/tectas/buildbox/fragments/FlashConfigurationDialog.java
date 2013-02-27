package at.tectas.buildbox.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.listeners.FlashdialogOnClickListener;

public class FlashConfigurationDialog extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(getString(R.string.download_flash_options_title));
		
		int[] checkedInt = getResources().getIntArray(R.array.download_flash_default_options);
		
		boolean[] checked = new boolean[checkedInt.length];
		
		for (int i = 0; i < checkedInt.length; i++) {
			if (checkedInt[i] != 0) {
				checked[i] = true;
			}
			else
				checked[i] = false;
		}
		
		FlashdialogOnClickListener listener = new FlashdialogOnClickListener((BuildBoxMainActivity)getActivity());
		
		builder.setMultiChoiceItems(
				R.array.download_flash_options, 
				checked, 
				listener);
		
		builder.setPositiveButton(R.string.download_flash_text, listener);
		
		builder.setNegativeButton(R.string.download_flash_dialog_cancel_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}
}

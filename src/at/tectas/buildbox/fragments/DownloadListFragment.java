package at.tectas.buildbox.fragments;

import java.util.ArrayList;

import at.tectas.buildbox.R;
import at.tectas.buildbox.adapters.DownloadPackageAdapter;
import at.tectas.buildbox.helpers.SharedObjectsHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class DownloadListFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.download_list_fragment, container, false);
		
		ListView list = (ListView) view.findViewById(R.id.download_list);
		
		DownloadPackageAdapter adapter = new DownloadPackageAdapter(getActivity());
		
		SharedObjectsHelper.downloadAdapter = adapter;
		
		list.setAdapter(SharedObjectsHelper.downloadAdapter);
		
		return view;
	}
}

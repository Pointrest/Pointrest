package com.pointrestapp.pointrest;

import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.pointrestapp.pointrest.adapters.ElencoListCursorAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;


public class FragmentListFrame extends Fragment 
	implements LoaderCallbacks<Cursor>{

//	private static final int POI_LOADER_ID = Constants.TabType.POI;
//	private static final int AC_LOADER_ID = Constants.TabType.AC;
//	private static final int TUTTO_LOADER_ID = Constants.TabType.TUTTO;

	private ElencoListCursorAdapter mElencoListCursorAdapter;
	private Callback mListener;
	private int mCurrentTab;
	
	public static FragmentListFrame getInstance(int tabId) {
		FragmentListFrame vFragment = new FragmentListFrame();
		Bundle vBundle = new Bundle();
		vBundle.putInt(Constants.TAB_TYPE, tabId);
		vFragment.setArguments(vBundle);
		return vFragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof Callback)
			mListener = (Callback)activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vView = inflater.inflate(R.layout.transparent_list, container, false);
		
		Bundle vBundle = getArguments();
		
		if (savedInstanceState != null)
			vBundle = savedInstanceState;
		
		setUpGui(vView, vBundle);
		return vView;
	}

	private void setUpGui(View aView, Bundle aBundle) {
		ListView vListView = (ListView)aView.findViewById(R.id.listView_elenco);
		mElencoListCursorAdapter = new ElencoListCursorAdapter(getActivity(), null, true);
		vListView.setAdapter(mElencoListCursorAdapter);
		vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//mListener.goToDetailScreen((int) id);
			}
			
		});
		
		FrameLayout vFrame = (FrameLayout)aView.findViewById(R.id.frame_map);
		vFrame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//mListener.goToMapScreen();
			}
		});
		//REMOVE
		Button b = (Button)aView.findViewById(R.id.btn_temp);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ContentValues values = new ContentValues();
				int type = new Random().nextInt(3);
				values.put(PuntiDbHelper.NOME, "punto" + type);
				values.put(PuntiDbHelper.TYPE, type);
				getActivity().getContentResolver().insert(PuntiContentProvider.PUNTI_URI, values);
			}
		});
		
		
		if (aBundle != null) {
			int tabId = aBundle.getInt(Constants.TAB_TYPE);
			mCurrentTab = tabId;
		}
		getLoaderManager().initLoader(mCurrentTab, null, this);	

	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(Constants.TAB_TYPE, mCurrentTab);
		super.onSaveInstanceState(outState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader
				(getActivity(),
						PuntiContentProvider.PUNTI_URI,
						null,
						PuntiDbHelper.TYPE + "=?",
						new String[]{ id + "" },
						null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mElencoListCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mElencoListCursorAdapter.swapCursor(null);
	}
	
	public interface Callback {
		void goToDetailScreen(int pointId);
		void goToMapScreen();
	}
}

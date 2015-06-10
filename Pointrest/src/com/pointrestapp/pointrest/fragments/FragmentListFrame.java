package com.pointrestapp.pointrest.fragments;

import java.util.Calendar;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.Constants.NotificationBlocked;
import com.pointrestapp.pointrest.R.id;
import com.pointrestapp.pointrest.R.layout;
import com.pointrestapp.pointrest.adapters.ElencoListCursorAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;


public class FragmentListFrame extends Fragment
		implements LoaderCallbacks<Cursor> {

//	private static final int POI_LOADER_ID = Constants.TabType.POI;
//	private static final int AC_LOADER_ID = Constants.TabType.AC;
//	private static final int TUTTO_LOADER_ID = Constants.TabType.TUTTO;

	private ElencoListCursorAdapter mElencoListCursorAdapter;
	private Callback mListener;
	private int mCurrentTab;
	private View mView;
	ListView mListView;
	
	public FragmentListFrame() {
		System.out.print(true);
	}
	
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
		mView = inflater.inflate(R.layout.transparent_list, container, false);
		
		Bundle vBundle = getArguments();
		
		if (savedInstanceState != null)
			vBundle = savedInstanceState;
		
		setUpGui(mView, vBundle);
		return mView;
	}

	private void setUpGui(View aView, Bundle aBundle) {
		mListView = (ListView)aView.findViewById(R.id.listView_elenco);
		mElencoListCursorAdapter = new ElencoListCursorAdapter(getActivity(), null, true);
		mListView.setAdapter(mElencoListCursorAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//mListener.goToDetailScreen((int) id);
				
				//to remove, only test
				//((MainActivity) mListener).goToNotifiche();
				mListener.goToMapScreen(0, 0);
			}
			
		});
		
		FrameLayout vFrame = (FrameLayout)aView.findViewById(R.id.frame_transparent);

		vFrame.setOnTouchListener(new OnTouchListener() {
		    private static final int MAX_CLICK_DURATION = 200;
		    private long startClickTime;
		    
			@Override
			public boolean onTouch(View v, MotionEvent event) {
			    
		        switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN: {
	                startClickTime = Calendar.getInstance().getTimeInMillis();
	                break;
	            }
	            case MotionEvent.ACTION_UP: {
	                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
	                if(clickDuration < MAX_CLICK_DURATION) {
	                	//mListener.goToMapScreen(event.getX() * event.getXPrecision(), event.getY() * event.getYPrecision());
	                	//mListener.goToMapScreen(event.getRawX(), event.getRawY());
	                	mListener.goToMapScreen(event.getX(), event.getY());
	                }
	            }
	        }
	        return true;
			}
		});
		
		vFrame.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				ContentValues values = new ContentValues();
				Random r = new Random();
				int type = r.nextInt(3);
				values.put(PuntiDbHelper.NOME, "punto" + type);
				values.put(PuntiDbHelper.TYPE, type);
				values.put(PuntiDbHelper.BLOCKED, Constants.NotificationBlocked.TRUE);
				double lat = r.nextDouble() + r.nextInt(50);
				double lang = r.nextDouble() + r.nextInt(50);
				values.put(PuntiDbHelper.LATUTUDE, lat);
				values.put(PuntiDbHelper.LONGITUDE, lang);
				getActivity().getContentResolver().insert(PuntiContentProvider.PUNTI_URI, values);
				return true;
			}
		});
		
		if (aBundle != null) {
			int tabId = aBundle.getInt(Constants.TAB_TYPE);
			mCurrentTab = tabId;
		}
		
	}
	
	@Override
	public void onResume() {
		getLoaderManager().initLoader(mCurrentTab, null, this);
		super.onResume();
	};
	
	@Override
		public void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
		}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(Constants.TAB_TYPE, mCurrentTab);
		super.onSaveInstanceState(outState);
	}

	public interface Callback {
		void goToDetailScreen(int pointId);
		void goToMapScreen(float clixkedX, float clickedY);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),
								PuntiContentProvider.PUNTI_URI,
								null,
								PuntiDbHelper.TYPE + "=?",
								new String[]{ mCurrentTab + "" },
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
}

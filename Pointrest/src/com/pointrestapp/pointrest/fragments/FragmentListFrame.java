package com.pointrestapp.pointrest.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.adapters.ElencoListCursorAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;

public class FragmentListFrame extends Fragment implements
		LoaderCallbacks<Cursor> {

	public static final String DETTAGLIO_ID = "DETTAGLIO_ID";
	// private static final int POI_LOADER_ID = Constants.TabType.POI;
	// private static final int AC_LOADER_ID = Constants.TabType.AC;
	// private static final int TUTTO_LOADER_ID = Constants.TabType.TUTTO;

	private ElencoListCursorAdapter mElencoListCursorAdapter;
	private Callback mListener;
	private int mCategory;
	private View mView;
	ListView mListView;
	TranslateAnimation mAnimation;
	private SharedPreferences mSettings;

	public FragmentListFrame() {
		System.out.print(true);
	}

	public static FragmentListFrame getInstance(int categoryId) {
		FragmentListFrame vFragment = new FragmentListFrame();
		Bundle vBundle = new Bundle();
		vBundle.putInt(Constants.CATEGORY_TYPE, categoryId);
		vFragment.setArguments(vBundle);
		return vFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof Callback)
			mListener = (Callback) activity;
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
		mSettings = this.getActivity().getSharedPreferences(
				Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);

		mListView = (ListView) aView.findViewById(R.id.listView_elenco);
		mElencoListCursorAdapter = new ElencoListCursorAdapter(getActivity(),
				null, true);
		mListView.setAdapter(mElencoListCursorAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mListener.goToDetailScreen((int) id);
			}

		});

		FrameLayout vFrame = (FrameLayout) aView
				.findViewById(R.id.frame_transparent);
		vFrame.setOnTouchListener(new OnTouchListener() {
			private static final int MAX_CLICK_DURATION = 200;
			private long startClickTime;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/*
				 * populateDbWithDummyPoint(); switch (event.getAction()) { case
				 * MotionEvent.ACTION_DOWN: { startClickTime =
				 * Calendar.getInstance().getTimeInMillis(); break; } case
				 * MotionEvent.ACTION_UP: { long clickDuration =
				 * Calendar.getInstance() .getTimeInMillis() - startClickTime;
				 * if (clickDuration < MAX_CLICK_DURATION) { //
				 * mListener.goToMapScreen(event.getX() * //
				 * event.getXPrecision(), event.getY() * //
				 * event.getYPrecision()); //
				 * mListener.goToMapScreen(event.getRawX(), // event.getRawY());
				 * // mListener.goToMapScreen(event.getX(), event.getY());
				 * MotionEvent e = event; e.setLocation(630, 1113);
				 * mListener.goToMapScreen(event); } } } return true;
				 */
				return false;
			}
		});
		
		if (aBundle != null)
			mCategory = aBundle.getInt(Constants.CATEGORY_TYPE);

		// animate();
	}

	public void animate() {
		mAnimation = new TranslateAnimation(0, 0, 0, 599);
		mAnimation.setDuration(10000);
		mAnimation.setFillAfter(true);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mListView.setAnimation(mAnimation);
		mListView.startAnimation(mAnimation);
	}

	@Override
	public void onResume() {
		getLoaderManager().initLoader(mCategory, null, this);
		super.onResume();
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(Constants.CATEGORY_TYPE, mCategory);
		super.onSaveInstanceState(outState);
	}

	public interface Callback {
		void goToDetailScreen(int pointId);

		void goToMapScreen(float clixkedX, float clickedY);

		void goToMapScreen(MotionEvent event);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String selection = null;
		String[] selectionArgs = null;
		if (mCategory != Constants.TabType.TUTTO
				&& !mSettings.getBoolean(
						Constants.SharedPreferences.SEARCH_ENABLED, false)) {
			selection = PuntiDbHelper.CATEGORY_ID + "=?";
			selectionArgs = new String[] { mCategory + "" };
		}

		int sottocategoria_id = mSettings.getInt(
				Constants.SharedPreferences.SUB_CATEGORY_ID, -9898);
		boolean only_fav = mSettings.getBoolean(
				Constants.SharedPreferences.ONLY_FAVOURITE, false);
		List<String> selectionArgsTmp = new ArrayList<String>();
		if (mSettings.getBoolean(Constants.SharedPreferences.SEARCH_ENABLED,
				false)) {
			if (sottocategoria_id != -9898) {
				selection = PuntiDbHelper.SOTTOCATEGORIA_ID + "=?";
				if (only_fav) {
					selection += " and " + PuntiDbHelper.FAVOURITE + "=?";
					if (mCategory != Constants.TabType.TUTTO)
						selection += " and " + PuntiDbHelper.CATEGORY_ID + "=?";
				} else {
					if (mCategory != Constants.TabType.TUTTO)
						selection += " and " + PuntiDbHelper.CATEGORY_ID + "=?";
				}
			} else {
				if (only_fav) {
					selection = PuntiDbHelper.FAVOURITE + "=?";
					if (mCategory != Constants.TabType.TUTTO)
						selection += " and " + PuntiDbHelper.CATEGORY_ID + "=?";
				} else {
					if (mCategory != Constants.TabType.TUTTO)
						selection = PuntiDbHelper.CATEGORY_ID + "=?";
				}
			}
			if (sottocategoria_id != -9898)
				selectionArgsTmp.add(sottocategoria_id + "");
			if (only_fav)
				selectionArgsTmp.add("1");
			if (mCategory != Constants.TabType.TUTTO)
				selectionArgsTmp.add(mCategory + "");
			selectionArgs = new String[selectionArgsTmp.size()];
			for (int i = 0; i < selectionArgsTmp.size(); i++) {
				selectionArgs[i] = selectionArgsTmp.get(i);
			}
		}

		return new CursorLoader(getActivity(), PuntiContentProvider.PUNTI_URI,
				null, selection, selectionArgs, null);
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

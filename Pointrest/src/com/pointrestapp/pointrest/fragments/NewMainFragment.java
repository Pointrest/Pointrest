package com.pointrestapp.pointrest.fragments;

import java.util.ArrayList;
import java.util.List;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.MyMapView;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.activities.MainActivity;
import com.pointrestapp.pointrest.adapters.TabAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;

public class NewMainFragment extends Fragment implements
		TabAdapter.TabSelectedListener, OnMapReadyCallback {

	private static final String CATEGORY_ID = "category_id";
	private int mCategoryId = Constants.TabType.TUTTO;

	private ViewPager mViewPager;
	private TabAdapter mTabsAdapter;

	private GoogleMap mMap;
	private MyMapView mMapView;
	private List<Marker> mMarkers = new ArrayList<Marker>();
	private boolean mLoadedMarkers;

	private View mFrameBelow;
	private RelativeLayout mLayoutWhole;
	private boolean mFullscreen;
	private MainActivity mHostActivity;

	private SharedPreferences mSettings;

	public static NewMainFragment getInstance() {
		return new NewMainFragment();
	}

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof MainActivity)
			mHostActivity = (MainActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vView = inflater.inflate(R.layout.new_big_layout_test, container,
				false);
		mSettings = this.getActivity().getSharedPreferences(
				Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);
		setupGui(savedInstanceState, vView);
		return vView;
	}

	private void setupGui(Bundle savedInstanceState, View vView) {
		mViewPager = (ViewPager) vView.findViewById(R.id.pager);
		AppCompatActivity vActivity = (AppCompatActivity) getActivity();
		ActionBar bar = vActivity.getSupportActionBar();
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

		if (savedInstanceState != null) {
			mCategoryId = savedInstanceState.getInt(CATEGORY_ID);
		}

		mTabsAdapter = new TabAdapter(vActivity, getChildFragmentManager());
		mViewPager.setAdapter(mTabsAdapter);
		mViewPager.addOnPageChangeListener(mTabsAdapter);

		mMapView = (MyMapView) vView.findViewById(R.id.mapview);
		mFrameBelow = (View) vView.findViewById(R.id.frame_map_below);
		mLayoutWhole = (RelativeLayout) vView.findViewById(R.id.relative_tab);
		LayoutTransition lt = new LayoutTransition();
		lt.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 2000);
		lt.setDuration(5000);

		mLayoutWhole.setLayoutTransition(lt);
		mMapView.onCreate(savedInstanceState);
		mMapView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		mMapView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println();
			}
		});

		// We need this listener to avoid adding LatLngs to a 0 size MapView
		mMapView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// if (!mLoadedMarkers)
						onTabSelected(mCategoryId);
					}
				});
	}

	@Override
	public void onMapReady(GoogleMap arg0) {
		mMap = arg0;
		mMap.setMyLocationEnabled(true);
	}

	private void showMarkersForType(int categoryId) {
		mCategoryId = categoryId;
		boolean haveAtLeastOnePointToShow = false;
		// Che c posso fare qua?
		if (mMap == null || getActivity() == null)
			return;
		mLoadedMarkers = true;
		mMap.clear();
		Builder vBoundsBuilder = LatLngBounds.builder();

		int sottocategoria_id = mSettings.getInt(
				Constants.SharedPreferences.SUB_CATEGORY_ID, -9898);
		boolean only_fav = mSettings.getBoolean(
				Constants.SharedPreferences.ONLY_FAVOURITE, false);

		String selection = null;
		String[] selectionArgs = null;
		List<String> selectionArgsTmp = new ArrayList<String>();
		if (mCategoryId != Constants.TabType.TUTTO
				&& !mSettings.getBoolean(
						Constants.SharedPreferences.SEARCH_ENABLED, false)) {
			selection = PuntiDbHelper.CATEGORY_ID + "=?";
			selectionArgs = new String[] { categoryId + "" };
		}
		if (mSettings.getBoolean(Constants.SharedPreferences.SEARCH_ENABLED,
				false)) {
			selection = (sottocategoria_id != -9898 ? PuntiDbHelper.SOTTOCATEGORIA_ID
					+ "=? "
					+ (only_fav ? " and "
							+ PuntiDbHelper.FAVOURITE
							+ "=?"
							+ (mCategoryId != Constants.TabType.TUTTO ? " and "
									+ PuntiDbHelper.CATEGORY_ID + "=?" : "")
							: (mCategoryId != Constants.TabType.TUTTO ? PuntiDbHelper.CATEGORY_ID
									+ "=?"
									: ""))
					: (only_fav ? PuntiDbHelper.FAVOURITE
							+ "=?"
							+ (mCategoryId != Constants.TabType.TUTTO ? " and "
									+ PuntiDbHelper.CATEGORY_ID + "=?" : "")
							: (mCategoryId != Constants.TabType.TUTTO ? PuntiDbHelper.CATEGORY_ID
									+ "=?"
									: "")))
					+ (mCategoryId != Constants.TabType.TUTTO ? " and "
							+ PuntiDbHelper.CATEGORY_ID + "=?" : "");

			if (sottocategoria_id != -9898)
				selectionArgsTmp.add(sottocategoria_id + "");
			if (only_fav)
				selectionArgsTmp.add("1");
			if (mCategoryId != Constants.TabType.TUTTO)
				selectionArgsTmp.add(categoryId + "");
			selectionArgs = new String[selectionArgsTmp.size()];
			for (int i = 0; i < selectionArgsTmp.size(); i++) {
				selectionArgs[i] = selectionArgsTmp.get(i);
			}
		}
		Cursor cursor = null;
		try {
			cursor = getActivity().getContentResolver().query(
					PuntiContentProvider.PUNTI_URI, null, selection,
					selectionArgs, null);

			int pointNameIndex = cursor.getColumnIndex(PuntiDbHelper.NOME);
			int pointFavIndex = cursor.getColumnIndex(PuntiDbHelper.FAVOURITE);
			int pointLatIndex = cursor.getColumnIndex(PuntiDbHelper.LATUTUDE);
			int pointLonIndex = cursor.getColumnIndex(PuntiDbHelper.LONGITUDE);

			BitmapDescriptor icon = null;

			while (cursor.moveToNext()) {
				haveAtLeastOnePointToShow = true;
				LatLng vLatLng = new LatLng(cursor.getDouble(pointLatIndex),
						cursor.getDouble(pointLonIndex));
				vBoundsBuilder.include(vLatLng);

				icon = cursor.getInt(pointFavIndex) == Constants.Favourite.TRUE ? BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
						: BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_RED);

				mMarkers.add(mMap.addMarker(new MarkerOptions()
						.title(cursor.getString(pointNameIndex))
						.position(vLatLng).icon(icon)));
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		if (haveAtLeastOnePointToShow)
			mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
					vBoundsBuilder.build(), getResources()
							.getDimensionPixelSize(R.dimen.map_padding)));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mMapView != null)
			mMapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (mMapView != null)
			mMapView.onLowMemory();
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.getMapAsync(this);
		if (mMapView != null)
			mMapView.onResume();
		int tab = mTabsAdapter.getTabPositionFromCategoryId(mCategoryId);
		mViewPager.setCurrentItem(tab, true);
	}

	@Override
	public void onTabSelected(int categoryId) {
		mCategoryId = categoryId;
		showMarkersForType(categoryId);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(CATEGORY_ID, mCategoryId);
		super.onSaveInstanceState(outState);
	}

	public void prepareForShow(final MotionEvent event) {
		/*
		 * long downTime = SystemClock.uptimeMillis(); long eventTime =
		 * SystemClock.uptimeMillis() + 100; // List of meta states found here:
		 * developer
		 * .android.com/reference/android/view/KeyEvent.html#getMetaState() int
		 * metaState = 0; MotionEvent motionEvent = MotionEvent.obtain(
		 * downTime, eventTime, MotionEvent.ACTION_UP, x, y, metaState );
		 */

		/*
		 * int h = mLayoutWhole.getHeight(); LayoutParamANimation a = new
		 * LayoutParamANimation(mMapView, h); a.setDuration(500); //a.start();
		 * mMapView.startAnimation(a);
		 */
		/*
		 * @SuppressWarnings("unused") float z = mFrameBelow.getHeight() / 2;
		 * mLayoutWhole.animate() .scaleY(mLayoutWhole.getHeight() +
		 * mFrameBelow.getHeight()) .setListener(new Animator.AnimatorListener()
		 * {
		 * 
		 * @Override public void onAnimationStart(Animator animation) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onAnimationRepeat(Animator animation) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onAnimationEnd(Animator animation) {
		 * 
		 * }
		 * 
		 * @Override public void onAnimationCancel(Animator animation) { // TODO
		 * Auto-generated method stub
		 * 
		 * } }) //.setDuration(3) .setInterpolator(new LinearInterpolator())
		 * .start();
		 * //((ViewManager)mFrameBelow.getParent()).removeView(mFrameBelow);
		 * //mMapView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		 * LayoutParams.FILL_PARENT));
		 */
		/*
		 * int h = mLayoutWhole.getHeight(); Animation ani = new
		 * LayoutParamANimation(mMapView, h); ani.setDuration(10000);
		 * mMapView.startAnimation(ani);
		 */

		Animation a = new WeightChangeAnimation(mFrameBelow, 0f);
		// Animation b = new WeightChangeAnimation(mFrameBelow, 0f);
		// AnimatorSet s = new AnimatorSet();
		a.setDuration(150);
		a.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				getActivity().getWindow().getDecorView()
						.findViewById(android.R.id.content)
						.dispatchTouchEvent(event);
			}
		});
		// b.setDuration(500);
		mFrameBelow.startAnimation(a);
		mFullscreen = true;
		// getActivity().getWindow().getDecorView().findViewById(android.R.id.content).dispatchTouchEvent(motionEvent);

	}

	public class WeightChangeAnimation extends Animation {
		float targetWeight;
		float startingWeight;
		View view;

		public WeightChangeAnimation(View view, float targetWeight) {
			this.view = view;
			this.targetWeight = targetWeight;
			startingWeight = ((android.widget.LinearLayout.LayoutParams) view
					.getLayoutParams()).weight;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			((LinearLayout.LayoutParams) view.getLayoutParams()).weight = startingWeight
					+ (targetWeight - startingWeight) * interpolatedTime;
			view.requestLayout();
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
				int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	}

	public void onBackPressed() {
		// onTabSelected(mCategoryId);
		/*
		 * onTabSelected(mCategoryId); Animation a = new
		 * WeightChangeAnimation(mFrameBelow, 20f); a.setDuration(150);
		 * mFrameBelow.startAnimation(a);
		 */
	}

	public void updateMarkers() {
		showMarkersForType(mCategoryId);
	}

}

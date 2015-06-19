package com.pointrestapp.pointrest.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.MotionEvent;

import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.adapters.TabAdapter;
import com.pointrestapp.pointrest.fragments.FragmentListFrame;
import com.pointrestapp.pointrest.fragments.FragmentMap;
import com.pointrestapp.pointrest.fragments.FragmentTitleScreen;

public class MainScreenActivity extends BaseActivity implements
		TabAdapter.MapCallback,
		TabAdapter.ListCallback,
		FragmentListFrame.Callback {

	private static final String TAG_MAP_SCREEN = "TAG_MAP_SCREEN";
	private static final String TAG_TITLE_SCREEN = "TAG_TITLE_SCREEN";

	private FragmentTitleScreen mTitleScreenFragment;	
	private FragmentMap mMapFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			mTitleScreenFragment = FragmentTitleScreen.getInstance();
			mMapFragment = FragmentMap.getInstance(0);
			getFragmentManager().beginTransaction()
				.add(R.id.container, mMapFragment, TAG_MAP_SCREEN)
				.add(R.id.container, mTitleScreenFragment, TAG_TITLE_SCREEN)
				.commit();
			
		} else {
			mTitleScreenFragment = (FragmentTitleScreen) getFragmentManager().findFragmentByTag(TAG_TITLE_SCREEN);
			mMapFragment = (FragmentMap) getFragmentManager().findFragmentByTag(TAG_MAP_SCREEN);
		}
	}

	@Override
	public void onTabSelected(int puntoType) {
		mMapFragment.onTabSelected(puntoType);
		mTitleScreenFragment.onTabSelected(puntoType);
	}

	@Override
	public void goToDetailScreen(int pointId) {
		
	}
	
	@Override
	public void goToMapScreen(float x, float y) {
		//mMapFragment.prepareForShow(x, y);
		getFragmentManager().beginTransaction()
		.hide(mTitleScreenFragment)
		.addToBackStack(null)
		.commit();
	}
	
	@Override
	public void onBackPressed() {
		mMapFragment.onBackPressed();
		mTitleScreenFragment.OnBackPressed();
		super.onBackPressed();
	}

	@Override
	public Fragment getFragmentForTab(int puntoType) {
		return mTitleScreenFragment.getFragmentForTab(puntoType);
	}

	@Override
	public void goToMapScreen(MotionEvent event) {
		mMapFragment.prepareForShow(event);
		getFragmentManager().beginTransaction()
		.hide(mTitleScreenFragment)
		.addToBackStack(null)
		.commit();
	}

}

package com.pointrestapp.pointrest.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.adapters.TabAdapter;

public class FragmentTitleScreen extends Fragment
	implements TabAdapter.ListCallback,
			   TabAdapter.MapCallback {

	
	private static final String CURRENT_TAB = "CURRENT_TAB";
	private ViewPager mViewPager;
	private Activity mActivity;
	private TabAdapter mTabsAdapter;
	private int mCurrentTab = 1;
	
	public static FragmentTitleScreen getInstance() {
		return new FragmentTitleScreen();
	}

	@Override
	public void onAttach(Activity activity) {
		mActivity = activity;
		super.onAttach(activity);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vView = inflater.inflate(R.layout.fragment_title_screen, container, false);
        mViewPager = (ViewPager) vView.findViewById(R.id.pager);
        ActionBar bar = mActivity.getActionBar();
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        
        if (savedInstanceState != null) {
        	mCurrentTab = savedInstanceState.getInt(CURRENT_TAB);
        }
        
        mTabsAdapter = new TabAdapter(mActivity, getChildFragmentManager());
        
        //mAdaptedFragments = ((MyApplication)getActivity().getApplication()).mAdaptedFragments;
        mViewPager.setAdapter(mTabsAdapter);
        mViewPager.setOnPageChangeListener(mTabsAdapter);
		return vView;
	}
	
	@Override
	public void onResume() {
        mViewPager.setCurrentItem(mCurrentTab, true);
        mTabsAdapter.onPageSelected(mCurrentTab);
		super.onResume();
	}

	@Override
	public Fragment getFragmentForTab(int puntoType) {
		FragmentListFrame f = FragmentListFrame.getInstance(puntoType);
		return f;
	}

	public void OnBackPressed() {
		onTabSelected(mCurrentTab);
	}

	@Override
	public void onTabSelected(int puntoType) {
		mCurrentTab = puntoType;
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(CURRENT_TAB, mCurrentTab);
		super.onSaveInstanceState(outState);
	}
}

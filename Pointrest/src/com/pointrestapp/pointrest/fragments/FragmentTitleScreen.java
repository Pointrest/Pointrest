package com.pointrestapp.pointrest.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.adapters.TabAdapter;

public class FragmentTitleScreen extends Fragment
	implements TabAdapter.TabSelectedListener {

	
	private static final String CATEGORY_ID = "category_id";
	private ViewPager mViewPager;
	private TabAdapter mTabsAdapter;
	private int mCategoryId = Constants.TabType.TUTTO;
	
	public static FragmentTitleScreen getInstance() {
		return new FragmentTitleScreen();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vView = inflater.inflate(R.layout.fragment_title_screen, container, false);
        mViewPager = (ViewPager) vView.findViewById(R.id.pager);
        AppCompatActivity vActivity = (AppCompatActivity)getActivity();
        ActionBar bar = vActivity.getSupportActionBar();
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        
        if (savedInstanceState != null) {
        	mCategoryId = savedInstanceState.getInt(CATEGORY_ID);
        }
        
        mTabsAdapter = new TabAdapter(vActivity, getChildFragmentManager());
        mViewPager.setAdapter(mTabsAdapter);
        mViewPager.setOnPageChangeListener(mTabsAdapter);
		return vView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
        int tab = mTabsAdapter.getTabPositionFromCategoryId(mCategoryId);
        mViewPager.setCurrentItem(tab, true);
	}

	public void OnBackPressed() {
		onTabSelected(mCategoryId);
	}

	@Override
	public void onTabSelected(int categoryId) {
		mCategoryId = categoryId;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(CATEGORY_ID, mCategoryId);
		super.onSaveInstanceState(outState);
	}
}

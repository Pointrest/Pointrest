package com.pointrestapp.pointrest.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.R.id;
import com.pointrestapp.pointrest.R.layout;
import com.pointrestapp.pointrest.adapters.TabAdapter;

public class FragmentTitleScreen extends Fragment {

	
	private ViewPager mViewPager;
	private Activity mActivity;
	private TabAdapter mTabsAdapter;
	public static int h;
	public View v;
	
	public static FragmentTitleScreen getInstance() {
		return new FragmentTitleScreen();
	}

	@Override
	public void onAttach(Activity activity) {
		mActivity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vView = inflater.inflate(R.layout.fragment_title_screen, null);
		
        mViewPager = (ViewPager) vView.findViewById(R.id.pager);
        v = vView.findViewById(R.id.pager_title_strip);
        ActionBar bar = mActivity.getActionBar();
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        mTabsAdapter = new TabAdapter(mActivity, mViewPager);
		return vView;
	}
	
	@Override
	public void onResume() {
        h = v.getHeight();

		super.onResume();
	}
}

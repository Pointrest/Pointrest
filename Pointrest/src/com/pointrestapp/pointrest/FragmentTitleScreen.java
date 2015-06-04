package com.pointrestapp.pointrest;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointrestapp.pointrest.adapters.TabAdapter;

public class FragmentTitleScreen extends Fragment {

	
	private ViewPager mViewPager;
	private Activity mActivity;
	private TabAdapter mTabsAdapter;
	
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
        ActionBar bar = mActivity.getActionBar();
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        mTabsAdapter = new TabAdapter(mActivity, mViewPager);
		return vView;
	}
	
	
}

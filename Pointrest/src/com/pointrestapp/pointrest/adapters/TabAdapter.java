package com.pointrestapp.pointrest.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.pointrestapp.pointrest.fragments.FragmentListFrame;

public class TabAdapter extends FragmentPagerAdapter implements
	ViewPager.OnPageChangeListener {

    private final Callback mListener;
    private final ViewPager mViewPager;	
    private static final int TOTAL_TABS = 3;
	
	@Override
	public CharSequence getPageTitle(int position) {
		String ret = "";
		switch (position) {
		case 0:
			ret = "POI";
			break;
		case 1:
			ret = "TUTTO";
			break;
		case 2:
			ret = "AC";
			break;
		default:
			break;
		}
		return ret;
	}

	public TabAdapter(Activity activity, ViewPager pager) {
		super(activity.getFragmentManager());
        mListener = (Callback)activity;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
	}

	@Override
	public Fragment getItem(int arg0) {
		return FragmentListFrame.getInstance(arg0);
	}

	@Override
	public int getCount() {
		return TOTAL_TABS;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0){
		mListener.onTabSelected(arg0);
	}
	
	public interface Callback {
		void onTabSelected(int puntoType);
	}
}

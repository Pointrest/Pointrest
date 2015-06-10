package com.pointrestapp.pointrest.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class TabAdapter extends FragmentStatePagerAdapter implements
	ViewPager.OnPageChangeListener {

    private final MapCallback mMapListener;
    private final ListCallback mListListener;
    private static final int TOTAL_TABS = 3;
	/*
    @Override
    public int getItemPosition(Object object) {
    	return POSITION_NONE;
    } 
    */
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

	public TabAdapter(Activity activity, FragmentManager fm) {
		super(fm);
        mMapListener = (MapCallback)activity;
        mListListener = (ListCallback)activity;
	}

	@Override
	public Fragment getItem(int arg0) {
		return mListListener.getFragmentForTab(arg0);
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
		mMapListener.onTabSelected(arg0);
	}
	
	public interface MapCallback {
		void onTabSelected(int puntoType);
	}

	public interface ListCallback {
		Fragment getFragmentForTab(int puntoType);
	}
}

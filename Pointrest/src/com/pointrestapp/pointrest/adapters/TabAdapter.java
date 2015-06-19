package com.pointrestapp.pointrest.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.data.CategorieDbHelper;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.fragments.FragmentListFrame;

public class TabAdapter extends FragmentPagerAdapter implements
	ViewPager.OnPageChangeListener {

    private final TabSelectedListener mMapListener;
    private ArrayList<Pair<Integer, String>> mTabs;
    private static int TOTAL_TABS;

	@Override
	public CharSequence getPageTitle(int position) {
		return mTabs.get(position).second;
	}

	public TabAdapter(Activity activity, FragmentManager fm) {
		super(fm);
        mMapListener = (TabSelectedListener)activity;
		Cursor c = ((Context) mMapListener).getContentResolver() 
				.query(PuntiContentProvider.CATEGORIE_URI, null, null, null, null);
		int catNameIndex = c.getColumnIndex(CategorieDbHelper.NAME);
		int catIdIndex = c.getColumnIndex(CategorieDbHelper._ID);
		
		mTabs = new ArrayList<Pair<Integer,String>>();
		
		while (c.moveToNext()) {
			mTabs.add(new Pair<Integer, String>(c.getInt(catIdIndex), c.getString(catNameIndex)));
		}
		
		mTabs.add(1, new Pair<Integer, String>(-1, "Tutti i punti"));
		
		TOTAL_TABS = mTabs.size();
		
	}

	@Override
	public Fragment getItem(int arg0) {
		return FragmentListFrame.getInstance(mTabs.get(arg0).first);
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
		mMapListener.onTabSelected(mTabs.get(arg0).first);
	}
	
	public int getTabPositionFromCategoryId(int categoryId) {
		for (int i = 0; i < mTabs.size(); ++i) {
			if (mTabs.get(i).first == categoryId)
				return i;
		}
		throw new IllegalArgumentException("No such category man!");
	}
	
	public interface TabSelectedListener {
		void onTabSelected(int categoryId);
	}

}

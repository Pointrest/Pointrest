package com.pointrestapp.pointrest.activities;

import android.app.Fragment;
import android.os.Bundle;

import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.fragments.InfoAppFragment;
import com.pointrestapp.pointrest.fragments.NotificheFragment;

public class SimpleActivity extends BaseActivity {

	public static final String FRAGMENT_TO_LOAD =  "fragmenttoload";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle vBundle = getIntent().getExtras();
		
		if (savedInstanceState == null && vBundle != null) {
			loadFragment((FragmentToLoad)vBundle.get(FRAGMENT_TO_LOAD));
		}
	}
	
	protected void loadFragment(FragmentToLoad which) {
		Fragment fragment = null;
		switch (which) {
			case NOTIFICATIONS:
				fragment = NotificheFragment.getInstance();
				break;
			case INFOAPP:
				fragment = InfoAppFragment.getInstance();
				break;
			default:
				return;
		}
		
		getFragmentManager().beginTransaction()
		.replace(R.id.container, fragment)
		.commit();
	}
	
	protected enum FragmentToLoad {
		FILTERS,
		FAVOURITES,
		NOTIFICATIONS,
		INFOAPP
	}
}

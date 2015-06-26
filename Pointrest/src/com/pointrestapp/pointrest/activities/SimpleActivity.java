package com.pointrestapp.pointrest.activities;

import android.app.Fragment;
import android.os.Bundle;

import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.fragments.FiltriRicercaFragment;
import com.pointrestapp.pointrest.fragments.InfoAppFragment;
import com.pointrestapp.pointrest.fragments.NotificheFragment;
import com.pointrestapp.pointrest.fragments.PreferitiFragment;

public class SimpleActivity extends NewBaseActivity {

	public static final String FRAGMENT_TO_LOAD =  "fragmenttoload";
	private NotificheFragment mNotificheFragment;
	
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
		int section;
		switch (which) {
			case NOTIFICATIONS:
				mNotificheFragment = NotificheFragment.getInstance();
				fragment = mNotificheFragment;
				section = FragmentToLoad.NOTIFICATIONS.ordinal() + 1;
				break;
			case INFOAPP:
				fragment = InfoAppFragment.getInstance();
				section = FragmentToLoad.INFOAPP.ordinal() + 1;
				break;
			case FILTERS:
				fragment = FiltriRicercaFragment.getInstance();
				section = FragmentToLoad.FILTERS.ordinal() + 1;
				break;
			case FAVOURITES:
				fragment = PreferitiFragment.getInstance();
				section = FragmentToLoad.FAVOURITES.ordinal() + 1;
				break;
			default:
				return;
		}
		
		getFragmentManager().beginTransaction()
		.replace(R.id.container, fragment)
		.commit();
		//this.onSectionAttached(section);
		this.restoreActionBar();
	}
	
	protected enum FragmentToLoad {
		FILTERS,
		FAVOURITES,
		NOTIFICATIONS,
		INFOAPP
	}

	
}

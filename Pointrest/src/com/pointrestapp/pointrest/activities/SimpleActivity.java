package com.pointrestapp.pointrest.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.fragments.FiltriRicercaFragment;
import com.pointrestapp.pointrest.fragments.FragmentListFrame;
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
		CharSequence title = "";
		switch (which) {
			case NOTIFICATIONS:
				mNotificheFragment = NotificheFragment.getInstance();
				fragment = mNotificheFragment;
				title = getResources().getString(R.string.notifiche_title);
				break;
			case INFOAPP:
				fragment = InfoAppFragment.getInstance();
				title = getResources().getString(R.string.info_app_title);
				break;
			case FILTERS:
				fragment = FiltriRicercaFragment.getInstance();
				title = getResources().getString(R.string.filtri_title);
				break;
			case FAVOURITES:
				fragment = PreferitiFragment.getInstance();
				title = getResources().getString(R.string.preferiti_title);
				break;
			case DETAIL:
				Bundle vBundle = getIntent().getExtras();
				int val = 0;
				if (vBundle != null)
					val = vBundle.getInt(FragmentListFrame.DETTAGLIO_ID);
				//fragment = DeatilFragment.getInstance(val);
				title = getResources().getString(R.string.detail_title);
				break;
			default:
				return;
		}
		
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, fragment)
		.commit();
		this.restoreActionBar(title);
	}
	
	protected enum FragmentToLoad {
		FILTERS,
		FAVOURITES,
		NOTIFICATIONS,
		INFOAPP,
		DETAIL
	}

	
}

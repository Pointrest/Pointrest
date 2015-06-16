package com.pointrestapp.pointrest.activities;

import android.app.Fragment;
import android.os.Bundle;

import com.pointrestapp.pointrest.NotificheBloccateDialog.INotificheBloccateDialog;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.fragments.FiltriRicercaFragment;
import com.pointrestapp.pointrest.fragments.InfoAppFragment;
import com.pointrestapp.pointrest.fragments.NotificheFragment;
import com.pointrestapp.pointrest.fragments.PreferitiFragment;

public class SimpleActivity extends BaseActivity implements INotificheBloccateDialog {

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
		switch (which) {
			case NOTIFICATIONS:
				mNotificheFragment = NotificheFragment.getInstance();
				fragment = mNotificheFragment;
				break;
			case INFOAPP:
				fragment = InfoAppFragment.getInstance();
				break;
			case FILTERS:
				fragment = FiltriRicercaFragment.getInstance();
				break;
			case FAVOURITES:
				fragment = PreferitiFragment.getInstance();
				break;
			default:
				return;
		}
		
		getFragmentManager().beginTransaction()
		.replace(R.id.container, fragment)
		.commit();
		
		this.onSectionAttached(FragmentToLoad.FILTERS.ordinal() + 1);
	}
	
	protected enum FragmentToLoad {
		FILTERS,
		FAVOURITES,
		NOTIFICATIONS,
		INFOAPP
	}

	@Override
	public void onRipristina(long id) {
		mNotificheFragment.onRipristina(id);
	}

	@Override
	public void onVisualizza(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnulla() {
		// TODO Auto-generated method stub
		
	}
}

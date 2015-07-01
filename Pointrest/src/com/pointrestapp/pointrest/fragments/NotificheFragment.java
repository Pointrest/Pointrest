package com.pointrestapp.pointrest.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pointrest.dialog.DialogNotificheBloccate;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.activities.MainActivity;
import com.pointrestapp.pointrest.adapters.NotificheBloccateCursorAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;

public class NotificheFragment extends Fragment implements LoaderCallbacks<Cursor>{
	Switch promo, prossimita;
	TextView titoloPrelista, noNotificheBloccate;
	ListView lista;
	int mStackLevel = 0;
	public static final String DETTAGLIO_ID = "DETTAGLIO_ID";
	
	//private static final String CHIAVE = "CHIAVE1";	
	private static final int NOTIFICHE_BLOCCATE_LOADER_ID = 0;
	private NotificheBloccateCursorAdapter mCursorAdapter;
	public static final String NOTIFICHE_FRAGMENT_PREFS_NOTIFICATIONS = "notifiche_fragment_prefs_notifications";
	private SharedPreferences mSettings;	
	
	long pos;
	private MainActivity mBaseActivity;
	
	private static final String DIALOG_NOTIFICHE = "DIALOG_NOTIFICHE";
	protected static final int DIALOG_NOTIFICHE_BLOCCATE = 0;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mBaseActivity = (MainActivity)activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_notifiche, container, false);
		
		promo = (Switch)v.findViewById(R.id.notifichePromoSwitch);
		prossimita = (Switch)v.findViewById(R.id.notificheProssimitaSwitch);
		lista = (ListView)v.findViewById(R.id.listaNotificheBloccate);
		titoloPrelista = (TextView)v.findViewById(R.id.txtTitoloPrelistaNotificheBloccate);
		noNotificheBloccate = (TextView)v.findViewById(R.id.txtCompariSeNonCiSonoNotifiche);
		
		mCursorAdapter = new NotificheBloccateCursorAdapter(getActivity(), null);
		lista.setAdapter(mCursorAdapter);
		
		// Restore preferences
		mSettings = this.getActivity().getSharedPreferences(NOTIFICHE_FRAGMENT_PREFS_NOTIFICATIONS, Context.MODE_PRIVATE);
        promo.setChecked(mSettings.getBoolean("promoNotification", false));
		prossimita.setChecked(mSettings.getBoolean("preferenceNotification", false));
       
		
		lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mBaseActivity.goToDetailScreen((int)id);
			}
		});
		
		lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				pos = id;
				
				apriDialogRipristina(id);
		        
				return true;				
			}			
		});	
		
		promo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = mSettings.edit();
			    editor.putBoolean("promoNotification", promo.isChecked());
			    editor.commit();	
			    
			    //DA CANCELLARE
			    mBaseActivity.launchLocalNotification(28);
			}
		});
		
		prossimita.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = mSettings.edit();
				editor.putBoolean("preferenceNotification", prossimita.isChecked());
				editor.commit();
			}
		});
				
		getLoaderManager().initLoader(NOTIFICHE_BLOCCATE_LOADER_ID, null, this);
		
		
		lista.setEmptyView(noNotificheBloccate);
		
		return v;
	}
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader
				(getActivity(),
						PuntiContentProvider.PUNTI_URI,
						null,
						PuntiDbHelper.BLOCKED + "=?",
						new String[]{ Constants.NotificationBlocked.TRUE + "" },
						null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mCursorAdapter.swapCursor(null);
	}
	
	public void apriDialogRipristina(long id){

		mStackLevel++;

	    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    DialogFragment dialogFrag = DialogNotificheBloccate.getInstance(id);
        dialogFrag.setTargetFragment(this, DIALOG_NOTIFICHE_BLOCCATE);
        dialogFrag.show(getFragmentManager().beginTransaction(), "ripristina_notifica_bloccata");
        
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
            rimuoviDallaListaRipristinando(data.getLongExtra("ID", -1));
            //Toast.makeText(getActivity().getApplicationContext(), "ripristinando..", Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_CANCELED){
        	
        	Toast.makeText(getActivity().getApplicationContext(), "annulla", Toast.LENGTH_SHORT).show();
        }
	}
	
	private void rimuoviDallaListaRipristinando(long id){
		ContentValues values = new ContentValues();
		values.put(PuntiDbHelper.BLOCKED, Constants.NotificationBlocked.FALSE);
		getActivity().getContentResolver().update(PuntiContentProvider.PUNTI_URI, values, PuntiDbHelper._ID + "=" + id, null);
		Toast mToast = Toast.makeText(getActivity().getApplicationContext(), "Ripristinato con successo!", Toast.LENGTH_SHORT);
        mToast.show();
	}
	
	public void visualizzaPIOnTheMap(){
		Toast mToast = Toast.makeText(getActivity().getApplicationContext(), "(f)visualizzaPiOnTheMap with position: " + pos +" |to implement|", Toast.LENGTH_SHORT);
        mToast.show();
	}
	public static NotificheFragment getInstance() {
		// TODO Auto-generated method stub
		return new NotificheFragment();
	}
}

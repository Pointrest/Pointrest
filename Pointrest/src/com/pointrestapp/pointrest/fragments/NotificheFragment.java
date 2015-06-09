package com.pointrestapp.pointrest.fragments;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.NotificheBloccateDialog;
import com.pointrestapp.pointrest.NotificheBloccateDialog.INotificheBloccateDialog;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.adapters.NotificheBloccateCursorAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;

public class NotificheFragment extends Fragment implements LoaderCallbacks<Cursor>, INotificheBloccateDialog{
	Switch promo, prossimita;
	ListView lista;
	
	//private static final String CHIAVE = "CHIAVE1";	
	private static final int NOTIFICHE_BLOCCATE_LOADER_ID = 0;
	private NotificheBloccateCursorAdapter mCursorAdapter;
	public static final String PREFS_NOTIFICATIONS = "prefs_notifications";
	SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NOTIFICATIONS, 0);
	
	long pos;
	
	private static final String DIALOG_NOTIFICHE = "DIALOG_NOTIFICHE";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_notifiche, container, false);
		
		promo = (Switch)v.findViewById(R.id.notifichePromoSwitch);
		prossimita = (Switch)v.findViewById(R.id.notificheProssimitaSwitch);
		lista = (ListView)v.findViewById(R.id.listaNotificheBloccate);
		
		mCursorAdapter = new NotificheBloccateCursorAdapter(getActivity(), null);
		lista.setAdapter(mCursorAdapter);
		
		// Restore preferences
        promo.setChecked(settings.getBoolean("promoNotification", false));
		prossimita.setChecked(settings.getBoolean("preferenceNotification", false));
       
		
		lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				pos=id;
				NotificheBloccateDialog  dialog = NotificheBloccateDialog.newInstance();
				dialog.show(getFragmentManager(), DIALOG_NOTIFICHE);
				//String item = ((TextView)view).getText().toString();
				//Toast mToast = Toast.makeText(getActivity().getApplicationContext(), "tag -> " + view.getTag(), Toast.LENGTH_SHORT);
                //mToast.show();
			}
		});
		
		lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				pos = id;
				
				
				return false;
				
			}
			
		});	
		
		promo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = settings.edit();
			    editor.putBoolean("silentMode", promo.isChecked());
			    editor.commit();
			}
		});
		
		prossimita.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("silentMode", prossimita.isChecked());
				editor.commit();
			}
		});
		
		getLoaderManager().initLoader(NOTIFICHE_BLOCCATE_LOADER_ID, null, this);	
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
	
	
	/*-- Dialog --*/
	@Override
	public void onRipristina() {
		ContentValues values = new ContentValues();
		values.put(PuntiDbHelper.BLOCKED, Constants.NotificationBlocked.FALSE);
		getActivity().getContentResolver().update(PuntiContentProvider.PUNTI_URI, values, PuntiDbHelper._ID + "=" + pos, null);
		Toast mToast = Toast.makeText(getActivity().getApplicationContext(), "Ripristinato con successo!", Toast.LENGTH_SHORT);
        mToast.show();
	}
	
	@Override
	public void onVisualizza() {
		visualizzaPIOnTheMap();
	}
	@Override
	public void onAnnulla() {
		//onBackPressed();
	}
	
	public void visualizzaPIOnTheMap(){
		Toast mToast = Toast.makeText(getActivity().getApplicationContext(), "(f)visualizzaPiOnTheMap with id: " + pos +" |to implement|", Toast.LENGTH_SHORT);
        mToast.show();
	}
	public static NotificheFragment getInstance() {
		// TODO Auto-generated method stub
		return new NotificheFragment();
	}
}

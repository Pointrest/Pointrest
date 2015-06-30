package com.pointrestapp.pointrest.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pointrest.dialog.DialogPreferitiFragment;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.activities.SimpleActivity;
import com.pointrestapp.pointrest.adapters.PreferitiCursorAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;

public class PreferitiFragment extends Fragment implements LoaderCallbacks<Cursor>{

	protected static final int DIALOG_PREFERITI_FRAGMENT = 0;
	private static final int PREFERITI_LOADER_ID = 34;
	public static final String DETTAGLIO_ID = "DETTAGLIO_ID";
	ListView lista;
	TextView txtNoPref;
	private PreferitiCursorAdapter mCursorAdapter;
	int mStackLevel = 0;
	
	public static PreferitiFragment getInstance() { 
		return new PreferitiFragment();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_preferiti_screen, container, false);
		
		lista = (ListView)v.findViewById(R.id.list_preferiti);
		txtNoPref = (TextView)v.findViewById(R.id.txt_noPreferitiPresenti);
		mCursorAdapter = new PreferitiCursorAdapter(getActivity(), null);
		lista.setAdapter(mCursorAdapter);
		
		lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent vIntent = new Intent(getActivity(), SimpleActivity.class);
				vIntent.putExtra(SimpleActivity.FRAGMENT_TO_LOAD, SimpleActivity.FragmentToLoad.DETAIL);
				vIntent.putExtra(DETTAGLIO_ID, (int)id);
				Log.d("simpleactivity", "from FavouriteFragment to DetailFragment");
				startActivity(vIntent);
			}
		});
		
		lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				dialogRimuoviPreferito(id);
				return true;
			}
		});	
		
		getLoaderManager().initLoader(PREFERITI_LOADER_ID, null, this);
		
		lista.setEmptyView(txtNoPref);
		
		return v;
	}
	
	public void dialogRimuoviPreferito(long id){
		mStackLevel++;

	    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    DialogFragment dialogFrag = DialogPreferitiFragment.getInstance(id);
        dialogFrag.setTargetFragment(this, DIALOG_PREFERITI_FRAGMENT);
        dialogFrag.show(getFragmentManager().beginTransaction(), "rimuovi_fragment_dialog");
	}

	private void rimuoviPreferito(long id){
		ContentValues values = new ContentValues();
		values.put(PuntiDbHelper.FAVOURITE, Constants.Favourite.FALSE);
		getActivity().getContentResolver().update(PuntiContentProvider.PUNTI_URI, values, PuntiDbHelper._ID + "=" + id, null);
		Toast mToast = Toast.makeText(getActivity().getApplicationContext(), "Rimosso con successo!", Toast.LENGTH_SHORT);
        mToast.show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
            rimuoviPreferito(data.getLongExtra("ID", -1));
            Toast.makeText(getActivity().getApplicationContext(), "rimuovi", Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_CANCELED){
        	
        	Toast.makeText(getActivity().getApplicationContext(), "annulla", Toast.LENGTH_SHORT).show();
        }
	}
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader
				(getActivity(),
						PuntiContentProvider.PUNTI_URI,
						null,
						PuntiDbHelper.FAVOURITE + "=?",
						new String[]{ Constants.Favourite.TRUE + "" },
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
}

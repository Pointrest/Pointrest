package com.pointrestapp.pointrest.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pointrest.dialog.ListsDialogRicerca;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.SottocategoriaDbHelper;

public class FiltriRicercaFragment extends Fragment implements LoaderCallbacks<Cursor>{
	LinearLayout lTipo, lCategoria;
	TextView txtTipo, txtCategoria, txtMetri;
	SeekBar raggio;
	Switch soloPreferiti;
	Button resetFiltri, cerca;
	int mStackLevel = 0;
	public static final int DIALOG_FRAGMENT = 1;
	private int progressSeekBar = 0;
	
	public static final String FILTRI_RICERCA_PREFS_NOTIFICATIONS = "filtri_ricerca_prefs_notifications";
	private static final String TIPO_PI_SHARED = "tipo_pi_shared";
	protected static final String SOLO_PREFERITI_SHARED_PREF = "solo_preferiti_shared_pref";
	private static final String CATEGORY_ID = "category_id";
	private SharedPreferences mSettings;
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putInt("level", mStackLevel);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_search_filters, container, false);
		
		if (savedInstanceState != null) {
	        mStackLevel = savedInstanceState.getInt("level");
	    }
		
		lTipo = (LinearLayout)v.findViewById(R.id.layoutTipo);
		lCategoria = (LinearLayout)v.findViewById(R.id.layoutCategoria);
		txtTipo = (TextView)v.findViewById(R.id.tipo_pi);
		txtCategoria = (TextView)v.findViewById(R.id.categoria_pi);
		txtMetri = (TextView)v.findViewById(R.id.metriBySeekBar);
		raggio = (SeekBar)v.findViewById(R.id.raggioInteresse);
		raggio.setMax(19);
		soloPreferiti = (Switch)v.findViewById(R.id.notifichePromo);
		resetFiltri = (Button)v.findViewById(R.id.resetFilters);
		cerca = (Button)v.findViewById(R.id.cercaByFilter);
		
		txtMetri.setText("1 km");
		
		// Restore preferences
		mSettings = this.getActivity().getSharedPreferences(FILTRI_RICERCA_PREFS_NOTIFICATIONS, Context.MODE_PRIVATE);
		progressSeekBar = mSettings.getInt(Constants.SharedPreferences.RAGGIO, 1);
		raggio.setProgress(progressSeekBar - 1);
		txtMetri.setText( + progressSeekBar + " km");
		soloPreferiti.setChecked(mSettings.getBoolean(SOLO_PREFERITI_SHARED_PREF, false));
		switch(mSettings.getInt(TIPO_PI_SHARED, 1)){
			case Constants.TabType.POI:
				txtTipo.setText("Punti di interesse");
				break;
			case Constants.TabType.TUTTO:
				txtTipo.setText("Tutti i PI");
				break;
			case Constants.TabType.AC:
				txtTipo.setText("Attività commerciali");
				break;	
		}
		try{
		Cursor c = getActivity().getContentResolver()
				.query(PuntiContentProvider.SOTTOCATEGORIE_URI, 
						new String[]{SottocategoriaDbHelper.NAME + ""},
						SottocategoriaDbHelper._ID + "=?",
						new String[]{ mSettings.getInt(CATEGORY_ID, 999) + "" },
						null);
    	c.moveToFirst();
    	txtCategoria.setText("" + c.getString(0));
    	c.close();
		}catch(Exception exc){
			txtCategoria.setText("Tutte");
		}
		lTipo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(false);
			}
		});
		lCategoria.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(true);
			}
		});
		soloPreferiti.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = mSettings.edit();
			    editor.putBoolean(SOLO_PREFERITI_SHARED_PREF, soloPreferiti.isChecked());
			    editor.commit();
			}
		});
		
		raggio.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			  
			  
			  @Override
			  public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
				  progressSeekBar = progresValue + 1; //Toast.makeText(getActivity().getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
				  txtMetri.setText( + progressSeekBar + " km");
			  }
			
			  @Override
			  public void onStartTrackingTouch(SeekBar seekBar) {
				  txtMetri.setText( + progressSeekBar + " km");
			  }
			
			  @Override
			  public void onStopTrackingTouch(SeekBar seekBar) {
				  txtMetri.setText( + progressSeekBar + " km");// + seekBar.getMax());
				  SharedPreferences.Editor editor = mSettings.edit();
				  editor.putInt(Constants.SharedPreferences.RAGGIO, progressSeekBar);
				  editor.commit();
			  }
		 });
		 resetFiltri.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressSeekBar=0;
				raggio.setProgress(0);
				txtMetri.setText("1 km");
				soloPreferiti.setChecked(false);
				SharedPreferences.Editor editor = mSettings.edit();
				txtTipo.setText("Tutti i PI");
				editor.putInt(TIPO_PI_SHARED, Constants.TabType.TUTTO);
			    editor.commit();
				//TO IMPLEMENT ---> categoria tutte
			}
		 });
		 cerca.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					cercaByFilter();
				}
			});
		
		return v;
	}
	
	private void cercaByFilter(){
		//valori da passare: raggio, soloPreferiti(boolean), tipo, categoria
	}
	
	void showDialog(boolean isCategoryTipe) {

	    mStackLevel++;

	    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    if(isCategoryTipe){
	    	DialogFragment dialogFrag;
	    		if(txtTipo.getText() == "Punti di interesse")//0
	    		{
	    			dialogFrag = ListsDialogRicerca.getInstance(10, "Scegli la categoria", true, Constants.TabType.POI);
	    		}
	    		else if(txtTipo.getText() == "Attività commerciali") //2
	    		{
					dialogFrag = ListsDialogRicerca.getInstance(10, "Scegli la categoria", true, Constants.TabType.AC);
	    		}
	    		else//(txtTipo.getText() == "Tutti i PI") //1
	    		{
    				dialogFrag = ListsDialogRicerca.getInstance(10, "Scegli la categoria", true, -999);
	    		}
	            
	            dialogFrag.setTargetFragment(this, DIALOG_FRAGMENT);
	            dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
	    }else{
	    	DialogFragment dialogFrag = ListsDialogRicerca.getInstance(11, "Scegli il tipo di PI", false, -10);
            dialogFrag.setTargetFragment(this, DIALOG_FRAGMENT);
            dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
	    }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        switch(requestCode) {
	            case DIALOG_FRAGMENT:
	            	
	            	SharedPreferences.Editor editor = mSettings.edit();
	                
	            	if (resultCode == Activity.RESULT_OK) {
	                    int position = data.getIntExtra("LIST", 999);
	                    boolean is_category = data.getBooleanExtra("IS_CATEGORY", false);
	                    if(!is_category){
	                    	switch(position){
	                    		case 0:
	                			    editor.putInt(TIPO_PI_SHARED, Constants.TabType.TUTTO);
	                			    editor.commit();
	                    			//Constants.TabType.TUTTO;
	                			    txtTipo.setText("Tutti i PI");
	                    			break;
	                    		case 1:
	                			    editor.putInt(TIPO_PI_SHARED, Constants.TabType.AC);
	                			    editor.commit();
	                    			//Constants.TabType.AC;
	                			    txtTipo.setText("Attività commerciali");
	                    			break;
	                    		case 2:
	                    			editor.putInt(TIPO_PI_SHARED, Constants.TabType.POI);
	                			    editor.commit();
	                    			//Constants.TabType.POI;
	                			    txtTipo.setText("Punti di interesse");
	                    			break;
	                    	}
	                    }else{
	                    	int category_id = data.getIntExtra("CATEGORY_ID", 999);
	                    	Cursor c = getActivity().getContentResolver()
	            					.query(PuntiContentProvider.SOTTOCATEGORIE_URI, 
	            							new String[]{SottocategoriaDbHelper.NAME + ""},
	            							SottocategoriaDbHelper._ID + "=?",
	            							new String[]{ category_id + "" },
	            							null);
	                    	c.moveToFirst();
	                    	txtCategoria.setText("" + c.getString(0));
	                    	c.close();
	                    	editor.putInt(CATEGORY_ID, category_id);
            			    editor.commit();
	                    }
	                    //Toast.makeText(getActivity().getApplicationContext(), "Positions " + position + " || isCategory " + is_category, Toast.LENGTH_SHORT).show();
	                } else if (resultCode == Activity.RESULT_CANCELED){
	                	Toast.makeText(getActivity().getApplicationContext(), "Result cancelled", Toast.LENGTH_SHORT).show();
	                }

	                break;
	        }
	}
	
	
	
	//LoaderCallbacks
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}

	public static FiltriRicercaFragment getInstance() {
		// TODO Auto-generated method stub
		return new FiltriRicercaFragment();
	}
}

package com.pointrestapp.pointrest.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
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

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.ListsDialogRicerca;
import com.pointrestapp.pointrest.R;

public class FiltriRicercaFragment extends Fragment implements LoaderCallbacks<Cursor>{
	LinearLayout lTipo, lCategoria;
	TextView txtTipo, txtCategoria, txtMetri;
	SeekBar raggio;
	Switch soloPreferiti;
	Button resetFiltri, cerca;
	int mStackLevel = 0;
	public static final int DIALOG_FRAGMENT = 1;
	private int progressSeekBar = 0;
	
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
		soloPreferiti = (Switch)v.findViewById(R.id.notifichePromo);
		resetFiltri = (Button)v.findViewById(R.id.resetFilters);
		cerca = (Button)v.findViewById(R.id.cercaByFilter);
		
		txtMetri.setText("1 km");
		
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
				//to impelent
			}
		});
		raggio.setMax(19);
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
			  }
		 });
		 resetFiltri.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressSeekBar=0;
				raggio.setProgress(0);
				txtMetri.setText("1 km");
				soloPreferiti.setChecked(false);
				//to implement:
				//tipo tutti
				//categoria tutte
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

	    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
	    Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    if(!isCategoryTipe){
	            DialogFragment dialogFrag = ListsDialogRicerca.getInstance(10, "Scegli il tipo di PI", false);
	            dialogFrag.setTargetFragment(this, DIALOG_FRAGMENT);
	            dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
	    }else{
	    	DialogFragment dialogFrag = ListsDialogRicerca.getInstance(11, "Scegli la categoria", true);
            dialogFrag.setTargetFragment(this, DIALOG_FRAGMENT);
            dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
	    }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        switch(requestCode) {
	            case DIALOG_FRAGMENT:

	                if (resultCode == Activity.RESULT_OK) {
	                    int position = data.getIntExtra("LIST", 999);
	                    boolean is_category = data.getBooleanExtra("IS_CATEGORY", false);
	                    if(!is_category){
	                    	switch(position){
	                    		case 0:
	                    			//Constants.TabType.TUTTO;
	                    			break;
	                    		case 1:
	                    			//Constants.TabType.AC;
	                    			break;
	                    		case 2:
	                    			//Constants.TabType.POI;
	                    			break;
	                    	}
	                    }else{
	                    	
	                    }
	                    Toast.makeText(getActivity().getApplicationContext(), "Positions " + position + " || isCategory " + is_category, Toast.LENGTH_SHORT).show();
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
}

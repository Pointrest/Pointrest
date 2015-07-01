package com.pointrestapp.pointrest.fragments;

import com.pointrest.dialog.ListsDialogRicerca;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.activities.MainScreenActivity;
import com.pointrestapp.pointrest.activities.SimpleActivity;
import com.pointrestapp.pointrest.data.CategorieDbHelper;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.SottocategoriaDbHelper;

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
import android.util.Log;
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

public class FiltriRicercaFragment extends Fragment implements LoaderCallbacks<Cursor>{
	LinearLayout lTipo, lSottoCategoria;
	TextView txtTipoCategoria, txtSottoCategoria, txtMetri;
	SeekBar raggio;
	Switch soloPreferiti;
	Button resetFiltri, cerca;
	int mStackLevel = 0;
	public static final int DIALOG_FRAGMENT = 1;
	private int progressSeekBar = 0;
	
	
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
		lSottoCategoria = (LinearLayout)v.findViewById(R.id.layoutCategoria);
		txtTipoCategoria = (TextView)v.findViewById(R.id.tipo_pi);
		txtSottoCategoria = (TextView)v.findViewById(R.id.categoria_pi);
		txtMetri = (TextView)v.findViewById(R.id.metriBySeekBar);
		raggio = (SeekBar)v.findViewById(R.id.raggioInteresse);
		raggio.setMax(19);
		soloPreferiti = (Switch)v.findViewById(R.id.notifichePromo);
		resetFiltri = (Button)v.findViewById(R.id.resetFilters);
		cerca = (Button)v.findViewById(R.id.cercaByFilter);
		
		txtMetri.setText("1 km");
		
		// Restore preferences
		mSettings = this.getActivity().getSharedPreferences(Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);
		progressSeekBar = mSettings.getInt(Constants.SharedPreferences.RAGGIO, 10);
		raggio.setProgress(progressSeekBar - 1);
		txtMetri.setText( + progressSeekBar + " km");
		soloPreferiti.setChecked(mSettings.getBoolean(Constants.SharedPreferences.ONLY_FAVOURITE, false));
		Cursor cT = null;	//categorie
		try{
    		cT = getActivity().getContentResolver()
    				.query(PuntiContentProvider.CATEGORIE_URI, 
    						new String[]{CategorieDbHelper.NAME + "" },
    						CategorieDbHelper._ID + "=?",
							new String[]{ mSettings.getInt(Constants.SharedPreferences.CATEGORY_ID, -1) + "" },
							null);
    		if(cT.moveToFirst()){
    			txtTipoCategoria.setText(cT.getString(0));  			
    		}
    		else{
    			txtTipoCategoria.setText("Tutti i PI");
    		}
    	}
    	catch(Exception e){
    		Log.d("cursorException", "filtriricercafragment");
    	}
    	finally {
    		if(cT != null)
    			cT.close();
		}
		Cursor cSC = null;	//sottocategorie
		try{
			cSC = getActivity().getContentResolver()
					.query(PuntiContentProvider.SOTTOCATEGORIE_URI, 
							new String[]{SottocategoriaDbHelper.NAME + ""},
							SottocategoriaDbHelper._ID + "=?",
							new String[]{ mSettings.getInt(Constants.SharedPreferences.SUB_CATEGORY_ID, 999) + "" },
							null);
	    	if(cSC.moveToFirst())
	    		txtSottoCategoria.setText("" + cSC.getString(0));
	    	else
				txtSottoCategoria.setText(" -");
		}catch(Exception exc){
			txtSottoCategoria.setText(" -");
		}
		finally {
    		if(cSC != null)
    			cSC.close();
		}
		
		lTipo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(false);
			}
		});
		lSottoCategoria.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(true);
			}
		});
		soloPreferiti.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = mSettings.edit();
			    editor.putBoolean(Constants.SharedPreferences.ONLY_FAVOURITE, soloPreferiti.isChecked());
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
				progressSeekBar=10;
				raggio.setProgress(progressSeekBar - 1);
				txtMetri.setText("10 km");
				soloPreferiti.setChecked(false);
				SharedPreferences.Editor editor = mSettings.edit();
				txtTipoCategoria.setText("Tutti i PI");
				editor.putInt(Constants.SharedPreferences.CATEGORY_ID, Constants.TabType.TUTTO);
			    editor.putInt(Constants.SharedPreferences.SUB_CATEGORY_ID, -9898);
			    editor.commit();
			}
		 });
		 cerca.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences.Editor editor = mSettings.edit();
					editor.putBoolean(Constants.SharedPreferences.SEARCH_ENABLED, true);
					editor.commit();
					cercaByFilter();
				}
			});
		
		return v;
	}
	
	private void cercaByFilter(){
		Intent intent = new Intent(getActivity(), MainScreenActivity.class);
		intent.putExtra("raggio", mSettings.getInt(Constants.SharedPreferences.RAGGIO, 10));
		intent.putExtra("only_pref", mSettings.getBoolean(Constants.SharedPreferences.ONLY_FAVOURITE, false));
		intent.putExtra("cat", mSettings.getInt(Constants.SharedPreferences.CATEGORY_ID, -1));
		intent.putExtra("subCat", mSettings.getInt(Constants.SharedPreferences.SUB_CATEGORY_ID, 999));
		startActivity(intent);
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
	    	/*if(txtSottoCategoria.getText().toString().equals(" -")){
	    		dialogFrag = ListsDialogRicerca.getInstance(10, "Scegli la sottocategoria", true, -999);
				dialogFrag.setTargetFragment(this, DIALOG_FRAGMENT);
	            dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
	    	}*/ // why?
	    	//else{
	    	Cursor c = null;
	    	try{
	    		c = getActivity().getContentResolver()
	    				.query(PuntiContentProvider.CATEGORIE_URI, 
	    						new String[]{CategorieDbHelper.NAME + "", CategorieDbHelper._ID + ""},
	    						null,
	    						null,
	    						null);
	    		if(c.moveToFirst()){
	    			do{
	    				if(txtTipoCategoria.getText().toString().equals(c.getString(0))){
	    					dialogFrag = ListsDialogRicerca.getInstance(10, "Scegli la sottocategoria", true, c.getInt(1));
	    					dialogFrag.setTargetFragment(this, DIALOG_FRAGMENT);
	    		            dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
	    				}
	    			}while(c.moveToNext());	    			
	    		}
	    	}
	    	catch(Exception e){
	    		Log.d("cursorException", "filtriricercafragment");
	    	}
	    	finally {
	    		if(c != null)
	    			c.close();
			}
            
	    }else{
	    	DialogFragment dialogFrag = ListsDialogRicerca.getInstance(11, "Scegli la Categoria", false, -10);
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
	                    boolean is_category = data.getBooleanExtra("IS_CATEGORY", false);
	                    if(!is_category){
	                    	int category_id = data.getIntExtra("CATEGORY_ID", -999);
	                    	Cursor c = null;
	        		    	try{
	        		    		c = getActivity().getContentResolver()
	        		    				.query(PuntiContentProvider.CATEGORIE_URI, 
	        		    						new String[]{CategorieDbHelper.NAME + "", CategorieDbHelper._ID + ""},
	        		    						CategorieDbHelper._ID + "=?",
	        									new String[]{ data.getIntExtra("TYPE_ID", 999) + "" },
	        									null);
	        		    		if(c.moveToFirst()){
	        		    			txtTipoCategoria.setText(c.getString(0));
	        		    			editor.putInt(Constants.SharedPreferences.CATEGORY_ID, c.getInt(1));
	        		    			editor.commit();			
	        		    		}
	        		    	}
	        		    	catch(Exception e){
	        		    		Log.d("cursorException", "filtriricercafragment onActivityResult");
	        		    	}
	        		    	finally {
	        		    		if(c != null)
	        		    			c.close();
	        				}

	                    	txtSottoCategoria.setText(" -");
	                    	editor.putInt(Constants.SharedPreferences.SUB_CATEGORY_ID, -9898);
            			    editor.commit();
	                    }else{
	                    	int category_id = data.getIntExtra("CATEGORY_ID", 999);
	                    	Cursor c = getActivity().getContentResolver()
	            					.query(PuntiContentProvider.SOTTOCATEGORIE_URI, 
	            							new String[]{SottocategoriaDbHelper.NAME + ""},
	            							SottocategoriaDbHelper._ID + "=?",
	            							new String[]{ category_id + "" },
	            							null);
	                    	c.moveToFirst();
	                    	txtSottoCategoria.setText("" + c.getString(0));
	                    	c.close();
	                    	editor.putInt(Constants.SharedPreferences.SUB_CATEGORY_ID, category_id);
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

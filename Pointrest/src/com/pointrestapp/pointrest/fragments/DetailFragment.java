package com.pointrestapp.pointrest.fragments;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.adapters.RecycleViewAdapter;
import com.pointrestapp.pointrest.data.CategorieDbHelper;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
import com.pointrestapp.pointrest.data.PuntiImagesDbHelper;
import com.pointrestapp.pointrest.data.SottocategoriaDbHelper;

public class DetailFragment extends Fragment{

	private static final String PUNTO_ID = "punto id";
	private static final String NOME = "nome";
	private static final String DESCRIZIONE = "descrizione";
	private static final String LATITUDINE = "latitudine";
	private static final String LONGITUDINE = "longitudine";
	private static final String CATEGORIA = "categoria";
	private static final String SOTTOCATEGORIA = "sottocategoria";
	private static final String RATING = "preferito";
	private static final String BLOCCO_NOTIFICHE = "blocco notifiche";
	private TextView name;
	private TextView description;
	private TextView latitudine;
	private TextView longitudine;
	private int punto_id;
	private TextView categoria;
	private TextView sottocategoria;
	private ImageView iconImage;
	private RecyclerView recycleView;
	private LinearLayoutManager layoutManager;
	private ArrayList<Integer> imageIndexArray;
	private RatingBar preferito;
	private Switch bloccoNotifiche;
	
	
	public static DetailFragment getInstance(int id) {
		DetailFragment dtFragment = new DetailFragment();
		Bundle vBundle = new Bundle();
		vBundle.putInt(PUNTO_ID, id);
		dtFragment.setArguments(vBundle);
		return dtFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_detail, container,false);
		
		Cursor cursor = null;
		
		Bundle vBundle = getArguments();
		if(vBundle != null)
			punto_id  = vBundle.getInt(PUNTO_ID);
		
		
		name = (TextView)view.findViewById(R.id.txt_nome);
		description = (TextView)view.findViewById(R.id.txt_descrizione);
		latitudine = (TextView)view.findViewById(R.id.txt_latitudine);
		longitudine = (TextView)view.findViewById(R.id.txt_longitudine);
		categoria = (TextView)view.findViewById(R.id.txt_categoria);
		sottocategoria = (TextView)view.findViewById(R.id.txt_sottocategoria);
		iconImage = (ImageView)view.findViewById(R.id.iconImage);
		
		recycleView = (RecyclerView)view.findViewById(R.id.my_recycler_view);
		recycleView.setHasFixedSize(true);	
		layoutManager = new MyLinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
		recycleView.setLayoutManager(layoutManager);
		recycleView.setAdapter(new RecycleViewAdapter(loadImageArrayIndex(cursor)));
		
		preferito = (RatingBar)view.findViewById(R.id.ratingBar1);
		preferito.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 if (event.getAction() == MotionEvent.ACTION_UP) {
				
						int rating = (int) preferito.getRating();
						
						if(rating < 1){
							preferito.setRating(1);
							updateDB(1, PuntiDbHelper.FAVOURITE);
						}else{
							preferito.setRating(0);
							updateDB(0, PuntiDbHelper.FAVOURITE);
						}	
			        }
			  return true;
			}
		});
		
		bloccoNotifiche = (Switch)view.findViewById(R.id.switch1);
		bloccoNotifiche.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked)
					updateDB(1, PuntiDbHelper.BLOCKED);
				else
					updateDB(0, PuntiDbHelper.BLOCKED);
			}
		});
		
		if(savedInstanceState != null){
			name.setText(savedInstanceState.getString(NOME));
			description.setText(savedInstanceState.getString(DESCRIZIONE));
			latitudine.setText(savedInstanceState.getString(LATITUDINE));
			longitudine.setText(savedInstanceState.getString(LONGITUDINE));
			categoria.setText(savedInstanceState.getString(CATEGORIA));
			sottocategoria.setText(savedInstanceState.getString(SOTTOCATEGORIA));
			preferito.setRating(savedInstanceState.getFloat(RATING));
			bloccoNotifiche.setChecked(savedInstanceState.getBoolean(BLOCCO_NOTIFICHE));
		}else{
			loadPuntoImage(cursor);
			//PUNTO DATI
			loadPuntoDetail(cursor);
			//CATEGORIA
			loadPuntoCategoria(cursor);
			//SOTTOCATEGORIA
			loadPuntoSottocategoria(cursor);		
		}
			
		return view;
	}

	protected void updateDB(int value, String row) {
	
		ContentValues cv = new ContentValues();
		cv.put(row, value);
		
		getActivity().getContentResolver().update(PuntiContentProvider.PUNTI_URI, cv, PuntiDbHelper._ID + "=?",
				new String[]{punto_id + "" });	
		
	}

	private ArrayList<Integer> loadImageArrayIndex(Cursor cursor) {
		imageIndexArray = new ArrayList<Integer>();
		try{
			 cursor = getActivity().getContentResolver().query(PuntiContentProvider.PUNTI_IMAGES_URI, null, PuntiImagesDbHelper.PUNTO_ID + "=?",
						new String[]{punto_id + "" }, null);
			 
			 int imageIndex = cursor.getColumnIndex(PuntiImagesDbHelper._ID);
			
				 while(cursor.moveToNext()){		
					 imageIndexArray.add(cursor.getInt(imageIndex));
				 }	
			}
			finally{
				cursor.close();
			}
		return imageIndexArray;
	}

	private void loadPuntoImage(Cursor cursor) {
		try{
			 cursor = getActivity().getContentResolver().query(PuntiContentProvider.PUNTI_IMAGES_URI, null, PuntiImagesDbHelper.PUNTO_ID + "=?",
						new String[]{punto_id + "" }, null);
			 
			 int imageIndex = cursor.getColumnIndex(PuntiImagesDbHelper._ID);
			
				 if(cursor.moveToNext()){
					 Glide.with(getActivity())
					    .load(Constants.BASE_URL + "immagini/" + cursor.getInt(imageIndex))					  	
					  	.asBitmap()
					  	.diskCacheStrategy(DiskCacheStrategy.ALL)
					    .placeholder(R.drawable.loading)
					    .error(R.drawable.ic_cancel_black_36dp)  
					    .into(iconImage);		
				 }	
			 }
			 finally{
				 cursor.close();
			 }				
	}

	private void loadPuntoSottocategoria(Cursor cursor) {
		try{
			 cursor = getActivity().getContentResolver().query(PuntiContentProvider.SOTTOCATEGORIE_URI, null, SottocategoriaDbHelper._ID + "=?",
						new String[]{punto_id + "" }, null);
			 
			 int nameSottoCatIndex = cursor.getColumnIndex(SottocategoriaDbHelper.NAME);
			 
				 if(cursor.moveToNext()){
					 sottocategoria.setText(sottocategoria.getText() + cursor.getString(nameSottoCatIndex)); 				
				 }	
			}
			finally{
				cursor.close();
			}		
	}

	private void loadPuntoCategoria(Cursor cursor) {
		try{
			 cursor = getActivity().getContentResolver().query(PuntiContentProvider.CATEGORIE_URI, null, CategorieDbHelper._ID + "=?",
						new String[]{punto_id + "" }, null);
			 
			 int nameCategoriaIndex = cursor.getColumnIndex(CategorieDbHelper.NAME);
			
				 if(cursor.moveToNext()){
					 categoria.setText(categoria.getText() + cursor.getString(nameCategoriaIndex)); 				
				 }	
			 }
			 finally{
				 cursor.close();
			 }		
	}

	private void loadPuntoDetail(Cursor cursor) {
		 try{
			 cursor = getActivity().getContentResolver().query(PuntiContentProvider.PUNTI_URI, null, PuntiDbHelper._ID + "=?",
					new String[]{punto_id + "" }, null);
			 
			 int nameIndex = cursor.getColumnIndex(PuntiDbHelper.NOME);
			 int descriptionIndex = cursor.getColumnIndex(PuntiDbHelper.DESCRIZIONE);
			 int latitudineIndex = cursor.getColumnIndex(PuntiDbHelper.LATUTUDE);
			 int longitudineIndex = cursor.getColumnIndex(PuntiDbHelper.LONGITUDE);	
			 int favouriteIndex = cursor.getColumnIndex(PuntiDbHelper.FAVOURITE);
			 int blockeIndex = cursor.getColumnIndex(PuntiDbHelper.BLOCKED);
			
			 if(cursor.moveToNext()){
				 name.setText(cursor.getString(nameIndex)); 
				 description.setText(cursor.getString(descriptionIndex));
				 latitudine.setText(latitudine.getText() + cursor.getString(latitudineIndex));
				 longitudine.setText(longitudine.getText() + cursor.getString(longitudineIndex));	
				 preferito.setRating((float)cursor.getInt(favouriteIndex));
				 bloccoNotifiche.setChecked(cursor.getInt(blockeIndex) == 1 ? true:false);
				 Log.d("RATING_LOADING",""+ cursor.getInt(favouriteIndex) );
			 }	
			}
			finally{
				cursor.close();
			}		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString(NOME, name.getText().toString());
		outState.putString(DESCRIZIONE, description.getText().toString());
		outState.putString(LATITUDINE, latitudine.getText().toString());
		outState.putString(LONGITUDINE, longitudine.getText().toString());
		outState.putString(CATEGORIA, categoria.getText().toString());
		outState.putString(SOTTOCATEGORIA, sottocategoria.getText().toString());
		outState.putFloat(RATING, preferito.getRating());
		outState.putBoolean(BLOCCO_NOTIFICHE, bloccoNotifiche.isChecked());		
	}

	@Override
	public void onResume() {
		super.onResume();
		Cursor cursor = null;
		loadPuntoImage(cursor);
	}

}

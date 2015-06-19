package com.pointrest.dialog;

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
import com.pointrestapp.pointrest.data.SottocategoriaDbHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class ListsDialogRicerca extends DialogFragment {
	private String titoloLista;
	private boolean isCategoryTypeList;
	public static final String TITOLO_LISTA = "titolo_lista";
	public static final String IS_CATEGORY_TYPE_LIST = "is_category_type_list";
	private static final String NUM = "num";
	private static final String CATEGORIA_PRINCIPALE = "categoria_principale";
	private int categoriaPrincipale = 0;
	
	public static ListsDialogRicerca getInstance(int num, String titoloDellaLista, boolean isCategoryTypeList, int categoriaPrincipale){
		ListsDialogRicerca vItem = new ListsDialogRicerca();
		 Bundle bundle = new Bundle();
		    bundle.putInt(NUM, num);
		    bundle.putString(TITOLO_LISTA, titoloDellaLista);
		    bundle.putBoolean(IS_CATEGORY_TYPE_LIST, isCategoryTypeList);
		    bundle.putInt(CATEGORIA_PRINCIPALE, categoriaPrincipale);
		    vItem.setArguments(bundle);
		return vItem;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder vBuilder = new AlertDialog.Builder(getActivity());
		
		titoloLista = getArguments().getString(TITOLO_LISTA);
		isCategoryTypeList = getArguments().getBoolean(IS_CATEGORY_TYPE_LIST);
		categoriaPrincipale = getArguments().getInt(CATEGORIA_PRINCIPALE, 1);
		
		vBuilder.setTitle(titoloLista);
		
		if(!isCategoryTypeList){
			vBuilder.setItems(new String[]{"Tutti",  "Attivit� Commerciali", "Punti di interesse" }, 
						new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent tipologia = new Intent();
					tipologia.putExtra("LIST", which);
					tipologia.putExtra("IS_CATEGORY", false);
					getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, tipologia);
				}
			});
		}else{
			Cursor c = getActivity().getContentResolver()
					.query(PuntiContentProvider.SOTTOCATEGORIE_URI, 
							new String[]{SottocategoriaDbHelper.NAME + ""},
							SottocategoriaDbHelper.CATEGORIA_ID + "=?",
							new String[]{ categoriaPrincipale + "" },
							null);
			
			String[] columnNames = {};
			int tmpCursorIndex = 0;
			
			while(c.moveToNext()){
				columnNames[tmpCursorIndex] = c.getString(tmpCursorIndex);
				tmpCursorIndex++;
			}
			
			
			vBuilder.setItems(columnNames,//new String[]{"AAA",  "BBB", "CCC", "DDD" }, 
					new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent tipologia = new Intent();
				tipologia.putExtra("LIST", which);
				tipologia.putExtra("IS_CATEGORY", true);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, tipologia);
			}
		});
		}
		return vBuilder.create();
	}
}
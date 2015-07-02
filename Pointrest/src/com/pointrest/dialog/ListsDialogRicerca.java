package com.pointrest.dialog;

import com.pointrestapp.pointrest.data.CategorieDbHelper;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.SottocategoriaDbHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class ListsDialogRicerca extends DialogFragment {
	private String titoloLista;
	private boolean isSubCategoryTypeList;
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
		isSubCategoryTypeList = getArguments().getBoolean(IS_CATEGORY_TYPE_LIST);
		categoriaPrincipale = getArguments().getInt(CATEGORIA_PRINCIPALE, 1);
		
		vBuilder.setTitle(titoloLista);
		Cursor c = null, cAll = null;
		try{
			cAll = getActivity().getContentResolver()
					.query(PuntiContentProvider.SOTTOCATEGORIE_URI, 
							new String[]{SottocategoriaDbHelper.NAME + "", SottocategoriaDbHelper._ID + ""},
							null,
							null,
							null);
			c = cAll; //added
			
			String[] columnNames = new String[c.getCount()];
			final int[] columnIdNames = new int[c.getCount()];
			int tmpCursorIndex = 0;
			
			if(c.moveToFirst()){
				do{
					columnNames[tmpCursorIndex] = c.getString(0);
					columnIdNames[tmpCursorIndex] = c.getInt(1);
					tmpCursorIndex++;
				}while(c.moveToNext());
			}
			c.close();
			vBuilder.setItems(columnNames,
					new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent tipologia = new Intent();
				tipologia.putExtra("LIST", which);
				tipologia.putExtra("IS_CATEGORY", true);
				tipologia.putExtra("CATEGORY_ID", columnIdNames[which]);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, tipologia);
			}
			
			
		});
		}
		catch(Exception e){
			Log.d("listdialogricerca", "sottocategorie");
		}
		finally {
			c.close();
			cAll.close();
		}
		return vBuilder.create();
	}
}

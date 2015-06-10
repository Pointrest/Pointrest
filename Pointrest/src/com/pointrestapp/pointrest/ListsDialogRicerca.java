package com.pointrestapp.pointrest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class ListsDialogRicerca extends DialogFragment {
	private String titoloLista;
	private boolean isCategoryTypeList;
	public static final String TITOLO_LISTA = "titolo_lista";
	public static final String IS_CATEGORY_TYPE_LIST = "is_category_type_list";
	
	public static ListsDialogRicerca getInstance(int num, String titoloDellaLista, boolean isCategoryTypeList){
		ListsDialogRicerca vItem = new ListsDialogRicerca();
		 Bundle bundle = new Bundle();
		    bundle.putInt("num", num);
		    bundle.putString(TITOLO_LISTA, titoloDellaLista);
		    bundle.putBoolean(IS_CATEGORY_TYPE_LIST, isCategoryTypeList);
		    vItem.setArguments(bundle);
		return vItem;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder vBuilder = new AlertDialog.Builder(getActivity());
		
		titoloLista = getArguments().getString(TITOLO_LISTA);
		isCategoryTypeList = getArguments().getBoolean(IS_CATEGORY_TYPE_LIST);
		
		vBuilder.setTitle(titoloLista);
		
		if(!isCategoryTypeList){
			vBuilder.setItems(new String[]{"Tutti",  "Attività Commerciali", "Punti di interesse" }, 
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
			vBuilder.setItems(new String[]{"AAA",  "BBB", "CCC", "DDD" }, 
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

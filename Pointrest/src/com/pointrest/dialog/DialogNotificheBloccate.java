package com.pointrest.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogNotificheBloccate extends DialogFragment {
	public static DialogNotificheBloccate getInstance(long id){
		DialogNotificheBloccate vItem = new DialogNotificheBloccate(id);
		Bundle bundle = new Bundle();
	    vItem.setArguments(bundle);
		return vItem;
	}
	
	private long tmpID;
	
	private DialogNotificheBloccate(long id){
		tmpID= id;
	}
	
	@Override
	public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		
		//Bundle bundle = getArguments();
		
		builder.setTitle("Ripristino");
		
		
		builder.setMessage("Vuoi ripristinare l'elemento?");
		
		builder.setPositiveButton("Ripristina", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent preferito = new Intent();
				preferito.putExtra("ID", tmpID);
				
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, preferito);				
			}
		}); 
		
		builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, tipologia);
			}
		});
		return builder.create();
	}
}

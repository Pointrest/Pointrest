package com.pointrestapp.pointrest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class NotificheBloccateDialog extends DialogFragment {
	public interface INotificheBloccateDialog {
		public void onRipristina(long id);
		public void onVisualizza(long id);
		public void onAnnulla();
	}
	private long tempID = 0;
	
	private static final String TITLE = "TITLE";
	private INotificheBloccateDialog mListener;
	
	public static NotificheBloccateDialog newInstance(long id){
		NotificheBloccateDialog mDialog = new NotificheBloccateDialog(id);
		return mDialog;
	}
	
	public NotificheBloccateDialog(long id){
		tempID = id;
	}
	
	public static NotificheBloccateDialog newInstance(String title, int id){
		NotificheBloccateDialog mDialog = new NotificheBloccateDialog(id);
		Bundle bundle = new Bundle();
		bundle.putString(TITLE, title);
		mDialog.setArguments(bundle);
		return mDialog;
	}

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof INotificheBloccateDialog){
			mListener = (INotificheBloccateDialog) activity;
		}
		super.onAttach(activity);
	}

	@Override
	public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		
		Bundle bundle = getArguments();
		
		if(bundle != null) {
			builder.setTitle(bundle.getString(TITLE));
		}  else {
			builder.setTitle("Ripristino");
		}
		
		builder.setMessage("Vuoi ripristinare l'elemento?");
		
		builder.setPositiveButton("Ripristina", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mListener != null)
					mListener.onRipristina(tempID);
				
			}
		}); 
		
		builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mListener != null)
					mListener.onAnnulla();	
			}
		});
		
		return builder.create();
	}
}

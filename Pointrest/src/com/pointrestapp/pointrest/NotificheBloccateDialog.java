package com.pointrestapp.pointrest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class NotificheBloccateDialog extends DialogFragment {
	public interface INotificheBloccateDialog {
		public void onRipristina();
		public void onVisualizza();
		public void onAnnulla();
	}
	
	private static final String TITLE = "TITLE";
	private INotificheBloccateDialog mListener;
	
	public static NotificheBloccateDialog newInstance(){
		NotificheBloccateDialog mDialog = new NotificheBloccateDialog();
		return mDialog;
	}
	
	public static NotificheBloccateDialog newInstance(String title){
	NotificheBloccateDialog mDialog = new NotificheBloccateDialog();
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
			builder.setTitle("Notifiche Bloccate");
		}
		
		builder.setMessage("Vuoi ripristinare l'elemento?");
		
		builder.setPositiveButton("Ripristina", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mListener != null)
					mListener.onRipristina();
				
			}
		}); 
		builder.setNeutralButton("Visualizza", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mListener != null)
					mListener.onVisualizza();	
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

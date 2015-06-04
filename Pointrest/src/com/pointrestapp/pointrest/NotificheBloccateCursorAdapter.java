package com.pointrestapp.pointrest;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificheBloccateCursorAdapter extends CursorAdapter {
	public NotificheBloccateCursorAdapter(Context context, Cursor c) {
		super(context, c, 0);
	}
	
	private class ViewHolder{
		public TextView nomePI;
		public ImageView info, rimuovi;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater vInflater = LayoutInflater.from(context);
		View vView = vInflater.inflate(R.layout.element_notifiche_bloccate, null);
		
		ViewHolder vHolder = new ViewHolder();
		vHolder.nomePI = (TextView)vView.findViewById(R.id.nomePI);
		vHolder.info = (ImageView)vView.findViewById(R.id.infoNotificheBloccate);
		vHolder.rimuovi = (ImageView)vView.findViewById(R.id.removeNotificheBloccate);
		
		vView.setTag(vHolder);		
		return vView;	
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int vNameColumnIndex = cursor.getColumnIndex(NotificheBloccateHelper.NAME);

		ViewHolder vHolder = (ViewHolder)view.getTag();
		
		vHolder.nomePI.setText(cursor.getString(vNameColumnIndex));
		
	}

}

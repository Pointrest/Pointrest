package com.pointrestapp.pointrest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
import com.pointrestapp.pointrest.data.PuntiImagesDbHelper;

public class NotificheBloccateCursorAdapter extends CursorAdapter {
	public NotificheBloccateCursorAdapter(Context context, Cursor c) {
		super(context, c, 0);
	}
	
	private class ViewHolder{
		public TextView nomePI;
		public ImageView img;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater vInflater = LayoutInflater.from(context);
		View vView = vInflater.inflate(R.layout.element_preferiti_screen, null);
		
		ViewHolder vHolder = new ViewHolder();
		vHolder.nomePI = (TextView)vView.findViewById(R.id.name_pi);	
		vHolder.img = (ImageView)vView.findViewById(R.id.pi_image);
		vView.setTag(vHolder);
		return vView;	
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int vNameColumnIndex = cursor.getColumnIndex(PuntiDbHelper.NOME);

		ViewHolder vHolder = (ViewHolder)view.getTag();
		
		vHolder.nomePI.setText(cursor.getString(vNameColumnIndex));

		int preferitoIdColumnIndex = cursor.getColumnIndex(PuntiDbHelper._ID);
		
		Cursor c = context.getContentResolver()
				.query(PuntiContentProvider.PUNTI_IMAGES_URI, 
						new String[]{PuntiImagesDbHelper._ID + ""},
						PuntiImagesDbHelper.PUNTO_ID + "=?",
						new String[]{ cursor.getInt(preferitoIdColumnIndex) + "" },
						null);
		
		if(c.moveToFirst()){
			int imgIdOnRemoteDB = c.getInt(0);
			Glide.with(context).load(Constants.BASE_URL + "immagini/" + imgIdOnRemoteDB).placeholder(R.drawable.ic_place_black_36dp).crossFade().into(vHolder.img);
			
			c.close();
		}
		
	}

}

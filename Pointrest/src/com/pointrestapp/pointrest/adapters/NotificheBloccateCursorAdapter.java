package com.pointrestapp.pointrest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.data.PuntiDbHelper;

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
		//int vImgColumnIndex = cursor.getColumnIndex(NotificheBloccateHelper.IMAGE);

		ViewHolder vHolder = (ViewHolder)view.getTag();
		
		vHolder.nomePI.setText(cursor.getString(vNameColumnIndex));
		//byte[] decodedString = Base64.decode(cursor.getString(vImgColumnIndex), Base64.DEFAULT);
		//Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		//vHolder.img.setImageBitmap(decodedByte);
		
	}

}

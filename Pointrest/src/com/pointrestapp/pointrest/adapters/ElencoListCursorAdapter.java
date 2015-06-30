package com.pointrestapp.pointrest.adapters;

import java.nio.channels.FileLockInterruptionException;

import com.bumptech.glide.Glide;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
import com.pointrestapp.pointrest.data.PuntiImagesDbHelper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ElencoListCursorAdapter extends CursorAdapter {

	public ElencoListCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
	}

	private static class ViewHolder {
		public TextView nomePI;
		public ImageView img;
		
		public ViewHolder(View aView) {
			nomePI = (TextView)aView.findViewById(R.id.name_pi);	
			img = (ImageView)aView.findViewById(R.id.pi_image);
		}
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View vView = LayoutInflater.from(context)
				.inflate(R.layout.element_preferiti_screen, parent, false);
		vView.setTag(new ViewHolder(vView));
		return vView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		ViewHolder vHolder = (ViewHolder)view.getTag();	
		
		String namePI = cursor.getString(cursor.getColumnIndex(PuntiDbHelper.NOME));
		vHolder.nomePI.setText(namePI + "");
		
		int preferitoIdColumnIndex = cursor.getColumnIndex(PuntiDbHelper._ID);
		
		Cursor c =null;
		try{
			c = context.getContentResolver()
				.query(PuntiContentProvider.PUNTI_IMAGES_URI, 
						new String[]{PuntiImagesDbHelper._ID + ""},
						PuntiImagesDbHelper.PUNTO_ID + "=?",
						new String[]{ cursor.getInt(preferitoIdColumnIndex) + "" },
						null);
			
			if(c.moveToFirst()){
				int imgIdOnRemoteDB = c.getInt(0);
				Glide.with(context).load(Constants.BASE_URL + "immagini/" + imgIdOnRemoteDB).placeholder(R.drawable.ic_place_black_36dp).crossFade().into(vHolder.img);
			}
		}catch(Exception e ){
			Log.d("elencolistcursoradapter", "cursor bindview");
		}
		finally {
			if(c!=null)
				c.close();
		}
	}

	private int lastPosition = -1;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View mV = super.getView(position, convertView, parent);
		
		Animation animation = AnimationUtils.loadAnimation(mV.getContext(), (position > lastPosition) ? R.animator.up_from_bottom : R.animator.down_from_top);
		mV.startAnimation(animation);
	    lastPosition = position;
	    
		return mV;
	}
}

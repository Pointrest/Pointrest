package com.pointrestapp.pointrest.adapters;

import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.R.id;
import com.pointrestapp.pointrest.R.layout;
import com.pointrestapp.pointrest.data.PreferitoHelper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PreferitiCursorAdapter extends CursorAdapter {

	public PreferitiCursorAdapter(Context context, Cursor c) {
		super(context, c, 0);
	}

	private class ViewHolder{
		public TextView nome_preferito;
		public ImageView foto_preferito;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		
		View view = inflater.inflate(R.layout.element_preferiti_screen, null);
		ViewHolder vholder = new ViewHolder();
		
		vholder.nome_preferito = (TextView) view.findViewById(R.id.name_pi);
		vholder.foto_preferito = (ImageView) view.findViewById(R.id.pi_image);
		
		view.setTag(vholder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int preferitoColumnIndex = cursor.getColumnIndex(PreferitoHelper.PREFERITO);
		ViewHolder vholder = (ViewHolder) view.getTag();
		
		vholder.nome_preferito.setText(cursor.getString(preferitoColumnIndex));
		//SETTARE IMMAGINE!
		//vholder.nome_preferito.setImageResource(R.drawable.my_image);
		
	}
}

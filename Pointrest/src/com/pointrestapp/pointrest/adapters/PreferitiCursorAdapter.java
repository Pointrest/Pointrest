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
		int preferitoNameColumnIndex = cursor.getColumnIndex(PuntiDbHelper.NOME);
		ViewHolder vholder = (ViewHolder) view.getTag();
		
		vholder.nome_preferito.setText(cursor.getString(preferitoNameColumnIndex));
		//SETTARE IMMAGINE!
		//vholder.nome_preferito.setImageResource(R.drawable.my_image);
		
	}
}

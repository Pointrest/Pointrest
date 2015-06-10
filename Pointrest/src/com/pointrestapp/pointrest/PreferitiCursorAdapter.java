package com.pointrestapp.pointrest;

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
		public Button info_p, delete_p, map_p;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		
		View view = inflater.inflate(R.layout.element_preferiti_screen, null);
		ViewHolder vholder = new ViewHolder();
		
		vholder.nome_preferito = (TextView) view.findViewById(R.id.name_pi);
		vholder.foto_preferito = (ImageView) view.findViewById(R.id.pi_image);
		//vholder.info_p = (Button) view.findViewById(R.id.info_pi);
		//vholder.delete_p = (Button) view.findViewById(R.id.delete_pi);
		//vholder.map_p = (Button) view.findViewById(R.id.on_map_pi);
		
		view.setTag(vholder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//int preferitoColumnIndex = cursor.getColumnIndex(PreferitoHelper.PREFERITO);
		ViewHolder vholder = (ViewHolder) view.getTag();
		
		//vholder.nome_preferito.setText(cursor.getString(preferitoColumnIndex));
		//SETTARE IMMAGINE!
		//vholder.nome_preferito.setImageResource(R.drawable.my_image);
		vholder.info_p.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		vholder.delete_p.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		vholder.map_p.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}
}

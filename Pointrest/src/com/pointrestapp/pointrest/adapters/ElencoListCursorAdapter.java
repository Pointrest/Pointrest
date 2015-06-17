package com.pointrestapp.pointrest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.data.PuntiDbHelper;

public class ElencoListCursorAdapter extends CursorAdapter {

	public ElencoListCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
	}

	private static class ViewHolder {
		public TextView testText;
		
		public ViewHolder(View aView) {
			testText = (TextView)aView.findViewById(R.id.txtview_test);
		}
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View vView = LayoutInflater.from(context)
				.inflate(R.layout.temptextlayout, parent, false);
		vView.setTag(new ViewHolder(vView));
		return vView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int vPointTypeIndex = cursor.getColumnIndex(PuntiDbHelper.CATEGORY_ID);
		
		int typeId = cursor.getInt(vPointTypeIndex);

		ViewHolder vHolder = (ViewHolder)view.getTag();	
		
		
		vHolder.testText.setText(typeId + "");
	}
}

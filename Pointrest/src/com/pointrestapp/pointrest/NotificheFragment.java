package com.pointrestapp.pointrest;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class NotificheFragment extends Fragment {
	Switch promo, prossimita;
	ListView lista;
	
	private static final String CHIAVE = "CHIAVE1";	
	private static final int NOTIFICHE_BLOCCATE_LOADER_ID = 0;
	private NotificheBloccateCursorAdapter vCursorAdapter;
	NotificheBloccateCursorAdapter mAdapter;
	long pos;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_notifiche, container, false);
		
		promo = (Switch)v.findViewById(R.id.notifichePromoSwitch);
		prossimita = (Switch)v.findViewById(R.id.notificheProssimitaSwitch);
		lista = (ListView)v.findViewById(R.id.listaNotificheBloccate);
		
		vCursorAdapter = new NotificheBloccateCursorAdapter(getActivity(), null);
		lista.setAdapter(vCursorAdapter);
		
		lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				pos=id;
			}
		});
		
		lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				pos = id;
				
				
				return false;
				
			}
			
		});	
		
		lista.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                
                String item = ((TextView)view).getText().toString();
                
                //Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                
            }
		});
		
		return v;
	}
}

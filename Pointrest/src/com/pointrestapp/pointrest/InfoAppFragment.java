package com.pointrestapp.pointrest;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InfoAppFragment extends Fragment {
	TextView versione, autori, website, email;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.info_app, container, false);
		
		versione = (TextView)v.findViewById(R.id.numeroVersione);
		autori = (TextView)v.findViewById(R.id.autori);
		website = (TextView)v.findViewById(R.id.website);
		email = (TextView)v.findViewById(R.id.contattaci);
		
		website.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.google.com")));
				 return true;
			}
		});
		
		return v;
	}
}

package com.pointrestapp.pointrest.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointrestapp.pointrest.R;

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
				 startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://pointerest.azurewebsites.net")));
				 return true;
			}
		});
		
		email.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
			            "mailto","info.pointrest@gmail.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[info] pointrest");
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
				return false;
			}
		});

		String versionName;
		try {
			versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			//e.printStackTrace();
			versionName = "1.0.2";
		}
		versione.setText(versionName);
		return v;
	}

	public static InfoAppFragment getInstance() {
		// TODO Auto-generated method stub
		return new InfoAppFragment();
	}
}

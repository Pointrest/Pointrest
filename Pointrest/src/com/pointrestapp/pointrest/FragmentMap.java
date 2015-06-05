package com.pointrestapp.pointrest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pointrestapp.pointrest.adapters.TabAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;

public class FragmentMap extends Fragment  implements
		OnMapReadyCallback,
		TabAdapter.Callback {

	private GoogleMap mMap;
	private MapFragment mMapFragment;
	private List<Marker> mMarkers = new ArrayList<Marker>();

	public static FragmentMap getInstance(int aPosition){
		FragmentMap tf = new FragmentMap();
		return tf;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vView = inflater.inflate(R.layout.fragment_map, container, false);
		setUpGui(vView);
		return vView;
	}
	
	private void setUpGui(View vView) {
		mMapFragment = (MapFragment)getChildFragmentManager()
				.findFragmentById(R.id.fragment_map);
		
		mMapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap arg0) {
		mMap = arg0;
		
		mMap.setMyLocationEnabled(true);
		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
				return false;
			}
		});
	}

	@Override
	public void onTabSelected(int puntoType) {
		showMarkersForType(puntoType);
	}

	private void showMarkersForType(int puntoType) {
		mMap.clear();
		Builder vBoundsBuilder = LatLngBounds.builder();
		Cursor cursor = getActivity().getContentResolver().query(PuntiContentProvider.PUNTI_URI,
				null,
				PuntiDbHelper.TYPE + "=?",
				new String[]{ puntoType + "" },
				null);
		int pointNameIndex = cursor.getColumnIndex(PuntiDbHelper.NOME);
		/*int pointLatIndex = cursor.getColumnIndex(PuntiDbHelper.LAT);
		int pointLanIndex = cursor.getColumnIndex(PuntiDbHelper.LAN);
		
		while (cursor.moveToNext()) {
			mMarkers.add(mMap.addMarker(new MarkerOptions()
				.title(cursor.getString(pointNameIndex))
				.position(new LatLng(cursor.getDouble(pointLatIndex),
									 cursor.getDouble(pointLanIndex)))
			));
		} */
		Random r = new Random();
		while (cursor.moveToNext()) {
			double lat = r.nextDouble() + r.nextInt(50);
			double lang = r.nextDouble() + r.nextInt(50);
			LatLng vLatLng = new LatLng(lat, lang);
			vBoundsBuilder.include(vLatLng);
			mMarkers.add(mMap.addMarker(new MarkerOptions()
				.title(cursor.getString(pointNameIndex))
				.position(vLatLng)
			));
		}
		mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(vBoundsBuilder.build(), 1));
	}
}

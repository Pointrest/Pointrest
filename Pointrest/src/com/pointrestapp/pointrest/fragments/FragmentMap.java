package com.pointrestapp.pointrest.fragments;

import java.util.ArrayList;
import java.util.List;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pointrestapp.pointrest.MainActivity;
import com.pointrestapp.pointrest.MyMapView;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.adapters.TabAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
//import android.view.ViewGroup.LayoutParams;

public class FragmentMap extends Fragment  implements
		OnMapReadyCallback,
		TabAdapter.MapCallback,
		ConnectionCallbacks,
		OnConnectionFailedListener {

	private GoogleMap mMap;
	private MyMapView mMapView;
	private Location mCurrentLocation;
	private List<Marker> mMarkers = new ArrayList<Marker>();
	private GoogleApiClient mGoogleApiClient;
	private View mFrameBelow;
	private LinearLayout mLayoutWhole;
	private boolean mFullscreen;
	private MainActivity mHostActivity;
	private int mCurrentTab;

	public static FragmentMap getInstance(int aPosition){
		FragmentMap tf = new FragmentMap();
		return tf;
	}

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof MainActivity)
			mHostActivity = (MainActivity)activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		buildGoogleApiClient();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vView = inflater.inflate(R.layout.fragment_map_frame, container, false);
		//View v2 = inflater.inflate(R.layout.fragment_map, (ViewGroup)vView, false);
		//mMapFragment = (MapFragment)getFragmentManager()
		//		.findFragmentById(R.id.fragment_map);
		//A hack for Android versions where the childFM wasn't available
		//if (mMapFragment == null) {
		//	mMapFragment = (MapFragment)getChildFragmentManager()
		//			.findFragmentById(R.id.fragment_map);
		//}
		//getChildFragmentManager().beginTransaction()
		//	.add(R.id.frame_map_above, mMapFragment)
		//	.commit();
		mMapView = (MyMapView)vView.findViewById(R.id.mapview);
		mFrameBelow = (View)vView.findViewById(R.id.frame_map_below);
		mLayoutWhole = (LinearLayout)vView.findViewById(R.id.linear_tab);
		LayoutTransition lt = new LayoutTransition();
		lt.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 2000);
		lt.setDuration(5000);
//		lt.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 0);
//		lt.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
//		lt.setStagger(LayoutTransition.CHANGE_APPEARING, 0);
		
		
		mLayoutWhole.setLayoutTransition(lt);
		mMapView.onCreate(savedInstanceState);
		/*mMapView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		}); */
		mMapView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println();
			}
		});
		
		//We need this listener to avoid adding LatLngs to a 0 size MapView;
		mMapView.getViewTreeObserver().addOnGlobalLayoutListener(
			    new ViewTreeObserver.OnGlobalLayoutListener() {
			      @Override
			      public void onGlobalLayout() {
			    	  showMarkersForType(mCurrentTab);
			      }
			    });
		return vView;
	}
	
	@Override
	public void onMapReady(GoogleMap arg0) {
		mMap = arg0;
		mMap.setMyLocationEnabled(true);
		mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
		/*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
				return false;
			}
		}); */
	}

	@Override
	public void onTabSelected(int puntoType) {
		showMarkersForType(puntoType);
	}

	private void showMarkersForType(int puntoType) {
		mCurrentTab = puntoType;
		boolean haveAtLeastOnePointToShow = false;
		//Che cazzo posso fare qua?
		if (mMap == null)
			return;
		mMap.clear();
		Builder vBoundsBuilder = LatLngBounds.builder();
		Cursor cursor = getActivity().getContentResolver().query(
				PuntiContentProvider.PUNTI_URI,
				null,
				PuntiDbHelper.TYPE + "=?",
				new String[]{ puntoType + "" },
				null);
		int pointNameIndex = cursor.getColumnIndex(PuntiDbHelper.NOME);
		int pointLatIndex = cursor.getColumnIndex(PuntiDbHelper.LATUTUDE);
		int pointLonIndex = cursor.getColumnIndex(PuntiDbHelper.LONGITUDE);
		
		while (cursor.moveToNext()) {
			haveAtLeastOnePointToShow = true;
			LatLng vLatLng = new LatLng(cursor.getDouble(pointLatIndex),
					 					cursor.getDouble(pointLonIndex));
			vBoundsBuilder.include(vLatLng);
			mMarkers.add(mMap.addMarker(new MarkerOptions()
				.title(cursor.getString(pointNameIndex))
				.position(vLatLng)
			));
		}

		if (haveAtLeastOnePointToShow)
			mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(vBoundsBuilder.build(), 100));
	}
	
	 
		@Override
		public void onResume() {
			mMapView.getMapAsync(this);
			if (mMapView != null)
				mMapView.onResume();
			super.onResume();
		}
	 
		@Override
		public void onDestroy() {
			super.onDestroy();
			if (mMapView != null)
				mMapView.onDestroy();
		}
	 
		@Override
		public void onLowMemory() {
			super.onLowMemory();
			if (mMapView != null)
				mMapView.onLowMemory();
		}
		
		protected synchronized void buildGoogleApiClient() {
		    mGoogleApiClient = new GoogleApiClient.Builder(getActivity(), this, this)
		        .addApi(LocationServices.API)
		        .build();
		}

		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConnected(Bundle arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConnectionSuspended(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void prepareForShow(float x, float y) {
			/* 
			long downTime = SystemClock.uptimeMillis();
			long eventTime = SystemClock.uptimeMillis() + 100;
			// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
			int metaState = 0;
			MotionEvent motionEvent = MotionEvent.obtain(
			    downTime, 
			    eventTime, 
			    MotionEvent.ACTION_UP, 
			    453, 
			    234, 
			    metaState
			);

			mMapView.dispatchTouchEvent(motionEvent); */
/*			int h = mLayoutWhole.getHeight();
			LayoutParamANimation a = new LayoutParamANimation(mMapView, h);
			a.setDuration(500);
			//a.start();
			mMapView.startAnimation(a); */
			/*
			@SuppressWarnings("unused")
			float z = mFrameBelow.getHeight() / 2;
			mLayoutWhole.animate()
			.scaleY(mLayoutWhole.getHeight() + mFrameBelow.getHeight())
			.setListener(new Animator.AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub
					
				}
			})
            //.setDuration(3)
            .setInterpolator(new LinearInterpolator())
            .start();
			//((ViewManager)mFrameBelow.getParent()).removeView(mFrameBelow);
			//mMapView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
*/
			/*
			int h = mLayoutWhole.getHeight();
			Animation ani = new LayoutParamANimation(mMapView, h);
			ani.setDuration(10000);
			mMapView.startAnimation(ani);
			*/
			
			Animation a = new WeightChangeAnimation(mFrameBelow, 0f);
			//Animation b = new WeightChangeAnimation(mFrameBelow, 0f);
			//AnimatorSet s = new AnimatorSet();
			a.setDuration(150);
			//b.setDuration(500);
			mFrameBelow.startAnimation(a);
			mFullscreen = true;
		}
		
		public class WeightChangeAnimation extends Animation {
		    float targetWeight;
		    float startingWeight;
		    View view;

		    public WeightChangeAnimation(View view, float targetWeight) {
		        this.view = view;
		        this.targetWeight = targetWeight;
		        startingWeight = ((android.widget.LinearLayout.LayoutParams) view.getLayoutParams()).weight;
		    }

		    @Override
		    protected void applyTransformation(float interpolatedTime, Transformation t) {
		        ((LinearLayout.LayoutParams)view.getLayoutParams()).weight = startingWeight +  (targetWeight - startingWeight)*interpolatedTime;
		        view.requestLayout();
		    }

		    @Override
		    public void initialize(int width, int height, int parentWidth,
		            int parentHeight) {
		        super.initialize(width, height, parentWidth, parentHeight);
		    }

		    @Override
		    public boolean willChangeBounds() {
		        return true;
		    }
		}
		
		public void onBackPressed() {
			Animation a = new WeightChangeAnimation(mFrameBelow, 20f);
			a.setDuration(150);
			mFrameBelow.startAnimation(a);
		}

}

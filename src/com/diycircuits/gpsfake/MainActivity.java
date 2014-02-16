package com.diycircuits.gpsfake;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.FragmentManager;
import android.app.Fragment;
import android.util.Log;
import android.content.Context;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;

public class MainActivity extends Activity implements OnCameraChangeListener, OnClickListener {

	public static final String TAG = "GPSFake";
	private MarkerOptions mMarker = null;
	private GoogleMap mMap = null;
	private LatLng mInit = null;
	private Settings settings = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		settings = new Settings(getApplicationContext());
		FragmentManager fm = getFragmentManager();
		Fragment frag = fm.findFragmentById(R.id.map);
		if (frag instanceof MapFragment) {
			MapFragment mapf = (MapFragment) frag;
			mMap = (GoogleMap) mapf.getMap();
			mMap.setOnCameraChangeListener(this);
			mMarker = new MarkerOptions();
			mInit = new LatLng(settings.getLat(), settings.getLng());
			mMarker.position(mInit);
			mMarker.draggable(true);

			CameraUpdate cam = CameraUpdateFactory.newLatLng(mInit);
			mMap.moveCamera(cam);
			mMap.addMarker(mMarker);
		}

		Button set   = (Button) findViewById(R.id.set_location);
		set.setOnClickListener(this);
		Button start = (Button) findViewById(R.id.start);
		start.setOnClickListener(this);
		Button sel   = (Button) findViewById(R.id.select_apps);
		sel.setOnClickListener(this);

		start.setText(settings.isStarted() ? getString(R.string.stop) : getString(R.string.start));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onClick(View v) {
		boolean started = settings.isStarted();
	
		if (v.getId() == R.id.set_location) {
			settings.update(mInit.latitude, mInit.longitude, started);
		} else if (v.getId() == R.id.start) {
			Button start = (Button) findViewById(R.id.start);
			started = !started;
			start.setText(started ? getString(R.string.stop) : getString(R.string.start));
			settings.update(mInit.latitude, mInit.longitude, started);
		} else if (v.getId() == R.id.select_apps) {
		}

		if (started) {
			Context context = getApplicationContext();
			CharSequence text = getString(R.string.location_msg) + " " + mInit.latitude + " " + mInit.longitude;
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else {
			Context context = getApplicationContext();
			CharSequence text = getString(R.string.location_msg_stopped);
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onCameraChange(CameraPosition position) {
		LatLng pos = mMarker.getPosition();
		final Projection proj = mMap.getProjection();
		final VisibleRegion vr = proj.getVisibleRegion();
		final LatLng farLeft = vr.farLeft;
		final LatLng farRight = vr.farRight;
		final LatLng nearLeft = vr.nearLeft;
		final LatLng nearRight = vr.nearRight;

		double screenLat = Math.abs(farLeft.latitude - nearRight.latitude) / 2.0;
		double screenLng = Math.abs(farLeft.longitude - nearRight.longitude) / 2.0;

		double latDiff = Math.abs(mInit.latitude - position.target.latitude);
		double lngDiff = Math.abs(mInit.longitude - position.target.longitude);

		if (latDiff > screenLat || lngDiff > screenLng) {
		    double cLat = mMap.getCameraPosition().target.latitude;
			double cLng = mMap.getCameraPosition().target.longitude;
			mInit = new LatLng(cLat, cLng);
			mMarker.position(mInit);
			mMap.clear();
			mMap.addMarker(mMarker);
		}
		
	}

}

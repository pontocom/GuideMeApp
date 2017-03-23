package pt.iscte.daam.guidemeapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap myMap;
    protected double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        latitude = i.getDoubleExtra("latitude", 38.743);
        longitude = i.getDoubleExtra("longitude", -9.195);

        Log.i("GUIDEMEAPP", "Location received LAT = " + latitude + ", LOG = " + longitude);

        MapFragment supportMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        LatLng myPos = new LatLng(latitude, longitude);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        if(myMap!=null) {
            try {
                myMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Log.i("GUIDEMEAPP", "Unable to obtain user location - maybe User refused permission?");
            }

            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15));
        }
    }
}

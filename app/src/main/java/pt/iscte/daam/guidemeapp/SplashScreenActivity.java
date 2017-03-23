package pt.iscte.daam.guidemeapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashScreenActivity extends AppCompatActivity implements LocationListener {

    protected ProgressDialog progressDialog;
    protected LocationManager locationManager;
    protected double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ActivityCompat.requestPermissions(SplashScreenActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Log.i("GUIDEMEAPP", "Waiting for location updates...");
        } catch (SecurityException e) {
            Log.i("GUIDEMEAPP", "Unable to obtain location - maybe User refused permission?");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting location...");
        progressDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.i("GUIDEMEAPP", "Location obtained LAT = " + latitude + ", LOG = " + longitude);

            locationManager.removeUpdates(this);

            progressDialog.dismiss();

            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            i.putExtra("latitude", latitude);
            i.putExtra("longitude", longitude);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

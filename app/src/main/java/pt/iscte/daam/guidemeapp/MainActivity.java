package pt.iscte.daam.guidemeapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap myMap;
    protected double latitude, longitude;

    protected SharedPreferences appdata;
    protected String defaultURL = "http://10.211.55.10:3000";
    protected int mRange;
    protected String mURL;
    public Map <Marker, Integer> hmap;
    protected ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        latitude = i.getDoubleExtra("latitude", 38.743);
        longitude = i.getDoubleExtra("longitude", -9.195);

        dialog = new ProgressDialog(MainActivity.this);

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

            loadAllPOIs();

            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15));

            myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.i("GuideMeApp", "Click on marker with ID = " + hmap.get(marker));
                    Intent i = new Intent(MainActivity.this, POIDetailsActivity.class);
                    i.putExtra("IDPOI", hmap.get(marker));
                    startActivity(i);
                }
            });
        }
    }

    public void loadAllPOIs() {
        appdata = getSharedPreferences("GuideMeAppData",0);
        mRange = appdata.getInt("RANGE", 50);
        mURL = appdata.getString("URL", defaultURL);

        Log.i("GUIDEMEAPP", "RANGE = " + mRange);
        Log.i("GuideMeApp", mURL + "/poi/range/" + latitude + "/" + longitude + "/" + mRange*1000);
        /* get information about all the POIs the REST API */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mURL + "/poi/range/" + latitude + "/" + longitude + "/" + mRange*1000,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("GuideMeApp", response);
                        dialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("status").compareTo("OK") == 0) {
                                JSONArray poi = jsonObject.getJSONArray("poi");
                                hmap = new HashMap<Marker, Integer>();

                                for(int i = 0; i< poi.length(); i++) {
                                    JSONObject jsonpoi = poi.getJSONObject(i);
                                    Log.i("GuideMeApp", "-> " + jsonpoi.getString("name"));

                                    //add Marker to Map
                                    Marker marker = myMap.addMarker(new MarkerOptions()
                                        .title(jsonpoi.getString("name"))
                                        .snippet(jsonpoi.getString("address"))
                                        .position(new LatLng(jsonpoi.getDouble("latt"), jsonpoi.getDouble("logt"))));

                                    hmap.put(marker, jsonpoi.getInt("id"));
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Some error occured while getting POI information!", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            Log.i("GuideMeApp", "Error processing the JSON answer -> " + e.getMessage());
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Some error occured while getting POI information!", Toast.LENGTH_SHORT).show();
                        Log.i("GuideMeApp", error.toString());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        dialog.setMessage("Getting POIs from server...");
        dialog.show();
    }


    public void clickOpenSettings(View view) {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    public void clickRefresh(View v) {
        // delete all POIs from map
        if(hmap != null) {
            if(!hmap.isEmpty()) {
                Log.i("GuideMeApp", "hmap is not empty, so we can delete the POIs");
                for(Map.Entry<Marker,Integer> entry : hmap.entrySet()) {
                    entry.getKey().remove();
                }
                // get and add new POIs
                loadAllPOIs();
            } else {
                Log.i("GuideMeApp", "hmap is empty, so do nothing!!!");
                loadAllPOIs();
            }
        }
    }
}

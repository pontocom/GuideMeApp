package pt.iscte.daam.guidemeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import org.json.JSONObject;

public class POIDetailsActivity extends AppCompatActivity {

    protected TextView tvPOIname;
    protected ImageView ivPOIimage;
    protected TextView tvPOIaddress;
    protected TextView tvPOIdescription;
    protected ProgressBar pbData;

    protected String namePOI, imagePOI, addressPOI, descriptionPOI;
    protected int idPOI;

    protected String defaultURL = "http://10.211.55.10:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poidetails);

        tvPOIname = (TextView) findViewById(R.id.tvPOIname);
        ivPOIimage = (ImageView) findViewById(R.id.iVPOIimage);
        tvPOIaddress = (TextView) findViewById(R.id.tvPOIaddress);
        tvPOIdescription = (TextView) findViewById(R.id.tvPOIdescription);
        pbData = (ProgressBar) findViewById(R.id.pBdata);

        Intent i = getIntent();
        idPOI = i.getIntExtra("IDPOI", 5);

        /* load information from SharedPreferences */
        SharedPreferences appdata = getSharedPreferences("GuideMeAppData",0);
        int mRange = appdata.getInt("RANGE", 50);
        String mURL = appdata.getString("URL", defaultURL);

        /* get information about this specific POI from the REST API */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mURL + "/poi/" + idPOI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("GuideMeApp", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("status").compareTo("OK") == 0) {
                                JSONObject poi = jsonObject.getJSONArray("poi").getJSONObject(0);

                                namePOI = poi.getString("name");
                                imagePOI = poi.getString("image");
                                addressPOI = poi.getString("address");
                                descriptionPOI = poi.getString("description");

                                tvPOIname.setText(namePOI);
                                tvPOIaddress.setText(addressPOI);
                                tvPOIdescription.setText(descriptionPOI);

                                Glide
                                        .with(POIDetailsActivity.this)
                                        .load(imagePOI)
                                        .asBitmap()
                                        .into(new BitmapImageViewTarget(ivPOIimage) {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                super.onResourceReady(resource, glideAnimation);
                                                pbData.setVisibility(ProgressBar.INVISIBLE);
                                            }
                                        });


                            } else {
                                Toast.makeText(POIDetailsActivity.this, "Some error occured while getting POI information!", Toast.LENGTH_SHORT).show();
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
                        pbData.setVisibility(ProgressBar.INVISIBLE);
                        Toast.makeText(POIDetailsActivity.this, "Some error occured while getting POI information!", Toast.LENGTH_SHORT).show();
                        Log.i("GuideMeApp", error.toString());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void clickClose(View v) {
        finish();
    }
}

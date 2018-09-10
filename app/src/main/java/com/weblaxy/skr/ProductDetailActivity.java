package com.weblaxy.skr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.weblaxy.skr.WebService.WebAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class ProductDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView brandName, description, price, priceDiscount, gstAmount, totalAmount;
    LinearLayout fleetWrap;
    ImageView prodImage;
    Button cartButn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cartButn = (Button)findViewById(R.id.cart_butn);
        cartButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        brandName = (TextView) findViewById(R.id.brand_name);
        description = (TextView) findViewById(R.id.description);
        price = (TextView) findViewById(R.id.price);
        priceDiscount = (TextView) findViewById(R.id.price_discount);
        gstAmount = (TextView) findViewById(R.id.gst_amount);
        totalAmount = (TextView) findViewById(R.id.total_amount);
        fleetWrap = (LinearLayout)findViewById(R.id.fleet_wrap);
        prodImage = (ImageView)findViewById(R.id.prod_image);
        callWS();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void callWS(){
        ProgressDialog progressDialog = new ProgressDialog(ProductDetailActivity.this);
        progressDialog.setTitle("Connecting...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (!checkNetwork()) {
            showDialog("No internet connection");
            progressDialog.dismiss();
            return;
        } else {
            try {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String userid = preferences.getString("USER_ID", "");

                JSONObject paramJsonObject = new JSONObject();
                paramJsonObject.put("product_id","63");
                paramJsonObject.put("variant_id","30");

                String paramString = paramJsonObject.toString();
                paramString = URLEncoder.encode(paramString, "utf-8");

                String wsUrl = WebAddress.getProductDetailsUrl()+paramString;

                Log.d("Link: ", ">>> " + wsUrl);
                progressDialog.dismiss();
                parseData(wsUrl);
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }
    }
    public void parseData(String wsUrl) {
        //Showing a progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(ProductDetailActivity.this);
        progressDialog.setTitle("Connecting...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //Creating a json array request
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, wsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response: ", ">>> " + response);
                try {
                    if(response.getString("status").equals("success")){
                        progressDialog.dismiss();
                        updateUI(response);
                    } else {
                        progressDialog.dismiss();
                        showDialog1("Something went wrong!");
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                showDialog1(error.toString());
            }
        });
        // Access the RequestQueue
        Log.d("stringRequest", "" + jsObjRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsObjRequest);
    }
    public void updateUI(JSONObject jsonObj){

        try {

            toolbar.setTitle(jsonObj.getString("name"));
            brandName.setText(jsonObj.getString("brand_name"));
            description.setText(jsonObj.getString("description"));
            price.setText(jsonObj.getString("price"));
            priceDiscount.setText(jsonObj.getString("discount_price"));
            gstAmount.setText(jsonObj.getString("gst"));

            totalAmount.setText(Integer.parseInt(jsonObj.getString("discount_price")) + Integer.parseInt(jsonObj.getString("gst"))+"");

            String photoUrl = jsonObj.getString("cover_img");
            Drawable mDefaultBackground = getResources().getDrawable(R.drawable.ic_launcher_background);
            Glide.with(ProductDetailActivity.this).load(photoUrl).error(mDefaultBackground).into(prodImage);

            for(int i=0; i<jsonObj.getJSONArray("fleets").length(); i++) {
                ImageView fleetItem = new ImageView(this);
                fleetItem.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(60), dpToPx(60)));
                fleetItem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                fleetItem.setAdjustViewBounds(true);

                Glide.with(ProductDetailActivity.this).load(jsonObj.getJSONArray("fleets").get(i)).into(fleetItem);
                fleetWrap.addView(fleetItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private boolean checkNetwork() {
        boolean wifiAvailable = false;
        boolean mobileAvailable = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                wifiAvailable = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                mobileAvailable = true;
            }
        }
        return wifiAvailable || mobileAvailable;
    }
    public void showDialog(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductDetailActivity.this);
                alertDialogBuilder
                        .setTitle(message);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();

                            }
                        })
                        .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }
    public void showDialog1(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductDetailActivity.this);
                alertDialogBuilder
                        .setTitle(message);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    public static int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
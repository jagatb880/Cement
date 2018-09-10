package com.weblaxy.skr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    LinearLayout dataHolder;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        inflater = this.getLayoutInflater();
        dataHolder = (LinearLayout)findViewById(R.id.data_holder);

        callWS();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_change_pw) {
            Intent intent = new Intent(HomeActivity.this, ForgotPWActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void callWS(){
        ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
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
                paramJsonObject.put("user_id", userid);

                String paramString = paramJsonObject.toString();
                paramString = URLEncoder.encode(paramString, "utf-8");

//                String wsUrl = WebAddress.getDoctorProfileDataUrl()+paramString;
                String wsUrl = "http://weblaxy.in/skr/api/rest/getCategoriyList";
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
        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
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
                        updateUI(response.getJSONArray("categories"));
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
    public void updateUI(JSONArray jsonArray){
        try {
            for(int i=0; i<jsonArray.length(); i++) {
                Log.d("::::::::::::::", i+"");
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                final String prodId = jsonObject.getString("id");
                View dataLayout = inflater.inflate(R.layout.content_home_item, dataHolder, false);
                TextView itemName = (TextView) dataLayout.findViewById(R.id.item_title);
                itemName.setText(jsonObject.getString("name"));
                ImageView itemThumb = (ImageView) dataLayout.findViewById(R.id.item_thumb);
                Glide.with(this.getApplicationContext()).load(jsonObject.getString("thumb").toString()).into(itemThumb);

                dataLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), ProductListActivity.class);
                        i.putExtra("PRODUCT_ID", prodId);
                        startActivity(i);
                    }
                });
                dataHolder.addView(dataLayout);
            }
        } catch (JSONException e) {
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
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

}
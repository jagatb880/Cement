package com.weblaxy.skr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class ProfileActivity extends AppCompatActivity{
    LinearLayout prodOne, prodTwo, prodThree, prodFour, prodFive, prodSix;
    EditText fullName, emailAddress, mobileNumber, alternateMobileNumber;
    Button updateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTheProfileData();

        fullName = findViewById(R.id.name_fld);
        emailAddress = findViewById(R.id.email_fld);
        mobileNumber = findViewById(R.id.mob_fld);
        alternateMobileNumber = findViewById(R.id.alt_mob_fld);
        updateBtn = findViewById(R.id.update_btn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForValidation();
            }
        });
    }

    public void checkForValidation(){
        if(fullName.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(),"Please enter the full name",Toast.LENGTH_SHORT).show();
        }else if(emailAddress.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(),"Please enter the email id",Toast.LENGTH_SHORT).show();
        }else if(mobileNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter the mobile number", Toast.LENGTH_SHORT).show();
        }else if(!validateMobile(mobileNumber.getText().toString())){
            Toast.makeText(getApplicationContext(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
        }else{
            updateTheProfile();
        }
    }

    public boolean validateMobile(String mobile) {
        return mobile.length() >= 10;
    }

    public void updateTheProfile(){
        ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle("Connecting...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (!checkNetwork()) {
            showDialog("No internet connection");
            progressDialog.dismiss();
            return;
        } else {
            try {

                JSONObject paramJsonObject = new JSONObject();
                paramJsonObject.put("customer_id","5");
                paramJsonObject.put("name",fullName.getText().toString().trim());
                paramJsonObject.put("email",emailAddress.getText().toString().trim());
                paramJsonObject.put("password","jgpa143");
                paramJsonObject.put("mobile_no",mobileNumber.getText().toString().trim());
                paramJsonObject.put("alt_contact_no",alternateMobileNumber.getText().toString().trim());


                String paramString = paramJsonObject.toString();
                paramString = URLEncoder.encode(paramString, "utf-8");

                String wsUrl = WebAddress.setProfileDetailsUrl()+paramString;

                Log.d("Link: ", ">>> " + wsUrl);
                progressDialog.dismiss();
                networkCallToUpdateProfile(wsUrl);
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }
    }

    public void networkCallToUpdateProfile(String wsUrl){
        //Showing a progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
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
                        Toast.makeText(getApplicationContext(),"Profile successfully updated",Toast.LENGTH_SHORT).show();
                        finish();
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
        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }

    public void setTheProfileData(){
        ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle("Connecting...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (!checkNetwork()) {
            showDialog("No internet connection");
            progressDialog.dismiss();
            return;
        } else {
            try {

                JSONObject paramJsonObject = new JSONObject();
                paramJsonObject.put("customer_id","5");

                String paramString = paramJsonObject.toString();
                paramString = URLEncoder.encode(paramString, "utf-8");

                String wsUrl = WebAddress.getProfileDetailsUrl()+paramString;

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
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle("Connecting...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //Creating a json array request
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, wsUrl, null, new Response.Listener<JSONObject>() {
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
        Log.d("stringRequest", "" + jsonObjRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        jsonObjRequest.setShouldCache(false);
        requestQueue.add(jsonObjRequest);
    }

    public void updateUI(JSONObject jsonObj){

        try {

            fullName.setText(jsonObj.getString("name"));
            emailAddress.setText(jsonObj.getString("email"));
            mobileNumber.setText(jsonObj.getString("mobile_no"));
            alternateMobileNumber.setText(jsonObj.getString("alt_contact_no"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showDialog1(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
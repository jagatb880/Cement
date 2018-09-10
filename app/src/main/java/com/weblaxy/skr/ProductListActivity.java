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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.weblaxy.skr.WebService.WebAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity{

    private ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;
    private TabLayout tabLayout;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cements");
        setSupportActionBar(toolbar);
        toolbar = (Toolbar) findViewById(com.weblaxy.skr.R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        viewPager = (ViewPager) findViewById(com.weblaxy.skr.R.id.viewpager);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        callWS();
    }

    private void setupViewPager(JSONArray bookDataArray) {
        try {

            ProductListActivity.ViewPagerAdapter adapter = new ProductListActivity.ViewPagerAdapter(getSupportFragmentManager());

            for(int i = 0; i< bookDataArray.length(); i++) {
                JSONObject bookJsonObject = new JSONObject(bookDataArray.getJSONObject(i).toString());
                adapter.addFragment(bookJsonObject.toString(), i);
            }
            viewPager.setAdapter(adapter);
            tabLayout = (TabLayout) findViewById(com.weblaxy.skr.R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setScrollPosition(0, 0f, true);
            viewPager.setCurrentItem(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(String jsonObject, int index) throws JSONException {
            Fragment fragment = new ProductListFragment(new JSONObject(jsonObject).getJSONArray("product_units").toString(), index);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(new JSONObject(jsonObject).getString("type_name"));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
        ProgressDialog progressDialog = new ProgressDialog(ProductListActivity.this);
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
                paramJsonObject.put("product_id", 63);

                String paramString = paramJsonObject.toString();
                paramString = URLEncoder.encode(paramString, "utf-8");
//                paramString = "product_id=63";
                String wsUrl = WebAddress.getProductListUrl()+paramString;

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
        final ProgressDialog progressDialog = new ProgressDialog(ProductListActivity.this);
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
    public void updateUI(JSONObject jsonObject){

        try {
            toolbar.setTitle(jsonObject.getString("product_name"));
            setupViewPager(jsonObject.getJSONArray("product_types"));


//            for(int i=0; i<jsonArray.length(); i++) {
//                Log.d("::::::::::::::", i+"");
//                final JSONObject jsonObject = jsonArray.getJSONObject(i);

//                dataLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent i = new Intent(getApplicationContext(), ProductListActivity.class);
//                        i.putExtra("PRODUCT_ID", prodId);
//                        startActivity(i);
//                    }
//                });

//            }


//            String photoUrl = "www.masterdoctor.in/MD/admin/candidate-profile-image/1486414820.png";
//            Drawable mDefaultBackground = getResources().getDrawable(R.drawable.profile_placeholder);
//            Glide.with(HomeActivity.this).load(photoUrl).error(mDefaultBackground).into(profileImage);

//            nameFld.setText(jsonObject.getString("name"));

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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductListActivity.this);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductListActivity.this);
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

package com.weblaxy.skr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.weblaxy.skr.Adapter.OrderedListsAdapter;
import com.weblaxy.skr.ModelClass.OrderedLists;
import com.weblaxy.skr.WebService.WebAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    //Creating a List of OrderedList
    private List<OrderedLists> orderedLists;

    //Creating Views
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initializing Views
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Initializing our jobMatching list
        orderedLists = new ArrayList<>();

        getData();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
//                                        swipeRefreshLayout.setRefreshing(true);
//                                        getData();
                                    }
                                }
        );
    }

    public void getData(){
        ProgressDialog progressDialog = new ProgressDialog(OrdersActivity.this);
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
                paramJsonObject.put("customer_id","4");

                String paramString = paramJsonObject.toString();
                paramString = URLEncoder.encode(paramString, "utf-8");

                String wsUrl = WebAddress.getOrderListUrl()+paramString;

                Log.d("Link: ", ">>> " + wsUrl);
                progressDialog.dismiss();
                networkCallToUpdateOrderList(wsUrl);
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }
    }

    public void networkCallToUpdateOrderList(String wsUrl){
        //Showing a progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(OrdersActivity.this);
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
                        Log.d("response: ", ">>> " + response);
                        try {
                            JSONArray responseArray = response.getJSONArray("orders");
                            updateUI(responseArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    public void updateUI(JSONArray array){
        for(int i = 0; i< array.length(); i++) {
            Log.d("ArraySize",""+array.length());
            OrderedLists orderedList = new OrderedLists();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                orderedList.setOrderId(json.getString("order_id"));
                orderedList.setOrderStatus(json.getString("order_status"));
                orderedList.setTotalAmount(json.getString("total_amount"));
                orderedList.setCreatedDate(json.getString("created_date"));
                orderedList.setDeliveryDate(json.getString("deliver_date"));
                orderedList.setDeliveryAddress(json.getString("address"));
                orderedList.setItems(json.getJSONArray("items"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            orderedLists.add(orderedList);
        }

        //Finally initializing our adapter
        adapter = new OrderedListsAdapter(orderedLists, this);
        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

        // stopping swipe refresh
//        swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrdersActivity.this);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrdersActivity.this);
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
package com.weblaxy.skr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weblaxy.skr.Adapter.OrderedDetailsAdapter;
import com.weblaxy.skr.Adapter.OrderedListsAdapter;
import com.weblaxy.skr.ModelClass.OrderedDetails;
import com.weblaxy.skr.ModelClass.OrderedLists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    //Creating a List of OrderDetails
    private List<OrderedDetails> orderedDetails;

    //Creating Views
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private TextView total, deliveryAddress;
    private float totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button liveTrackButn = (Button) findViewById(R.id.live_track_butn);
        liveTrackButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailsActivity.this, LiveTrackActivity.class);
                startActivity(intent);
            }
        });

        total = findViewById(R.id.total);
        deliveryAddress = findViewById(R.id.address);

        Bundle b = getIntent().getExtras();
        String itemsArray = b.getString("itemsArray");
        String address = b.getString("address");
        Log.d("items array:", "" + itemsArray);

        //Initializing Views
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Initializing our Order list
        orderedDetails = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(itemsArray);
            getData(jsonArray, address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData(JSONArray array, String address) {
        ProgressDialog progressDialog = new ProgressDialog(OrderDetailsActivity.this);
        progressDialog.setTitle("Connecting...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        for (int i = 0; i < array.length(); i++) {
            Log.d("ArraySize", "" + array.length());
            OrderedDetails orderedDetail = new OrderedDetails();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                orderedDetail.setProductId(json.getString("product_id"));
                orderedDetail.setProductName(json.getString("product_name"));
                orderedDetail.setProductType(json.getString("type"));
                orderedDetail.setProductBrand(json.getString("brand"));
                orderedDetail.setProductQuantity(json.getString("quantity"));
                orderedDetail.setProductUnit(json.getString("unit"));
                orderedDetail.setProductPrice(json.getString("price"));
                orderedDetail.setProductImageUrl(json.getString("cover_img"));
                orderedDetail.setProductDescription(json.getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            orderedDetails.add(orderedDetail);
        }

        //Finally initializing our adapter
        adapter = new OrderedDetailsAdapter(orderedDetails, this);
        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

        for (int i=0; i < orderedDetails.size(); i++){
            totalAmount = totalAmount + Float.parseFloat(orderedDetails.get(i).getProductPrice());
        }
        total.setText(""+totalAmount);
        deliveryAddress.setText(address);

        progressDialog.dismiss();
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
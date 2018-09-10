package com.weblaxy.skr.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.weblaxy.skr.LiveTrackActivity;
import com.weblaxy.skr.ModelClass.OrderedLists;
import com.weblaxy.skr.OrderDetailsActivity;
import com.weblaxy.skr.R;

import org.json.JSONArray;

import java.util.List;

public class OrderedListsAdapter extends RecyclerView.Adapter<OrderedListsAdapter.ViewHolder> {

    private Context context;

    //List of orderlists
    List<OrderedLists> orderedLists;

    public OrderedListsAdapter(List<OrderedLists> orderedLists, Context context) {
        super();

        //Getting all the orderlists
        this.orderedLists = orderedLists;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_orders, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        OrderedLists orderedList = orderedLists.get(position);
        Log.d("order size",""+orderedLists.get(position));
        holder.orderId = orderedList.getOrderId();
        holder.orderStatus.setText(orderedList.getOrderStatus());
        holder.totalAmount.setText(orderedList.getTotalAmount());
        if(orderedList.getCreatedDate() !=  null){
            holder.createdDate.setText("Ordered on: "+orderedList.getCreatedDate().substring(0,10));
        }else{
            holder.createdDate.setText("No Ordered Date");
        }
        if(orderedList.getDeliveryDate() !=  null){
            holder.deliveryDate.setText(orderedList.getDeliveryDate().substring(0,10));
        }else{
            holder.deliveryDate.setText("No Delivery Date");
        }

        holder.deliveryAddress.setText(orderedList.getDeliveryAddress());

        if(orderedList.getOrderStatus().equals("Pending")){
            holder.liveStatus.setVisibility(View.VISIBLE);
        }else {
            holder.liveStatus.setVisibility(View.GONE);
        }

        holder.items = orderedList.getItems();
        Log.d("items:",""+holder.items);
    }

    @Override
    public int getItemCount() {
        Log.d("Count",""+orderedLists.size());
        return orderedLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public String orderId;
        public TextView orderStatus;
        public TextView totalAmount;
        public TextView createdDate;
        public TextView deliveryDate;
        public TextView deliveryAddress;
        public Button liveStatus;
        public Button viewDetails;
        public JSONArray items;

        public ViewHolder(View itemView) {
            super(itemView);

            orderStatus = itemView.findViewById(R.id.order_status);
            totalAmount = itemView.findViewById(R.id.total_amount);
            createdDate = itemView.findViewById(R.id.created_date);
            deliveryDate = itemView.findViewById(R.id.delivery_date);
            deliveryAddress = itemView.findViewById(R.id.delivery_address);
            liveStatus = itemView.findViewById(R.id.live_track_butn);
            viewDetails = itemView.findViewById(R.id.view_details_butn1);

            viewDetails.setOnClickListener(this);
            liveStatus.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.view_details_butn1:
                    Intent viewIntent = new Intent(context, OrderDetailsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("itemsArray",items.toString());
                    b.putString("address",deliveryAddress.getText().toString());
                    viewIntent.putExtras(b);
                    context.startActivity(viewIntent);
                    break;
                case R.id.live_track_butn:
                    Intent intent = new Intent(context, LiveTrackActivity.class);
                    context.startActivity(intent);
                    break;
            }
        }
    }
}

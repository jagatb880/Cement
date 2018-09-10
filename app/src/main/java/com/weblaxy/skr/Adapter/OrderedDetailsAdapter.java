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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.weblaxy.skr.CustomVolleyRequest;
import com.weblaxy.skr.LiveTrackActivity;
import com.weblaxy.skr.ModelClass.OrderedDetails;
import com.weblaxy.skr.ModelClass.OrderedLists;
import com.weblaxy.skr.OrderDetailsActivity;
import com.weblaxy.skr.R;

import org.json.JSONArray;

import java.util.List;

public class OrderedDetailsAdapter extends RecyclerView.Adapter<OrderedDetailsAdapter.ViewHolder> {

    private Context context;
    private ImageLoader imageLoader;

    //List of orderlists
    List<OrderedDetails> orderedDetails;

    public OrderedDetailsAdapter(List<OrderedDetails> orderedDetails, Context context) {
        super();

        //Getting all the orderedDetails
        this.orderedDetails = orderedDetails;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_order_details, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        OrderedDetails orderedDetail = orderedDetails.get(position);

        holder.productId = orderedDetail.getProductId();
        holder.productName.setText(orderedDetail.getProductName());
        holder.productType.setText(orderedDetail.getProductType());
        holder.productBrand.setText(orderedDetail.getProductBrand());
        holder.productQuantityUnit.setText(orderedDetail.getProductQuantity()+" : "+orderedDetail.getProductUnit());
        holder.productPrice.setText(orderedDetail.getProductPrice());

        imageLoader =CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(orderedDetail.getProductImageUrl(), ImageLoader.getImageListener(holder.productImage, R.drawable.ic_launcher_background, android.R.drawable.ic_dialog_alert));

        holder.productImage.setImageUrl(orderedDetail.getProductImageUrl(), imageLoader);

    }

    @Override
    public int getItemCount() {
        return orderedDetails.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public String productId;
        public TextView productName;
        public TextView productType;
        public TextView productBrand;
        public TextView productQuantityUnit;
        public TextView productPrice;
        public NetworkImageView productImage;

        public ViewHolder(View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productType = itemView.findViewById(R.id.product_type);
            productBrand = itemView.findViewById(R.id.product_brand);
            productQuantityUnit = itemView.findViewById(R.id.product_quantity_unit);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = (NetworkImageView)itemView.findViewById(R.id.product_image);
        }

    }
}

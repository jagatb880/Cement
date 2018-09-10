package com.weblaxy.skr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("ValidFragment")
public class ProductListFragment extends Fragment {
    LinearLayout prodUnitWrap;
    JSONArray dataObj;

    public ProductListFragment(String bookJsonArray, int index) throws JSONException {
        dataObj = new JSONArray(bookJsonArray);
    }

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//       if (getArguments() != null) {
//           try {
//               dataObj = new JSONArray(getArguments().getString("product_units"));

//               Log.d("XXXXXXXXXX", getArguments().getString("product_units"));
//           } catch (JSONException e) {
//               e.printStackTrace();
//           }
//       }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(com.weblaxy.skr.R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        prodUnitWrap = (LinearLayout) view.findViewById(R.id.prod_unit_wrap);


        for(int i = 0; i < dataObj.length(); i++){
            try {

                JSONObject unitData = dataObj.getJSONObject(i);
                Log.d("XXXXXXXXXXXX", unitData.toString());

                TextView textView = new TextView(this.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,0, dpToPx(10),0);
                textView.setLayoutParams(lp);
                textView.setMinWidth(dpToPx(75));
                textView.setBackgroundResource(R.drawable.butn_prim);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setGravity(Gravity.CENTER);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Small);
                } else {
                    textView.setTextAppearance(android.R.style.TextAppearance_Small);
                }

                textView.setText(unitData.getString("unit_name"));
                prodUnitWrap.addView(textView);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        CardView prodItem1 = (CardView) view.findViewById(R.id.prod_item1);
        prodItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ProductDetailActivity.class);
            startActivity(intent);
            }
        });

    }

    public static int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
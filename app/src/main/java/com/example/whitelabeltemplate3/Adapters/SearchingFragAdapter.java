package com.example.whitelabeltemplate3.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.whitelabeltemplate3.Activities.HomePageActivity;
import com.example.whitelabeltemplate3.Activities.SingleProductDetailsActivity;
import com.example.whitelabeltemplate3.Models.ProductDetailsModel;
import com.example.whitelabeltemplate3.R;
import com.example.whitelabeltemplate3.Utils.Constant;
import com.example.whitelabeltemplate3.Utils.MySingleton;
import com.example.whitelabeltemplate3.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchingFragAdapter extends RecyclerView.Adapter<SearchingFragAdapter.ViewHolder> {
    ArrayList<ProductDetailsModel> productArrayList;
    Context context;
    SpannableStringBuilder spannableText;
    SessionManager sessionManager;
    String authToken;
    public SearchingFragAdapter(ArrayList<ProductDetailsModel> productArrayList, Context context) {
        this.productArrayList = productArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchingFragAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_searching_recycler_item,parent,false);
        this.sessionManager = new SessionManager(context);
        authToken = sessionManager.getUserData().get("authToken");
        // Set 50% screen width manually
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = screenWidth / 2;
        view.setLayoutParams(layoutParams);
        return new SearchingFragAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchingFragAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.productName.setText(productArrayList.get(position).getProductTitle());

        Glide.with(context).load(productArrayList.get(position).getProductImagesModelsArrList().get(0).getProductImage()).into(holder.productImg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleProductDetailsActivity.class);
                intent.putExtra("productId",productArrayList.get(position).getProductId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView productImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productNameTxt);
            productImg = itemView.findViewById(R.id.productImg);

        }
    }
}
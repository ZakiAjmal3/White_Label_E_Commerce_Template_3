package com.example.whitelabeltemplate3.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whitelabeltemplate3.Activities.HomePageActivity;
import com.example.whitelabeltemplate3.Models.SingleOrderPageItemsModel;
import com.example.whitelabeltemplate3.R;

import java.util.ArrayList;

public class SingleOrderPageItemsAdapter extends RecyclerView.Adapter<SingleOrderPageItemsAdapter.ViewHolder> {
    ArrayList<SingleOrderPageItemsModel> productDetailsList;
    Context context;
    public SingleOrderPageItemsAdapter(ArrayList<SingleOrderPageItemsModel> productDetailsList, Context context) {
        this.productDetailsList = productDetailsList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_orders_items_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.productTitle.setText(productDetailsList.get(position).getProductTitle());
        holder.productPrice.setText("₹ " + productDetailsList.get(position).getProductPrice());
        if (!productDetailsList.get(position).getProductImgUrl().isEmpty()) {
            Glide.with(context).load(productDetailsList.get(position).getProductImgUrl()).into(holder.productImg);
        }else {
            Glide.with(context).load(R.drawable.no_image);
        }
        holder.buyItAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HomePageActivity.class);
                intent.putExtra("LoadCartFrag",true);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productDetailsList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productPrice;
        Button buyItAgain;
        ImageView productImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.productTitle);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImg = itemView.findViewById(R.id.productImg);
            buyItAgain = itemView.findViewById(R.id.buyItAgain);
        }
    }
}

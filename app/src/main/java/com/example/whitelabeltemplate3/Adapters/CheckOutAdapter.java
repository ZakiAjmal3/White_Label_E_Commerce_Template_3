package com.example.whitelabeltemplate3.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whitelabeltemplate3.Activities.CheckOutOrderActivity;
import com.example.whitelabeltemplate3.Activities.HomePageActivity;
import com.example.whitelabeltemplate3.Models.CheckOutModel;
import com.example.whitelabeltemplate3.R;

import java.util.ArrayList;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder> {
    ArrayList<CheckOutModel> productDetailsList;
    Context context;
    public CheckOutAdapter(ArrayList<CheckOutModel> productDetailsList, Context context) {
        this.productDetailsList = productDetailsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_items_layout,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.productTitleTxt.setText(productDetailsList.get(position).getProductTitle());
        holder.productPriceTxt.setText("₹ " +productDetailsList.get(position).getProductPrice());
        if (!productDetailsList.get(position).getProductImg().isEmpty()) {
            Glide.with(context).load(productDetailsList.get(position).getProductImg()).into(holder.productImg);
        }else {
            Glide.with(context).load(R.drawable.no_image);
        }

        holder.editLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HomePageActivity.class);
                intent.putExtra("LoadCartFrag",true);
                context.startActivity(intent);
            }
        });
        holder.deleteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDetailsList.remove(position);
                notifyDataSetChanged();
                ((CheckOutOrderActivity) context).setOrderSummaryDetails();
                ((CheckOutOrderActivity) context).checkOutItemArraySize();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productDetailsList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productTitleTxt, productPriceTxt;
        ImageView productImg;
        LinearLayout editLL, deleteLL;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitleTxt = itemView.findViewById(R.id.productTitleTxt);
            productPriceTxt = itemView.findViewById(R.id.productPriceTxt);
            productImg = itemView.findViewById(R.id.productImg);
            editLL = itemView.findViewById(R.id.editLL);
            deleteLL = itemView.findViewById(R.id.deleteLL);
        }
    }
}

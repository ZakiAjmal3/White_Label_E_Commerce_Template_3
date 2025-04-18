package com.example.whitelabeltemplate3.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whitelabeltemplate3.Activities.OrderSingleViewActivity;
import com.example.whitelabeltemplate3.Models.MyOrderModel;
import com.example.whitelabeltemplate3.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {
    ArrayList<MyOrderModel> productDetailsList;
    Context context;
    public MyOrdersAdapter(ArrayList<MyOrderModel> productDetailsList, Context context) {
        this.productDetailsList = productDetailsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_item_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.orderStatusTxt.setText(productDetailsList.get(position).getOrderStatus());

        String orderStatus = productDetailsList.get(position).getOrderStatus();
        if (orderStatus.equalsIgnoreCase("Pending")){
            holder.orderStatusTxt.setTextColor(context.getResources().getColor(R.color.red_orange));
        }
        else if (orderStatus.equalsIgnoreCase("Confirmed") || orderStatus.equalsIgnoreCase("Paid")|| orderStatus.equalsIgnoreCase("Shipped") || orderStatus.equalsIgnoreCase("Delivered")) {
            holder.orderStatusTxt.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if (orderStatus.equalsIgnoreCase("Failed") || orderStatus.equalsIgnoreCase("Cancelled")) {
            holder.orderStatusTxt.setTextColor(context.getResources().getColor(R.color.red));
        }

        String orderDate = productDetailsList.get(position).getOrderDate();
        String formattedDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        try {
            Date date = simpleDateFormat.parse(orderDate); // Parse timestamp
            formattedDate = outputFormat.format(date); // Convert to required format
            System.out.println(formattedDate); // Output: March 27, 2025
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.orderDateTxt.setText(formattedDate);
        holder.orderIdTxt.setText("Order id: " + productDetailsList.get(position).getOrderId());
        holder.orderProductTitle.setText(productDetailsList.get(position).getProductTitle());
        holder.orderPrice.setText("₹ " + productDetailsList.get(position).getFinalAmount());

        Glide.with(context)
                .load(productDetailsList.get(position).getImagesModelArrayList().get(0).getProductImage())
                .error(R.drawable.no_image)
                .into(holder.productImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderSingleViewActivity.class);
//                    intent.putExtra("orderStatus", productDetailsList.get(getAdapterPosition()).getOrderStatus());
                intent.putExtra("orderDate", holder.orderDateTxt.getText().toString());
                intent.putExtra("orderId", productDetailsList.get(position).getOrderId());
//                    intent.putExtra("orderProductTitle", productDetailsList.get(getAdapterPosition()).getProductTitle());
//                    intent.putExtra("orderPrice", productDetailsList.get(getAdapterPosition()).getFinalAmount());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productDetailsList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderStatusTxt, orderDateTxt, orderIdTxt, orderProductTitle, orderPrice;
        ImageView productImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderStatusTxt = itemView.findViewById(R.id.orderStatusTxt);
            orderDateTxt = itemView.findViewById(R.id.orderDateTxt);
            orderIdTxt = itemView.findViewById(R.id.orderIdTxt);
            orderProductTitle = itemView.findViewById(R.id.orderProductTitle);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            productImg = itemView.findViewById(R.id.productImg);

        }
    }
}

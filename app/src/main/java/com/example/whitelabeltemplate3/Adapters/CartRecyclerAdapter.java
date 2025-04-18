package com.example.whitelabeltemplate3.Adapters;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitelabeltemplate3.Models.HomepageRecyclerModel;
import com.example.whitelabeltemplate3.R;

import java.util.ArrayList;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.ViewHolder> {
    ArrayList<HomepageRecyclerModel> dressStyleModels;
    Fragment context;
    int quantity = 1;
    public CartRecyclerAdapter(ArrayList<HomepageRecyclerModel> dressStyleModels, Fragment context) {
        this.dressStyleModels = dressStyleModels;
        this.context = context;
    }

    @NonNull
    @Override
    public CartRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_recycler_item_layout, parent, false);
        return new CartRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.productTitleTxt.setText(dressStyleModels.get(position).getProductName());
        holder.productTitleTxt.setEllipsize(TextUtils.TruncateAt.END);
        holder.productTitleTxt.setMaxLines(1);
//        holder.productSizeTxt.setText(productDetailsList.get(position).getProductSize());
        holder.productSizeTxt.setText("Large");
//        holder.productColorTxt.setText(productDetailsList.get(position).getProductColor());
        holder.productColorTxt.setText("Black");
        holder.productQuantityTxt.setText("1");

        holder.productImg.setImageResource(dressStyleModels.get(position).getProductImg());

        holder.productPriceTxt.setText("₹" + dressStyleModels.get(position).getProductPrice());

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progressBarDialog = new Dialog(context.getContext());
//                progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                progressBarDialog.setContentView(R.layout.progress_bar_dialog);
//                progressBarDialog.setCancelable(false);
//                progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
//                progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
//                progressBarDialog.show();
//                if (sessionManager.isLoggedIn()) {
//                    deleteItem(position);
//                }else {
//                    sessionManager.removeCartItem(productDetailsList.get(position).getProductId());
                    dressStyleModels.remove(position);
                    notifyDataSetChanged();
//                    ((CartItemFragment) context).setOrderSummaryDetails();
//                    ((CartItemFragment) context).checkCartItemArraySize();
//                    setCartCount();
//                    progressBarDialog.dismiss();
//                }
            }
        });
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt("1");
                quantity++;
//                updateProductQuantity(position, quantity);
//                productDetailsList.get(position).setProductQuantity(String.valueOf(quantity));
                holder.productQuantityTxt.setText(String.valueOf(quantity));
//                ((CartItemFragment) context).setOrderSummaryDetails();
            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt("1");
                if (quantity > 1) {
                    quantity--;
//                    updateProductQuantity(position, quantity);
//                    productDetailsList.get(position).setProductQuantity(String.valueOf(quantity));
                    holder.productQuantityTxt.setText(String.valueOf(quantity));
//                    ((CartItemFragment) context).setOrderSummaryDetails();
                }
            }
        });

//        if (!dressStyleModels.get(position).getProductImg().isEmpty()) {
//            Glide.with(context).load(dressStyleModels.get(position).getProductImagesModelsArrList().get(0).getProductImage()).into(holder.productImg);
//        }else {
//            Glide.with(context).load(R.drawable.no_image);
//        }

    }

    @Override
    public int getItemCount() {
        return dressStyleModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productTitleTxt, productPriceTxt, productSizeTxt, productColorTxt, productQuantityTxt;
        ImageView productImg,deleteBtn,plus,minus;;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productTitleTxt = itemView.findViewById(R.id.productTitleTxt);
            productPriceTxt = itemView.findViewById(R.id.productPriceTxt);
            productSizeTxt = itemView.findViewById(R.id.sizeTxt);
            productColorTxt = itemView.findViewById(R.id.colorTxt);
            productQuantityTxt = itemView.findViewById(R.id.quantityDisplayTxt);
            plus = itemView.findViewById(R.id.quantityPlusTxt);
            minus = itemView.findViewById(R.id.quantityMinusTxt);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            productImg = itemView.findViewById(R.id.productImg);

        }
    }
}
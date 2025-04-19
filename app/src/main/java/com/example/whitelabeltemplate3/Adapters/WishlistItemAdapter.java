package com.example.whitelabeltemplate3.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.whitelabeltemplate3.Activities.HomePageActivity;
import com.example.whitelabeltemplate3.Activities.SingleProductDetailsActivity;
import com.example.whitelabeltemplate3.Fragment.WishListFragment;
import com.example.whitelabeltemplate3.Models.ProductDetailsModel;
import com.example.whitelabeltemplate3.R;
import com.example.whitelabeltemplate3.Utils.Constant;
import com.example.whitelabeltemplate3.Utils.MySingletonFragment;
import com.example.whitelabeltemplate3.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WishlistItemAdapter extends RecyclerView.Adapter<WishlistItemAdapter.ViewHolder> {
    ArrayList<ProductDetailsModel> productArrayList;
    Fragment context;
    SpannableStringBuilder spannableText;
    SessionManager sessionManager;
    String authToken;
    Dialog progressBarDialog;
    public WishlistItemAdapter(ArrayList<ProductDetailsModel> productArrayList, Fragment context) {
        this.productArrayList = productArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public WishlistItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);
        this.sessionManager = new SessionManager(context.getContext());
        authToken = sessionManager.getUserData().get("authToken");
        return new WishlistItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.productName.setText(productArrayList.get(position).getProductTitle());
        holder.productName.setEllipsize(TextUtils.TruncateAt.END);
        holder.productName.setMaxLines(2);

        if (!productArrayList.get(position).getDiscountAmount().equals("0")) {
            String originalPrice = productArrayList.get(position).getProductMRP();
            String sellingPrice = productArrayList.get(position).getProductPrice();
            int disAmount = (Integer.parseInt(originalPrice) - Integer.parseInt(sellingPrice));

            // Create a SpannableString for the original price with strikethrough
            SpannableString spannableOriginalPrice = new SpannableString("₹" + originalPrice);
            spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);

            // Combine selling price + original price
            spannableText = new SpannableStringBuilder();
            spannableText.append("₹").append(sellingPrice).append(" ");
//            spannableText.append(spannableOriginalPrice);

            holder.productPriceStrikeThroughTxt.setText(spannableOriginalPrice);

            // Set combined text to productPrice
            holder.productPriceTxt.setText(spannableText);

            // Set discount % separately to productDiscount with green color
            String discountText = "(Save ₹" + disAmount + ")";
            holder.productDiscount.setText(discountText);
            holder.productDiscount.setVisibility(View.VISIBLE);
        } else {
            // No discount, just show the selling price
            holder.productPriceTxt.setText("₹" + productArrayList.get(position).getProductPrice());
            holder.productDiscount.setVisibility(View.GONE);
            holder.productPriceStrikeThroughTxt.setVisibility(View.GONE);
        }


        Glide.with(context).load(productArrayList.get(position).getProductImagesModelsArrList().get(0).getProductImage()).into(holder.productImg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getContext(), SingleProductDetailsActivity.class);
                intent.putExtra("productId",productArrayList.get(position).getProductId());
                context.startActivity(intent);
            }
        });
        holder.moveToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarDialog = new Dialog(context.getContext());
                progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressBarDialog.setContentView(R.layout.progress_bar_dialog);
                progressBarDialog.setCancelable(false);
                progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressBarDialog.show();
                removeFromWishList(position);
            }
        });
//        int wishlistState = productArrayList.get(position).getWishListImgToggle();
//        if (wishlistState == 1){
//            holder.wishListImg.setImageResource(R.drawable.ic_heart_red);
//            Log.e("WishList","WishListTrue");
//        }else {
//            holder.wishListImg.setImageResource(R.drawable.ic_heart_grey);
//            Log.e("WishList","WishListFalse");
//        }
//        holder.wishListImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.wishListImg.animate()
//                        .scaleX(1.2f)
//                        .scaleY(1.2f)
//                        .setDuration(150)
//                        .withEndAction(() -> {
//                            holder.wishListImg.animate().scaleX(1f).scaleY(1f).start();
//                        }).start();
//                setWishlistCount();
//                int state;
//                state = productArrayList.get(position).getWishListImgToggle();
//                if (sessionManager.isLoggedIn()) {
//                    if (state == 0) {
//                        addToWishList(position);
//                        holder.wishListImg.setImageResource(R.drawable.ic_heart_red);
//                        productArrayList.get(position).setWishListImgToggle(1);
//                    } else {
//                        removeFromWishList(position);
//                        holder.wishListImg.setImageResource(R.drawable.ic_heart_grey);
//                        productArrayList.get(position).setWishListImgToggle(0);
//                    }
//                }else {
//                    if (state == 0) {
//                        sessionManager.saveWishList(productArrayList.get(position));
//                        holder.wishListImg.setImageResource(R.drawable.ic_heart_red);
//                        productArrayList.get(position).setWishListImgToggle(1);
//                        Toast.makeText(context.getContext(), "Item added to WishList", Toast.LENGTH_SHORT).show();
//                    } else {
//                        sessionManager.removeWishListItem(productArrayList.get(position).getProductId());
//                        holder.wishListImg.setImageResource(R.drawable.ic_heart_grey);
//                        productArrayList.get(position).setWishListImgToggle(0);
//                        Toast.makeText(context.getContext(), "Item removed from WishList", Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//            }
//        });
    }
    private void setWishlistCount() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HomePageActivity activity = (HomePageActivity) context.getContext();
                activity.setWishlistCount();
            }
        }, 1500);  // Match the duration of the logo animation
    }

    private void removeFromWishList(int position) {
        String orderURL = Constant.BASE_URL + "wishlist/remove/" + productArrayList.get(position).getProductId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, orderURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        addToCart(position);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBarDialog.dismiss();
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("ExamListError", errorMessage);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        MySingletonFragment.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    private void addToCart(int position) {
        String cartURL = Constant.BASE_URL + "cart";
        String productIdStr = productArrayList.get(position).getProductId();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId", productIdStr);
            jsonObject.put("quantity", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, cartURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context.getContext(), "Item added to Cart", Toast.LENGTH_SHORT).show();
                        sessionManager.removeWishListItem(productArrayList.get(position).getProductId());
                        productArrayList.remove(position);
                        ((WishListFragment) context).checkWishListItemArraySize();
                        sessionManager.getWishlistFromServer();
                        setWishlistCount();
                        notifyDataSetChanged();
                        progressBarDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBarDialog.dismiss();
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
                                Toast.makeText(context.getContext(), message, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("ExamListError", errorMessage);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        MySingletonFragment.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPriceTxt,productDiscount,productPriceStrikeThroughTxt;
        ImageView productImg;
        CardView moveToCartBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productNameTxt);
            productPriceTxt = itemView.findViewById(R.id.productPriceTxt);
            productDiscount = itemView.findViewById(R.id.productDiscountTxt);
            productPriceStrikeThroughTxt = itemView.findViewById(R.id.productPriceStrikeThroughTxt);
            productImg = itemView.findViewById(R.id.productImg);
            moveToCartBtn = itemView.findViewById(R.id.moveToCartCard);

            if (sessionManager.isLoggedIn()){
                moveToCartBtn.setVisibility(View.VISIBLE);
            }else {
                moveToCartBtn.setVisibility(View.GONE);
            }
        }
    }
}
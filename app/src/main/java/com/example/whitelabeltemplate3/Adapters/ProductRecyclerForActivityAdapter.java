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

public class ProductRecyclerForActivityAdapter extends RecyclerView.Adapter<ProductRecyclerForActivityAdapter.ViewHolder> {
    ArrayList<ProductDetailsModel> productArrayList;
    Context context;
    SpannableStringBuilder spannableText;
    SessionManager sessionManager;
    String authToken;
    public ProductRecyclerForActivityAdapter(ArrayList<ProductDetailsModel> productArrayList, Context context) {
        this.productArrayList = productArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductRecyclerForActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_for_search_item_layout,parent,false);
        this.sessionManager = new SessionManager(context);
        authToken = sessionManager.getUserData().get("authToken");
        // Set 50% screen width manually
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = screenWidth / 2;
        view.setLayoutParams(layoutParams);
        return new ProductRecyclerForActivityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerForActivityAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.productName.setText(productArrayList.get(position).getProductTitle());

        if (!productArrayList.get(position).getDiscountAmount().equals("0")) {
            String originalPrice, disPercent, sellingPrice;
            originalPrice = productArrayList.get(position).getProductMRP();
            disPercent = productArrayList.get(position).getDiscountPercentage();
            sellingPrice = productArrayList.get(position).getProductPrice();

            // Create a SpannableString for the original price with strikethrough
            SpannableString spannableOriginalPrice = new SpannableString("₹" + originalPrice);
            spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);
            // Create the discount text
            String discountText = "(-" + disPercent + "%)";
            spannableText = new SpannableStringBuilder();
            spannableText.append("₹" + sellingPrice + " ");
            spannableText.append(spannableOriginalPrice);
            spannableText.append(" " + discountText);
            // Set the color for the discount percentage
            int startIndex = spannableText.length() - discountText.length();
            spannableText.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.productPrice.setText(spannableText);
        }else {
            holder.productPrice.setText("₹" + productArrayList.get(position).getProductPrice());
        }

        Glide.with(context).load(productArrayList.get(position).getProductImagesModelsArrList().get(0).getProductImage()).into(holder.productImg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleProductDetailsActivity.class);
                intent.putExtra("productId",productArrayList.get(position).getProductId());
                context.startActivity(intent);
            }
        });
//        ArrayList<WishListModel> wishListModels = sessionManager.getWishList();
//        for (int i = 0; i < wishListModels.size(); i++) {
//            if (wishListModels.get(i).getProductId().equals(productArrayList.get(position).getProductId())) {
//                holder.wishListImg.setImageResource(R.drawable.ic_heart_red);
//                productArrayList.get(position).setWishlistToggle(1);
//                break;
//            } else {
//                holder.wishListImg.setImageResource(R.drawable.ic_heart_grey);
//                productArrayList.get(position).setWishlistToggle(0);
//            }
//        }
        int wishlistState = productArrayList.get(position).getWishListImgToggle();
        if (wishlistState == 1){
            holder.wishListImg.setImageResource(R.drawable.ic_heart_red);
        }else {
            holder.wishListImg.setImageResource(R.drawable.ic_heart_grey);
        }
        holder.wishListImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWishlistCount();
                int state;
                state = productArrayList.get(position).getWishListImgToggle();
                if (sessionManager.isLoggedIn()) {
                    if (state == 0) {
                        addToWishList(position);
                        holder.wishListImg.setImageResource(R.drawable.ic_heart_red);
                        productArrayList.get(position).setWishListImgToggle(1);
                    } else {
                        removeFromWishList(position);
                        holder.wishListImg.setImageResource(R.drawable.ic_heart_grey);
                        productArrayList.get(position).setWishListImgToggle(0);
                    }
                }else {
                    if (state == 0) {
                        sessionManager.saveWishList(productArrayList.get(position));
                        holder.wishListImg.setImageResource(R.drawable.ic_heart_red);
                        productArrayList.get(position).setWishListImgToggle(1);
                        Toast.makeText(context, "Item added to WishList", Toast.LENGTH_SHORT).show();
                    } else {
                        sessionManager.removeWishListItem(productArrayList.get(position).getProductId());
                        holder.wishListImg.setImageResource(R.drawable.ic_heart_grey);
                        productArrayList.get(position).setWishListImgToggle(0);
                        Toast.makeText(context, "Item removed from WishList", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

    }
    private void setWishlistCount() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HomePageActivity activity = (HomePageActivity) context;
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
                        Toast.makeText(context, "Item removed from wishlist", Toast.LENGTH_SHORT).show();
                        sessionManager.removeWishListItem(productArrayList.get(position).getProductId());
                        sessionManager.getWishlistFromServer();
                        notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void addToWishList(int position) {
        String orderURL = Constant.BASE_URL + "wishlist";
        String productId = productArrayList.get(position).getProductId();
        String userId = sessionManager.getUserData().get("userId");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId", productId);
            jsonObject.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, orderURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Item added to wishlist", Toast.LENGTH_SHORT).show();
                        sessionManager.getWishlistFromServer();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName,productPrice;
        ImageView productImg,wishListImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productNameTxt);
            productPrice = itemView.findViewById(R.id.productPriceTxt);
            productImg = itemView.findViewById(R.id.productImg);
            wishListImg = itemView.findViewById(R.id.wishlistImg);

        }
    }
}
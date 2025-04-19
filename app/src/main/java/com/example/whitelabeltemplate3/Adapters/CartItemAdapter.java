package com.example.whitelabeltemplate3.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.example.whitelabeltemplate3.Fragment.CartItemFragment;
import com.example.whitelabeltemplate3.Models.CartItemModel;
import com.example.whitelabeltemplate3.R;
import com.example.whitelabeltemplate3.Utils.Constant;
import com.example.whitelabeltemplate3.Utils.MySingletonFragment;
import com.example.whitelabeltemplate3.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    ArrayList<CartItemModel> productDetailsList;
    Fragment context;
    int quantity = 1;
    SpannableStringBuilder spannableText;
    SessionManager sessionManager;
    String authToken;
    Dialog progressBarDialog;
    boolean moveToWishlistCardBoolean = false;
    public CartItemAdapter(ArrayList<CartItemModel> productDetailsList, Fragment context) {
        this.productDetailsList = productDetailsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_recycler_item_layout,parent,false);
        this.sessionManager = new SessionManager(context.getActivity());
        authToken = sessionManager.getUserData().get("authToken");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.productTitleTxt.setText(productDetailsList.get(position).getProductTitle());
        holder.productTitleTxt.setEllipsize(TextUtils.TruncateAt.END);
        holder.productTitleTxt.setMaxLines(2);
        holder.productSizeTxt.setText("Large");
        holder.productColorTxt.setText("Black");
        holder.productQuantityTxt.setText(productDetailsList.get(position).getProductQuantity());

        if (!productDetailsList.get(position).getProductImagesModelsArrList().isEmpty()) {
            Glide.with(context).load(productDetailsList.get(position).getProductImagesModelsArrList().get(0).getProductImage()).into(holder.productImg);
        }else {
            Glide.with(context).load(R.drawable.no_image);
        }
        if (!productDetailsList.get(position).getDiscountAmount().equals("0")) {
            String originalPrice, sellingPrice;
            originalPrice = productDetailsList.get(position).getProductMRP();
            sellingPrice = productDetailsList.get(position).getProductPrice();
            int disAmount = (Integer.parseInt(originalPrice) - Integer.parseInt(sellingPrice));

            // Create a SpannableString for the original price with strikethrough
            SpannableString spannableOriginalPrice = new SpannableString("₹" + originalPrice);
            spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);
            // Create the discount text
            spannableText = new SpannableStringBuilder();
            spannableText.append("₹" + sellingPrice + " ");
            spannableText.append(spannableOriginalPrice);

            holder.productPriceStrikeThroughTxt.setText(spannableOriginalPrice);
            // Set combined text to productPrice
            holder.productPriceTxt.setText(spannableText);
            // Set discount % separately to productDiscount with green color
            String discountText = "(Save ₹" + disAmount + ")";
            holder.productDiscount.setText(discountText);
            holder.productDiscount.setVisibility(View.VISIBLE);
        }else {
            // No discount, just show the selling price
            holder.productPriceTxt.setText("₹" + productDetailsList.get(position).getProductPrice());
            holder.productDiscount.setVisibility(View.GONE);
            holder.productPriceStrikeThroughTxt.setVisibility(View.GONE);
        }
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
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
                if (sessionManager.isLoggedIn()) {
                    deleteItem(position);
                }else {
                    sessionManager.removeCartItem(productDetailsList.get(position).getProductId());
                    productDetailsList.remove(position);
                    notifyDataSetChanged();
                    ((CartItemFragment) context).setOrderSummaryDetails();
                    ((CartItemFragment) context).checkCartItemArraySize();
                    setCartCount();
                    progressBarDialog.dismiss();
                }
            }
        });
        holder.moveToWishlistCard.setOnClickListener(new View.OnClickListener() {
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
                if (sessionManager.isLoggedIn()) {
                    deleteItem(position);
                }else {
                    sessionManager.removeCartItem(productDetailsList.get(position).getProductId());
                    productDetailsList.remove(position);
                    notifyDataSetChanged();
                    ((CartItemFragment) context).setOrderSummaryDetails();
                    ((CartItemFragment) context).checkCartItemArraySize();
                    setCartCount();
                    progressBarDialog.dismiss();
                }
            }
        });
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(productDetailsList.get(position).getProductQuantity());
                quantity++;
                updateProductQuantity(position, quantity);
                productDetailsList.get(position).setProductQuantity(String.valueOf(quantity));
                holder.productQuantityTxt.setText(String.valueOf(quantity));
                ((CartItemFragment) context).setOrderSummaryDetails();
            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(productDetailsList.get(position).getProductQuantity());
                if (quantity > 1) {
                    quantity--;
                    updateProductQuantity(position, quantity);
                    productDetailsList.get(position).setProductQuantity(String.valueOf(quantity));
                    holder.productQuantityTxt.setText(String.valueOf(quantity));
                    ((CartItemFragment) context).setOrderSummaryDetails();
                }
            }
        });
    }
    private void updateProductQuantity(int position, int quantity) {
        String deleteURL = Constant.BASE_URL + "cart/update";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId", productDetailsList.get(position).getProductId());
            jsonObject.put("quantity", quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, deleteURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(context.getContext(), "Item up successfully", Toast.LENGTH_SHORT).show();
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
//                                Toast.makeText(context.getContext(), message, Toast.LENGTH_LONG).show();
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
    private void setCartCount() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HomePageActivity activity = (HomePageActivity) context.getContext();
                activity.setCartCount();
            }
        }, 1500);  // Match the duration of the logo animation
    }
    private void deleteItem(int position) {
        String deleteURL = Constant.BASE_URL + "cart/remove/" + productDetailsList.get(position).getProductId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context.getContext(), "Item deleted successfully", Toast.LENGTH_SHORT).show();
                        if (!moveToWishlistCardBoolean){
                            moveToWishlistCardBoolean = true;
                            addToWishList(position);
                        }else {
                            sessionManager.removeCartItem(productDetailsList.get(position).getProductId());
                            productDetailsList.remove(position);
                            notifyDataSetChanged();
                            sessionManager.getCartFromServer();
                            ((CartItemFragment) context).setOrderSummaryDetails();
                            ((CartItemFragment) context).checkCartItemArraySize();
                            setCartCount();
                            progressBarDialog.dismiss();
                        }
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
    private void addToWishList(int position) {
        String orderURL = Constant.BASE_URL + "wishlist";
        String productId = productDetailsList.get(position).getProductId();
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
                        Toast.makeText(context.getContext(), "Item added to wishlist", Toast.LENGTH_SHORT).show();
                        sessionManager.getWishlistFromServer();
                        moveToWishlistCardBoolean = false;
                        sessionManager.removeCartItem(productDetailsList.get(position).getProductId());
                        productDetailsList.remove(position);
                        notifyDataSetChanged();
                        sessionManager.getCartFromServer();
                        ((CartItemFragment) context).setOrderSummaryDetails();
                        ((CartItemFragment) context).checkCartItemArraySize();
                        setCartCount();
                        progressBarDialog.dismiss();
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
        return productDetailsList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productTitleTxt, productPriceTxt, productSizeTxt, productColorTxt, productQuantityTxt,productDiscount,productPriceStrikeThroughTxt;;
        ImageView productImg;
        RelativeLayout deleteBtn,moveToWishlistCard;
        CardView plus,minus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitleTxt = itemView.findViewById(R.id.productTitleTxt);
            productPriceTxt = itemView.findViewById(R.id.productPriceTxt);
            productDiscount = itemView.findViewById(R.id.productDiscountTxt);
            productPriceStrikeThroughTxt = itemView.findViewById(R.id.productPriceStrikeThroughTxt);
            productSizeTxt = itemView.findViewById(R.id.sizeTxt);
            productColorTxt = itemView.findViewById(R.id.colorTxt);
            productQuantityTxt = itemView.findViewById(R.id.quantityDisplayTxt);
            plus = itemView.findViewById(R.id.plusCard);
            minus = itemView.findViewById(R.id.minusCard);
            deleteBtn = itemView.findViewById(R.id.removeRL);
            moveToWishlistCard = itemView.findViewById(R.id.moveToWishlistCard);
            productImg = itemView.findViewById(R.id.productImg);

        }
    }
}

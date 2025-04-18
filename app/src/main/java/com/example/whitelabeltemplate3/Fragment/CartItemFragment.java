package com.example.whitelabeltemplate3.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.whitelabeltemplate3.Activities.CheckOutOrderActivity;
import com.example.whitelabeltemplate3.Adapters.CartItemAdapter;
import com.example.whitelabeltemplate3.Adapters.CouponSelectingAdapter;
import com.example.whitelabeltemplate3.Models.CartItemModel;
import com.example.whitelabeltemplate3.Models.CouponSelectingModel;
import com.example.whitelabeltemplate3.Models.ProductImagesModel;
import com.example.whitelabeltemplate3.R;
import com.example.whitelabeltemplate3.Utils.Constant;
import com.example.whitelabeltemplate3.Utils.MySingletonFragment;
import com.example.whitelabeltemplate3.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartItemFragment extends Fragment {
    RecyclerView cartRecyclerView;
    CartItemAdapter cartItemAdapter;
    ArrayList<CartItemModel> cartItemModelArrayList;
    ArrayList<CouponSelectingModel> couponSelectingModelArrayList;
    TextView subTotalDisplayTxt, discountTxt, discountDisplayTxt, deliveryFeeDisplayTxt,
            finalAmountDisplayTxt,promoErrorTxt,promoCodeDiscountDisplayTxt;
    LinearLayout promoCodeDiscountLL;
    Button applyPromoBtn,checkOutBtn;
    EditText promoCodeET;
    RelativeLayout noDataLayout,couponsViewRL;
    NestedScrollView mainLayout;
    SessionManager sessionManager;
    String authToken;
    Dialog progressBarDialog;
    int totalProductQuantity = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        progressBarDialog = new Dialog(getContext());
        progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBarDialog.setContentView(R.layout.progress_bar_dialog);
        progressBarDialog.setCancelable(false);
        progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressBarDialog.show();

        couponsViewRL = view.findViewById(R.id.couponsViewRL);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        noDataLayout.setVisibility(View.GONE);
        mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.GONE);


        // Cart item Setup
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        couponSelectingModelArrayList = new ArrayList<>();
        cartItemModelArrayList = new ArrayList<>();

        //Order Summary Setup
        subTotalDisplayTxt = view.findViewById(R.id.subTotalDisplayTxt);
        discountTxt = view.findViewById(R.id.discountTxt);
        discountDisplayTxt = view.findViewById(R.id.discountDisplayTxt);
        deliveryFeeDisplayTxt = view.findViewById(R.id.sizeTxt);
        finalAmountDisplayTxt = view.findViewById(R.id.totalAmountDisplayTxt);
        promoErrorTxt = view.findViewById(R.id.invalidPromoTxt);
        promoCodeDiscountDisplayTxt = view.findViewById(R.id.promoCodeDiscountDisplayTxt);
        applyPromoBtn = view.findViewById(R.id.applyCodeBtn);
        checkOutBtn = view.findViewById(R.id.goToCheckOutTxt);
        promoCodeET = view.findViewById(R.id.promoCodeET);
        promoCodeDiscountLL = view.findViewById(R.id.promoCodeDiscountLL);

        if (sessionManager.isLoggedIn()) {
            getCart();
        }else {
            cartItemModelArrayList = sessionManager.getCart();
            if (!cartItemModelArrayList.isEmpty()) {
                cartRecyclerView.setAdapter(new CartItemAdapter(cartItemModelArrayList, CartItemFragment.this));
                mainLayout.setVisibility(View.VISIBLE);
                noDataLayout.setVisibility(View.GONE);
                progressBarDialog.dismiss();
                setOrderSummaryDetails();
            }else {
                mainLayout.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);
                progressBarDialog.dismiss();
            }
        }
        setUpCouponDialog();
//        setOrderSummaryDetails();

        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(getContext(), CheckOutOrderActivity.class);
                    if (promoCodeDiscount != 0) {
                        intent.putExtra("couponDiscount", promoCodeDiscount);
                        Log.e("couponDiscount", String.valueOf(promoCodeDiscount));
                    }
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "You are not logged in, Please login to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        couponsViewRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCouponDialog();
            }
        });

        applyPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new Dialog(getContext());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_dialog);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                checkAndApplyPromoCode(promoCodeET.getText().toString().trim());

            }
        });

        getCoupons();

        return view;
    }
    private void getCart() {
        String cartURL = Constant.BASE_URL + "cart";
        Log.e("ProductsURL", cartURL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, cartURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject dataObj = response.optJSONObject("data");
                            if (dataObj == null) return;

                            String cartId = dataObj.optString("_id", null);
                            JSONArray itemArray = dataObj.optJSONArray("items");
                            if (itemArray == null) return;

                            for (int i = 0; i < itemArray.length(); i++) {
                                JSONObject productObj0 = itemArray.optJSONObject(i);
                                if (productObj0 == null) continue;

                                String quantity = productObj0.optString("quantity", "0");

                                JSONObject productObj = productObj0.optJSONObject("product");
                                if (productObj == null) continue;

                                String productId = productObj.optString("_id", null);
                                String title = productObj.optString("title", "Unknown Product");

                                JSONObject slugObj = productObj.optJSONObject("meta");
                                String slug = (slugObj != null) ? slugObj.optString("slug", null) : null;

                                String MRP = productObj.optString("MRP", "0");
                                String price = productObj.optString("price", "0");

                                JSONObject discountObj = productObj.optJSONObject("discount");
                                String discountAmount = (discountObj != null) ? discountObj.optString("amount", "0") : "0";
                                String discountPercentage = (discountObj != null) ? discountObj.optString("percentage", "0") : "0";

                                String stock = productObj.optString("stock", "0");
                                String description = productObj.optString("description", "No description available");

                                JSONArray tagsArray = productObj.optJSONArray("tags");
                                String tags = parseTags(tagsArray != null ? tagsArray : new JSONArray());

                                String SKU = productObj.optString("SKU", "N/A");

                                ArrayList<ProductImagesModel> imagesList = new ArrayList<>();
                                JSONArray imageArray = productObj.optJSONArray("images");
                                if (imageArray != null) {
                                    for (int j = 0; j < imageArray.length(); j++) {
                                        String imageUrl = imageArray.optString(j, null);
                                        if (imageUrl != null) {
                                            Log.e("JSONIMG", imageUrl);
                                            imagesList.add(new ProductImagesModel(imageUrl));
                                        }
                                    }
                                }

                                String store = productObj.optString("store", null);
                                String category = productObj.optString("category", null);
                                String inputTag = productObj.optString("inputTag", null);

                                cartItemModelArrayList.add(new CartItemModel(cartId, productId, title, quantity,
                                        slug, MRP, price, discountAmount, discountPercentage, stock, description,
                                        tags, SKU, store, category, inputTag, "4", 0, imagesList));
                            }

                            if (!cartItemModelArrayList.isEmpty()) {
                                checkCartItemArraySize();
                                cartItemAdapter = new CartItemAdapter(cartItemModelArrayList, CartItemFragment.this);
                                cartRecyclerView.setAdapter(cartItemAdapter);
                                mainLayout.setVisibility(View.VISIBLE);
                                noDataLayout.setVisibility(View.GONE);
                                progressBarDialog.dismiss();
                                setOrderSummaryDetails();
                            } else {
                                mainLayout.setVisibility(View.GONE);
                                noDataLayout.setVisibility(View.VISIBLE);
                                progressBarDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            progressBarDialog.dismiss();
                            Log.e("JSONError", "Parsing error", e);
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
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("CartError", errorMessage);
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

        MySingletonFragment.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private String parseTags(JSONArray tagsArray) throws JSONException {
        StringBuilder tags = new StringBuilder();
        for (int j = 0; j < tagsArray.length(); j++) {
            tags.append(tagsArray.getString(j)).append(", ");
        }
        if (tags.length() > 0) {
            tags.setLength(tags.length() - 2); // Remove trailing comma and space
        }
        return tags.toString();
    }
    private void getCoupons() {
        String examCategoryURL = Constant.BASE_URL + "discount?pageNumber=1&pageSize=10&storeId=67d2b3da82e71e00672df277";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, examCategoryURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0;i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String couponId = jsonObject.getString("_id");
                                String couponCode = jsonObject.getString("code");
                                String couponType = jsonObject.getString("type");

                                JSONObject dateObj = jsonObject.getJSONObject("activeDates");
                                String startDate = dateObj.getString("startDate");
                                String endDate = dateObj.getString("endDate");

                                String discountType = null,discountValue = null;
                                if (jsonObject.has("discountValue")) {
                                    JSONObject discountObj = jsonObject.getJSONObject("discountValue");
                                    discountType = discountObj.getString("discountType");
                                    discountValue = discountObj.getString("value");
                                }

                                JSONObject discountObj = jsonObject.getJSONObject("minimumPurchase");
                                String purchaseType = discountObj.getString("purchaseType");
                                String purchaseValue = discountObj.getString("value");

                                String store = jsonObject.getString("store");
                                couponSelectingModelArrayList.add(new CouponSelectingModel(couponId,couponCode,couponType,startDate,endDate,discountType,discountValue,purchaseType,purchaseValue,store));
                            }
                        } catch (JSONException e) {
                            Log.e("Exam Catch error", "Error parsing JSON: " + e.getMessage());
                        }
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
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
                return headers;
            }
        };
        MySingletonFragment.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    int totalAmount = 0, finalTotalAmount = 0, shippingCharge = 0, discount = 0, promoCodeDiscount = 0;
    public void setOrderSummaryDetails() {
        totalAmount = 0; finalTotalAmount = 0; shippingCharge = 0; discount = 0;
        if (!cartItemModelArrayList.isEmpty()) {
            for (int i = 0; i < cartItemModelArrayList.size(); i++) {
                totalProductQuantity+= Integer.parseInt(cartItemModelArrayList.get(i).getProductQuantity());
                totalAmount += Integer.parseInt(cartItemModelArrayList.get(i).getProductMRP()) * Integer.parseInt(cartItemModelArrayList.get(i).getProductQuantity());
                discount += Integer.parseInt(cartItemModelArrayList.get(i).getDiscountAmount()) * Integer.parseInt(cartItemModelArrayList.get(i).getProductQuantity());
            }
            if (totalAmount > 500) {
                finalTotalAmount = totalAmount;
            } else {
                shippingCharge = 99;
                finalTotalAmount = totalAmount + 99;
            }
            finalTotalAmount -= discount;
            subTotalDisplayTxt.setText("₹" + String.valueOf(totalAmount) + ".00");
            finalAmountDisplayTxt.setText("₹" + String.valueOf(finalTotalAmount) + ".00");
            discountDisplayTxt.setText("-₹" + String.valueOf(discount) + ".00");
            deliveryFeeDisplayTxt.setText("+₹" + String.valueOf(shippingCharge) + ".00");

        }
    }
    public void checkCartItemArraySize() {
        if (cartItemModelArrayList.isEmpty()){
            mainLayout.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }else {
            mainLayout.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        }
    }
    public ArrayList<CartItemModel> getCartItemModelArrayList() {
        return cartItemModelArrayList;
    }
    public int getShippingCharge(){
        return shippingCharge;
    }

    private void checkAndApplyPromoCode(String couponCode) {
        boolean couponFound = false;
        for (int i = 0; i < couponSelectingModelArrayList.size(); i++){
            if (couponCode.equals(couponSelectingModelArrayList.get(i).getCouponCode())){
                couponFound = true;
                String couponType,couponDiscountType;
                int couponDiscountValue,newFinalAmount,newTotalAmount,newDiscountAmount;
                couponType = couponSelectingModelArrayList.get(i).getCouponType();
                couponDiscountType = couponSelectingModelArrayList.get(i).getDiscountType();
                discount = 0;
                for (int j = 0; j < cartItemModelArrayList.size(); j++){
                    discount += Integer.parseInt(cartItemModelArrayList.get(j).getDiscountAmount());
                }
                if (couponType.equalsIgnoreCase("ORDER")){
                    couponDiscountValue = Integer.parseInt(couponSelectingModelArrayList.get(i).getDiscountValue());
                    if (couponDiscountType.equalsIgnoreCase("PERCENTAGE")){
                        newTotalAmount = finalTotalAmount;
                        newDiscountAmount = finalTotalAmount * couponDiscountValue / 100;
                        newFinalAmount = finalTotalAmount - newDiscountAmount;
                        promoCodeDiscount = newDiscountAmount;
                        promoCodeDiscountDisplayTxt.setText("-₹" + promoCodeDiscount + ".00");
                        promoCodeDiscountLL.setVisibility(View.VISIBLE);
//                        subTotalDisplayTxt.setText("₹" + String.valueOf(newTotalAmount) + ".00");
                        finalAmountDisplayTxt.setText("₹" + String.valueOf(newFinalAmount) + ".00");
//                        discountDisplayTxt.setText("-₹" + String.valueOf(newDiscountAmount) + ".00");
                        promoCodeET.setText(couponCode);
                        promoErrorTxt.setVisibility(View.VISIBLE);
                        promoErrorTxt.setText("Promo Code Applied");
                        promoErrorTxt.setTextColor(ContextCompat.getColor(getContext(),R.color.green));
                    }else if (couponDiscountType.equalsIgnoreCase("FIXED_AMOUNT")){
                        newTotalAmount = finalTotalAmount;
                        newDiscountAmount = couponDiscountValue ;
                        newFinalAmount = finalTotalAmount - newDiscountAmount;
                        promoCodeDiscount = newDiscountAmount;
                        promoCodeDiscountDisplayTxt.setText("-₹" + promoCodeDiscountLL + ".00");
                        promoCodeDiscountLL.setVisibility(View.VISIBLE);
//                        subTotalDisplayTxt.setText("₹" + String.valueOf(newTotalAmount) + ".00");
                        finalAmountDisplayTxt.setText("₹" + String.valueOf(newFinalAmount) + ".00");
//                        discountDisplayTxt.setText("-₹" + String.valueOf(newDiscountAmount) + ".00");
                        promoErrorTxt.setVisibility(View.VISIBLE);
                        promoCodeET.setText(couponCode);
                        promoErrorTxt.setText("Promo Code Applied");
                        promoErrorTxt.setTextColor(ContextCompat.getColor(getContext(),R.color.green));
                    }
                } else if (couponType.equalsIgnoreCase("SHIPPING")) {
                    newFinalAmount = finalTotalAmount - shippingCharge;
                    shippingCharge = 0;
                    finalAmountDisplayTxt.setText("₹" + String.valueOf(newFinalAmount) + ".00");
                    deliveryFeeDisplayTxt.setText("₹ 0.00");
                    promoCodeET.setText(couponCode);
                }
                progressDialog.dismiss();
                break;
            }
        }
        if (!couponFound){
            progressDialog.dismiss();
            promoErrorTxt.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Please enter a valid promo code", Toast.LENGTH_SHORT).show();
        }
    }

    Dialog couponDialog,progressDialog;
    ImageView crossBtn;
    RecyclerView couponRecycler;
    CouponSelectingAdapter couponSelectingAdapter;
    private void setUpCouponDialog() {
        couponDialog = new Dialog(getContext());
        couponDialog.setContentView(R.layout.coupon_selecting_dialog_layout);

        crossBtn = couponDialog.findViewById(R.id.crossBtn);
        couponRecycler = couponDialog.findViewById(R.id.couponRecycler);
        couponRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

    }
    @SuppressLint("ResourceAsColor")
    private void openCouponDialog() {

        couponSelectingAdapter = new CouponSelectingAdapter(couponSelectingModelArrayList,CartItemFragment.this,finalTotalAmount,totalProductQuantity);
        couponRecycler.setAdapter(couponSelectingAdapter);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponDialog.dismiss();
            }
        });

        couponSelectingAdapter.notifyDataSetChanged();

        couponDialog.show();
        couponDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        couponDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        couponDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationDisplayBottomTop;
        couponDialog.getWindow().setGravity(Gravity.TOP);
        couponDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            couponDialog.getWindow().setStatusBarColor(R.color.white);
        }
    }
    public void closeCouponDialog(int position){
        progressDialog = new Dialog(getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_bar_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressDialog.show();

        String couponType,couponDiscountType,couponCodeStr;
        int couponDiscountValue,newFinalAmount,newTotalAmount,newDiscountAmount;
        couponType = couponSelectingModelArrayList.get(position).getCouponType();
        couponDiscountType = couponSelectingModelArrayList.get(position).getDiscountType();
        couponCodeStr = couponSelectingModelArrayList.get(position).getCouponCode();
        discount = 0;
        for (int i = 0; i < cartItemModelArrayList.size(); i++){
            discount += Integer.parseInt(cartItemModelArrayList.get(i).getDiscountAmount());
        }
        if (couponType.equalsIgnoreCase("ORDER")){
            couponDiscountValue = Integer.parseInt(couponSelectingModelArrayList.get(position).getDiscountValue());
            if (couponDiscountType.equalsIgnoreCase("PERCENTAGE")){
                newTotalAmount = finalTotalAmount;
                newDiscountAmount = finalTotalAmount * couponDiscountValue / 100;
                newFinalAmount = finalTotalAmount - newDiscountAmount;
                promoCodeDiscount = newDiscountAmount;
                promoCodeDiscountDisplayTxt.setText("-₹" + promoCodeDiscount + ".00");
                promoCodeDiscountLL.setVisibility(View.VISIBLE);
//                subTotalDisplayTxt.setText("₹" + String.valueOf(newTotalAmount) + ".00");
                finalAmountDisplayTxt.setText("₹" + String.valueOf(newFinalAmount) + ".00");
//                discountDisplayTxt.setText("-₹" + String.valueOf(newDiscountAmount) + ".00");
                promoErrorTxt.setVisibility(View.VISIBLE);
                promoCodeET.setText(couponCodeStr);
                promoErrorTxt.setText("Promo Code Applied");
                promoErrorTxt.setTextColor(ContextCompat.getColor(getContext(),R.color.green));
            }else if (couponDiscountType.equalsIgnoreCase("FIXED_AMOUNT")){
                newTotalAmount = finalTotalAmount;
                newDiscountAmount = couponDiscountValue ;
                newFinalAmount = finalTotalAmount - newDiscountAmount;
                promoCodeDiscount = newDiscountAmount;
                promoCodeDiscountDisplayTxt.setText("-₹" + promoCodeDiscount + ".00");
                promoCodeDiscountLL.setVisibility(View.VISIBLE);
//                subTotalDisplayTxt.setText("₹" + String.valueOf(newTotalAmount) + ".00");
                finalAmountDisplayTxt.setText("₹" + String.valueOf(newFinalAmount) + ".00");
//                discountDisplayTxt.setText("-₹" + String.valueOf(newDiscountAmount) + ".00");
                promoErrorTxt.setVisibility(View.VISIBLE);
                promoCodeET.setText(couponCodeStr);
                promoErrorTxt.setText("Promo Code Applied");
                promoErrorTxt.setTextColor(ContextCompat.getColor(getContext(),R.color.green));
            }
        } else if (couponType.equalsIgnoreCase("SHIPPING")) {
            newFinalAmount = finalTotalAmount - shippingCharge;
            shippingCharge = 0;
            finalAmountDisplayTxt.setText("₹" + String.valueOf(newFinalAmount) + ".00");
            deliveryFeeDisplayTxt.setText("₹ 0.00");
            promoCodeET.setText(couponCodeStr);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                couponDialog.dismiss();
                progressDialog.dismiss();
            }
        },500);
    }
}

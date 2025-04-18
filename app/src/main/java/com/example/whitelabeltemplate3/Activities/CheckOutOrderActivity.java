package com.example.whitelabeltemplate3.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.whitelabeltemplate3.Adapters.CheckOutAdapter;
import com.example.whitelabeltemplate3.Adapters.CheckOutAddressChangingRVAdapter;
import com.example.whitelabeltemplate3.Fragment.CartItemFragment;
import com.example.whitelabeltemplate3.Models.AddressItemModel;
import com.example.whitelabeltemplate3.Models.CartItemModel;
import com.example.whitelabeltemplate3.Models.CheckOutModel;
import com.example.whitelabeltemplate3.R;
import com.example.whitelabeltemplate3.Utils.Constant;
import com.example.whitelabeltemplate3.Utils.MySingleton;
import com.example.whitelabeltemplate3.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckOutOrderActivity extends AppCompatActivity {
    Button proceedToPayment;
    ImageView backBtn;
    RecyclerView checkoutRecyclerView;
    TextView subTotalDisplayTxt, discountDisplayTxt, deliveryFeeDisplayTxt, totalAmountDisplayTxt,
            changeAddressTxtBtn,promoCodeDiscountDisplayTxt;
    LinearLayout promoCodeDiscountLL;
    TextView loginUserName,addressLine1Txt, addressLine2Txt, addressLine3Txt;
    ArrayList<CheckOutModel> checkOutModelArrayList;
    ArrayList<CartItemModel> cartItemModelArrayList;
    ArrayList<AddressItemModel> addressItemArrayList = new ArrayList<>();
    RelativeLayout noDataLayout;
    NestedScrollView mainLayout;
    SessionManager sessionManager;
    String authToken;
    Dialog progressBarDialog;
    RadioGroup radioGroup;
    RadioButton radio1, radio2;
    String paymentMethodStr = "",selectedAddressId = "";
    String couponDiscountStr = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_order);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            EdgeToEdge.enable(this);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
//        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        sessionManager = new SessionManager(CheckOutOrderActivity.this);
        authToken = sessionManager.getUserData().get("authToken");

        noDataLayout = findViewById(R.id.noDataLayout);
        mainLayout = findViewById(R.id.mainLayout);

        backBtn = findViewById(R.id.imgMenu);
        checkoutRecyclerView = findViewById(R.id.checkoutRecyclerView);

        backBtn.setOnClickListener(v -> {
            finish();
        });
        //Order Summary Setup
        subTotalDisplayTxt = findViewById(R.id.subTotalDisplayTxt);
        discountDisplayTxt = findViewById(R.id.discountDisplayTxt);
        deliveryFeeDisplayTxt = findViewById(R.id.sizeTxt);
        totalAmountDisplayTxt = findViewById(R.id.totalAmountDisplayTxt);
        promoCodeDiscountLL = findViewById(R.id.promoCodeDiscountLL);
        promoCodeDiscountDisplayTxt = findViewById(R.id.promoCodeDiscountDisplayTxt);

        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkOutModelArrayList = new ArrayList<>();
        cartItemModelArrayList = new ArrayList<>();
        getAllAddress();
        CartItemFragment fragment = (CartItemFragment) HomePageActivity.getCurrentFragment();
        if (fragment != null) {
            cartItemModelArrayList = fragment.getCartItemModelArrayList();
        } else {
            Log.e("Activity", "Fragment not found.");
        }

        for (int i = 0; i < cartItemModelArrayList.size(); i++) {
            String cartId = cartItemModelArrayList.get(i).getCartId();
            String productId = cartItemModelArrayList.get(i).getProductId();
            String name = cartItemModelArrayList.get(i).getProductTitle();
            String mrp = cartItemModelArrayList.get(i).getProductMRP();
            String price = cartItemModelArrayList.get(i).getProductPrice();
            String discountAMT = cartItemModelArrayList.get(i).getDiscountAmount();
            String quantity = cartItemModelArrayList.get(i).getProductQuantity();
            String imgURL = cartItemModelArrayList.get(i).getProductImagesModelsArrList().get(0).getProductImage();
            checkOutModelArrayList.add(new CheckOutModel(cartId,productId,name,price,mrp,discountAMT,quantity,imgURL));
        }
        checkoutRecyclerView.setAdapter(new CheckOutAdapter(checkOutModelArrayList,this));

        couponDiscountStr = String.valueOf(getIntent().getIntExtra("couponDiscount",0));
        Log.e("true",couponDiscountStr);
        if (!couponDiscountStr.equals("0")){
            promoCodeDiscountDisplayTxt.setText("-₹" + couponDiscountStr);
            promoCodeDiscountLL.setVisibility(View.VISIBLE);
        }
        setOrderSummaryDetails();
        // Change Address Setup
        changeAddressTxtBtn = findViewById(R.id.changeAddressTxt);
        changeAddressTxtBtn.setOnClickListener(v -> {
            openEditDialog();
        });
        checkOutItemArraySize();

        // Address Showing Setup
        loginUserName = findViewById(R.id.loginUserName);
        addressLine1Txt = findViewById(R.id.addressLine1);
        addressLine2Txt = findViewById(R.id.addressLine2);
        addressLine3Txt = findViewById(R.id.addressLine3);

        String name = sessionManager.getUserData().get("fullName");
        String phone = sessionManager.getUserData().get("email");
        loginUserName.setText(name + ", " + phone);
        // Radio Group
        proceedToPayment = findViewById(R.id.proceedToPayment);
        radioGroup = findViewById(R.id.radioGroup);
        radio1 = findViewById(R.id.radio1);
        radio2 = findViewById(R.id.radio2);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio1) {
                    paymentMethodStr = "COD";
                } else if (checkedId == R.id.radio2) {
                    paymentMethodStr = "RazorPay";
                }
            }
        });
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarDialog = new Dialog(CheckOutOrderActivity.this);
                progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressBarDialog.setContentView(R.layout.progress_bar_dialog);
                progressBarDialog.setCancelable(false);
                progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressBarDialog.show();
                if (paymentMethodStr.isEmpty()){
                    progressBarDialog.dismiss();
                    Toast.makeText(CheckOutOrderActivity.this, "Please Select Payment Method", Toast.LENGTH_SHORT).show();
                }else if (selectedAddressId.isEmpty()){
                    progressBarDialog.dismiss();
                    Toast.makeText(CheckOutOrderActivity.this, "Please Select Address", Toast.LENGTH_SHORT).show();
                }else {
                    checkOut();
                }
            }
        });
    }
    Dialog drawerDialog;
    ImageView crossBtn;
    RecyclerView addressRV;
    CardView addAddressCardView;
    public void openEditDialog() {
        drawerDialog = new Dialog(CheckOutOrderActivity.this);
        drawerDialog.setContentView(R.layout.checkout_change_address_dialog);
        drawerDialog.setCancelable(true);

        crossBtn = drawerDialog.findViewById(R.id.crossBtn);
        crossBtn.setOnClickListener(v -> {
            drawerDialog.dismiss();
        });

        addAddressCardView = drawerDialog.findViewById(R.id.addAddressCardView);
        addAddressCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOutOrderActivity.this, AddressShowingInputActivity.class);
                intent.putExtra("openAddAddress",true);
                startActivity(intent);
                finish();
            }
        });

        addressRV = drawerDialog.findViewById(R.id.addressItemLayout);
        addressRV.setLayoutManager(new LinearLayoutManager(CheckOutOrderActivity.this));

        addressRV.setAdapter(new CheckOutAddressChangingRVAdapter(addressItemArrayList,CheckOutOrderActivity.this));

        drawerDialog.show();
        drawerDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        drawerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        drawerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationDisplayPopUp;
        drawerDialog.getWindow().setGravity(Gravity.TOP);
        drawerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawerDialog.getWindow().setStatusBarColor(ContextCompat.getColor(CheckOutOrderActivity.this,R.color.white));
        }
    }
    private void getAllAddress() {
        String addAddressURL = Constant.BASE_URL + "address";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, addAddressURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            addressItemArrayList.clear();
                            JSONArray dataJSONAry = response.getJSONArray("data");
                            for (int i = 0; i < dataJSONAry.length(); i++) {
                                JSONObject dataObject = dataJSONAry.getJSONObject(i);
                                String addressId = dataObject.getString("_id");
                                String addressType = dataObject.getString("addressType");
                                String firstName = dataObject.getString("firstName");
                                String lastName = dataObject.getString("lastName");
                                String country = dataObject.getString("country");
                                String streetAddress = dataObject.getString("streetAddress");
                                String apartment = dataObject.getString("apartment");
                                String city = dataObject.getString("city");
                                String state = dataObject.getString("state");
                                String pincode = dataObject.getString("pincode");
                                String phone = dataObject.getString("phone");
                                String email = dataObject.getString("email");
                                String isDefault = dataObject.getString("isDefault");
                                addressItemArrayList.add(new AddressItemModel(addressId,addressType,firstName,lastName,null,phone,email,apartment,streetAddress,city,pincode,state,country,isDefault));
                            }
                            setAddress();
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
                                Toast.makeText(CheckOutOrderActivity.this, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    int totalAmount = 0,finalTotalAmount = 0,shippingCharge = 0,discount = 0,couponDiscount = 0;

    public void setOrderSummaryDetails() {
        couponDiscount = Integer.parseInt(couponDiscountStr);
        for (int i = 0; i < checkOutModelArrayList.size(); i++) {
            totalAmount += Integer.parseInt(checkOutModelArrayList.get(i).getProductMRP()) * Integer.parseInt(cartItemModelArrayList.get(i).getProductQuantity());
            discount += Integer.parseInt(cartItemModelArrayList.get(i).getDiscountAmount()) * Integer.parseInt(cartItemModelArrayList.get(i).getProductQuantity());
        }
        if (totalAmount > 500){
            finalTotalAmount = totalAmount;
        }else {
            shippingCharge = 99;
            finalTotalAmount = totalAmount + 99;
        }
        finalTotalAmount -= discount;
        if (couponDiscount != 0){
            finalTotalAmount -= couponDiscount;
        }
        subTotalDisplayTxt.setText("₹" + String.valueOf(totalAmount) + ".00");
        totalAmountDisplayTxt.setText("₹" + String.valueOf(finalTotalAmount) + ".00");
        discountDisplayTxt.setText("-₹" + String.valueOf(discount) + ".00");
        deliveryFeeDisplayTxt.setText("+₹" + String.valueOf(shippingCharge) + ".00");
    }
    public void checkOutItemArraySize() {
        if (checkOutModelArrayList.isEmpty()){
            mainLayout.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }else {
            mainLayout.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        }
    }
    public void setAddressFromAdapter(String addressLine1, String addressLine2, String addressLine3, String addressId) {
        addressLine1Txt.setText(addressLine1);
        addressLine2Txt.setText(addressLine2);
        addressLine3Txt.setText(addressLine3);
        selectedAddressId = addressId;
        drawerDialog.dismiss();
    }
    public void setAddress() {
        if (addressItemArrayList.isEmpty()){
            return;
        }
        String addressLine1, addressLine2, addressLine3;

        addressLine1 = addressItemArrayList.get(0).getFirstName() + " " +
                addressItemArrayList.get(0).getLastName()+ "," +
                addressItemArrayList.get(0).getPhone();
        addressLine2 = addressItemArrayList.get(0).getApartment() + ", " +
                addressItemArrayList.get(0).getStreet() + ", " +
                addressItemArrayList.get(0).getCity() + ", " +
                addressItemArrayList.get(0).getPincode();
        addressLine3 = addressItemArrayList.get(0).getState() + ", " +
                addressItemArrayList.get(0).getCountry();
        selectedAddressId = addressItemArrayList.get(0).getAddressId();

        addressLine1Txt.setText(addressLine1);
        addressLine2Txt.setText(addressLine2);
        addressLine3Txt.setText(addressLine3);
    }
    public void checkOut() {
        String chekoutURL = Constant.BASE_URL + "order/checkout";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("totalAmount", totalAmount);
            jsonObject.put("shippingCharges", shippingCharge);
            jsonObject.put("taxAmount", 0);
            jsonObject.put("discounts", discount + couponDiscount);
            jsonObject.put("finalAmount", finalTotalAmount);
            jsonObject.put("paymentMethod", paymentMethodStr);
            jsonObject.put("addressId", selectedAddressId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, chekoutURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject orderObject = response.optJSONObject("payment");
                        String orderId = orderObject.optString("orderId");

                        if (paymentMethodStr.equals("COD")){
                            placeCODOrder(orderId);
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
                                Toast.makeText(CheckOutOrderActivity.this, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void placeCODOrder(String orderId) {
        String addAddressURL = Constant.BASE_URL + "order/cod";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addAddressURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        clearCart();
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
                                Toast.makeText(CheckOutOrderActivity.this, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void clearCart() {
        String clearCartURL = Constant.BASE_URL + "cart/clean";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, clearCartURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        startActivity(new Intent(CheckOutOrderActivity.this, OrderPlacedActivity.class));
                        finish();
                        sessionManager.getCartFromServer();
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
                                Toast.makeText(CheckOutOrderActivity.this, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
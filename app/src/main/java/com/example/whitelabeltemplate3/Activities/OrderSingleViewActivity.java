package com.example.whitelabeltemplate3.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.whitelabeltemplate3.Adapters.SingleOrderPageItemsAdapter;
import com.example.whitelabeltemplate3.Adapters.TrackingOrderTimelineAdapter;
import com.example.whitelabeltemplate3.Models.SingleOrderPageItemsModel;
import com.example.whitelabeltemplate3.Models.TrackingOrderTimelineModel;
import com.example.whitelabeltemplate3.R;
import com.example.whitelabeltemplate3.Utils.Constant;
import com.example.whitelabeltemplate3.Utils.MySingleton;
import com.example.whitelabeltemplate3.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderSingleViewActivity extends AppCompatActivity {
    TextView orderIdTxt,orderDateTxt,totalAmountDisplayTxt,discountDisplayTxt,shippingChargeDisplayTxt, taxAmountDisplayTxt,tFinalAmountDisplayTxt,shippingUserNameTxt,shippingAddressLine1Txt,shippingAddressLine2Txt,shippingAddressLine3Txt,paymentMethodTxt;
    String orderIdStr,orderDateStr,shippingUserNameStr, shippingAddressLineStr1, shippingAddressLineStr2, shippingAddressLineStr3;
    RecyclerView orderItemsRecyclerView;
    SingleOrderPageItemsAdapter sOrderItemsAdapter;
    ArrayList<SingleOrderPageItemsModel> sOrderItemsModelList = new ArrayList<>();
    ImageView backBtn;
    RelativeLayout trackOrderBtn;
    SessionManager sessionManager;
    NestedScrollView mainNestedScroll;
    String authToken;
    Dialog progressBarDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_single_view);

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

        sessionManager = new SessionManager(OrderSingleViewActivity.this);
        authToken = sessionManager.getUserData().get("authToken");

        backBtn = findViewById(R.id.imgBack);
        backBtn.setOnClickListener(v -> onBackPressed());

        orderIdTxt = findViewById(R.id.orderIdTxt);
        orderDateTxt = findViewById(R.id.orderDateTxt);
        totalAmountDisplayTxt = findViewById(R.id.totalAmountDisplayTxt);
        tFinalAmountDisplayTxt = findViewById(R.id.tFinalAmountDisplayTxt);
        discountDisplayTxt = findViewById(R.id.discountDisplayTxt);
        shippingChargeDisplayTxt = findViewById(R.id.shippingChargesDisplayTxt);
        taxAmountDisplayTxt = findViewById(R.id.taxAmountDisplayTxt);

        shippingUserNameTxt = findViewById(R.id.shippingUserName);
        shippingAddressLine1Txt = findViewById(R.id.shippingAddressLine1);
        shippingAddressLine2Txt = findViewById(R.id.shippingAddressLine2);
        shippingAddressLine3Txt = findViewById(R.id.shippingAddressLine3);
        paymentMethodTxt = findViewById(R.id.payMethodDisplayTxt);

        mainNestedScroll = findViewById(R.id.main);
        mainNestedScroll.setVisibility(View.GONE);

        progressBarDialog = new Dialog(OrderSingleViewActivity.this);
        progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBarDialog.setContentView(R.layout.progress_bar_dialog);
        progressBarDialog.setCancelable(false);
        progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressBarDialog.show();

        trackOrderBtn = findViewById(R.id.trackOrderBtn);
        trackOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrackingDialog();
            }
        });

        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderIdStr = getIntent().getStringExtra("orderId");
        orderDateStr = getIntent().getStringExtra("orderDate");

        orderIdTxt.setText("Order Id: " + orderIdStr);
        orderDateTxt.setText("Order on: " + orderDateStr);
//        totalAmountDisplayTxt.setText("₹ " +orderPrice);
//        tFinalAmountDisplayTxt.setText("₹ " +orderPrice);

        getOrderById();

    }
    Dialog drawerDialog;
    ImageView crossBtn;
    TextView cancelOrderBtn;
    private RecyclerView recyclerView;
    private TrackingOrderTimelineAdapter adapter;
    private List<TrackingOrderTimelineModel> orderStages;
    @SuppressLint("ResourceAsColor")
    private void showTrackingDialog() {
        drawerDialog = new Dialog(this);
        drawerDialog.setContentView(R.layout.tracking_order_timeline_dialog);
        drawerDialog.setCancelable(true);

        recyclerView = drawerDialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        crossBtn = drawerDialog.findViewById(R.id.crossBtn);
        cancelOrderBtn = drawerDialog.findViewById(R.id.cancelOrderBtn);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerDialog.dismiss();
            }
        });

        cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });

        // Sample order stages
        orderStages = new ArrayList<>();
        orderStages.add(new TrackingOrderTimelineModel("Order Placed", "Your order has been placed", R.drawable.ic_order_placed));
        orderStages.add(new TrackingOrderTimelineModel("Processing", "Your order is being processed", R.drawable.ic_order_processing));
        orderStages.add(new TrackingOrderTimelineModel("Shipped", "Your order has been shipped", R.drawable.ic_order_shipped));
        orderStages.add(new TrackingOrderTimelineModel("Delivered", "Your order has been delivered", R.drawable.ic_order_delivered));

        // Set up the adapter
        adapter = new TrackingOrderTimelineAdapter(orderStages,OrderSingleViewActivity.this);
        recyclerView.setAdapter(adapter);

        drawerDialog.show();
        drawerDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        drawerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        drawerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationDisplayRight;
        drawerDialog.getWindow().setGravity(Gravity.TOP);
        drawerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawerDialog.getWindow().setStatusBarColor(R.color.white);
        }
    }
    private void cancelOrder() {
        String orderURL = Constant.BASE_URL + "order/cancel";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId", orderIdStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, orderURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(OrderSingleViewActivity.this, "Order Cancelled SuccessFully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
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
                                Toast.makeText(OrderSingleViewActivity.this, message, Toast.LENGTH_LONG).show();
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

    private void getOrderById() {
        String orderURL = Constant.BASE_URL + "order/" + orderIdStr;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, orderURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject dataJSONObj = response.optJSONObject("data");
                        if (dataJSONObj == null) {
                            Toast.makeText(OrderSingleViewActivity.this, "Invalid response data", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String orderId = dataJSONObj.optString("orderId", "");
                        String totalAmount = dataJSONObj.optString("totalAmount", "0");
                        String shippingCharges = dataJSONObj.optString("shippingCharges", "0");
                        String taxAmount = dataJSONObj.optString("taxAmount", "0");
                        String discounts = dataJSONObj.optString("discounts", "0");
                        String finalAmount = dataJSONObj.optString("finalAmount", "0");

                        JSONObject addressObj = dataJSONObj.optJSONObject("shippingAddress");
                        String addressType = "", firstName = "", lastName = "", country = "", streetAddress = "", apartment = "", city = "", state = "", pincode = "", phone = "", email = "";
                        if (addressObj != null) {
                            addressType = addressObj.optString("addressType", "");
                            firstName = addressObj.optString("firstName", "");
                            lastName = addressObj.optString("lastName", "");
                            country = addressObj.optString("country", "");
                            streetAddress = addressObj.optString("streetAddress", "");
                            apartment = addressObj.optString("apartment", "");
                            city = addressObj.optString("city", "");
                            state = addressObj.optString("state", "");
                            pincode = addressObj.optString("pincode", "");
                            phone = addressObj.optString("phone", "");
                            email = addressObj.optString("email", "");
                        }

                        JSONObject paymentObj = dataJSONObj.optJSONObject("payment");
                        String paymentMethod = (paymentObj != null) ? paymentObj.optString("method", "") : "";

                        JSONArray productArray = dataJSONObj.optJSONArray("products");
                        if (productArray != null) {
                            for (int i = 0; i < productArray.length(); i++) {
                                JSONObject productObj = productArray.optJSONObject(i);
                                if (productObj == null) continue;

                                JSONObject productObj2 = productObj.optJSONObject("product");
                                if (productObj2 == null) continue;

                                String productId = productObj2.optString("_id", "");
                                String productTitle = productObj2.optString("title", "");
                                String productPrice = productObj2.optString("price", "0");
                                JSONArray imageArray = productObj2.optJSONArray("images");
                                String productImgUrl = (imageArray != null) ? imageArray.optString(0, null) : null;

                                sOrderItemsModelList.add(new SingleOrderPageItemsModel(productId, productImgUrl, productTitle, productPrice));
                            }
                        }

                        shippingUserNameStr = firstName + " " + lastName;
                        shippingAddressLineStr1 = apartment + ", " + streetAddress + ", " + city;
                        shippingAddressLineStr2 = state + ", " + country + ", " + pincode;
                        shippingAddressLineStr3 = phone + ", " + email;

                        sOrderItemsAdapter = new SingleOrderPageItemsAdapter(sOrderItemsModelList, OrderSingleViewActivity.this);
                        orderItemsRecyclerView.setAdapter(sOrderItemsAdapter);

                        shippingUserNameTxt.setText(shippingUserNameStr);
                        shippingAddressLine1Txt.setText(shippingAddressLineStr1);
                        shippingAddressLine2Txt.setText(shippingAddressLineStr2);
                        shippingAddressLine3Txt.setText(shippingAddressLineStr3);
                        paymentMethodTxt.setText(paymentMethod);

                        totalAmountDisplayTxt.setText("₹ " + totalAmount);
                        shippingChargeDisplayTxt.setText("₹ " + shippingCharges);
                        discountDisplayTxt.setText("-₹ " + discounts);
                        taxAmountDisplayTxt.setText("₹ " + taxAmount);
                        tFinalAmountDisplayTxt.setText("₹ " + finalAmount);

                        progressBarDialog.dismiss();
                        mainNestedScroll.setVisibility(View.VISIBLE);

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
                                Toast.makeText(OrderSingleViewActivity.this, message, Toast.LENGTH_LONG).show();
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
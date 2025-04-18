package com.example.whitelabeltemplate3.Activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.example.whitelabeltemplate3.Adapters.MyOrdersAdapter;
import com.example.whitelabeltemplate3.Models.MyOrderModel;
import com.example.whitelabeltemplate3.Models.ProductImagesModel;
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

public class MyOrderActivity extends AppCompatActivity {
    ImageView backBtn;
    RecyclerView myOrderRecyclerView;
    ArrayList<MyOrderModel> myOrderArrayList;
    RelativeLayout noDataLayout;
    NestedScrollView mainNestedLayout;
    MyOrdersAdapter myOrdersAdapter;
    ArrayAdapter<String> dropdownStatusArrayAdapter;
    ArrayAdapter<String> dropdownFilterArrayAdapter;
    AutoCompleteTextView autoCompStatusTV,autoCompFilterTV;
    final String[] selectedStatusItem = {""};
    final String[] selectedFilterItem = {""};
    int itemPerPage = 10, totalPages = 1,currentPage = 1;
    SessionManager sessionManager;
    String authToken;
    Dialog progressBarDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

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
        sessionManager = new SessionManager(MyOrderActivity.this);
        authToken = sessionManager.getUserData().get("authToken");
        backBtn = findViewById(R.id.imgBack);
        myOrderRecyclerView = findViewById(R.id.myOrderRecyclerView);

        autoCompStatusTV = findViewById(R.id.autoCompStatusTV);
        autoCompFilterTV = findViewById(R.id.autoCompFilterTV);

        noDataLayout = findViewById(R.id.noDataLayout);
        mainNestedLayout = findViewById(R.id.mainNestedLayout);
        noDataLayout.setVisibility(View.GONE);
        mainNestedLayout.setVisibility(View.GONE);

        progressBarDialog = new Dialog(MyOrderActivity.this);
        progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBarDialog.setContentView(R.layout.progress_bar_dialog);
        progressBarDialog.setCancelable(false);
        progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressBarDialog.show();

        setUpDropDown();

        myOrderArrayList = new ArrayList<>();
        myOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getAllOrders();
    }
    private void setUpDropDown() {
        String[] languages = getResources().getStringArray(R.array.my_orders_status_sort_list);
        dropdownStatusArrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item_text_layout, languages);
        autoCompStatusTV.setAdapter(dropdownStatusArrayAdapter);
        autoCompStatusTV.setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));
        autoCompStatusTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedStatusItem[0] = parent.getItemAtPosition(position).toString();
                noDataLayout.setVisibility(View.GONE);
                myOrderRecyclerView.setVisibility(View.GONE);
                progressBarDialog = new Dialog(MyOrderActivity.this);
                progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressBarDialog.setContentView(R.layout.progress_bar_dialog);
                progressBarDialog.setCancelable(false);
                progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressBarDialog.show();
                getAllOrders();
            }
        });
        String[] languages2 = getResources().getStringArray(R.array.my_orders_filter_sort_list);
        dropdownFilterArrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item_text_layout, languages2);
        autoCompFilterTV.setAdapter(dropdownFilterArrayAdapter);
        autoCompFilterTV.setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));
        autoCompFilterTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFilterItem[0] = parent.getItemAtPosition(position).toString();
                noDataLayout.setVisibility(View.GONE);
                myOrderRecyclerView.setVisibility(View.GONE);
                progressBarDialog = new Dialog(MyOrderActivity.this);
                progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressBarDialog.setContentView(R.layout.progress_bar_dialog);
                progressBarDialog.setCancelable(false);
                progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressBarDialog.show();
                getAllOrders();
            }
        });
    }

    private void getAllOrders() {
        String filter = "",status = "";
        if (selectedFilterItem[0].equals("All")){
            filter = "";
        }else if (selectedFilterItem[0].equals("1 months ago")){
            filter = "30d";
        }else if (selectedFilterItem[0].equals("3 months ago")){
            filter = "90d";
        }else if (selectedFilterItem[0].equals("6 months ago")){
            filter = "180d";
        }else if (selectedFilterItem[0].equals("2025")){
            filter = "2025";
        }else if (selectedFilterItem[0].equals("2024")){
            filter = "2024";
        }
        if (!selectedStatusItem[0].equals("All")) {
            status = selectedStatusItem[0];
        }
        String orderURL = Constant.BASE_URL + "order?pageNumber=" + currentPage + "&pageSize=" + itemPerPage
                + "&orderDate=" + filter + "&status=" + status;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, orderURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            myOrderArrayList.clear();
                            JSONArray dataJSONAry = response.getJSONArray("data");
                            for (int i = 0; i < dataJSONAry.length(); i++){
                                JSONObject dataJSONObj = dataJSONAry.getJSONObject(i);
                                String orderId = dataJSONObj.getString("orderId");
                                String finalAmount = dataJSONObj.getString("finalAmount");
                                String status = dataJSONObj.getString("status");
                                String orderDate = dataJSONObj.getString("createdAt");

                                JSONObject productObj = dataJSONObj.getJSONArray("product").getJSONObject(0);
                                String productTitle = productObj.getString("title");
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

                                myOrderArrayList.add(new MyOrderModel(orderId,finalAmount,status,orderDate,productTitle,imagesList));
                            }
                            if (myOrderArrayList.isEmpty()){
                                progressBarDialog.dismiss();
                                noDataLayout.setVisibility(View.VISIBLE);
                                myOrderRecyclerView.setVisibility(View.GONE);
                            }else {
                                progressBarDialog.dismiss();
                                noDataLayout.setVisibility(View.GONE);
                                mainNestedLayout.setVisibility(View.VISIBLE);
                                myOrdersAdapter = new MyOrdersAdapter(myOrderArrayList, MyOrderActivity.this);
                                myOrderRecyclerView.setAdapter(myOrdersAdapter);
                                myOrderRecyclerView.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            progressBarDialog.dismiss();
                            Log.e("Exam Catch error", "Error parsing JSON: " + e.getMessage());
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
                                Toast.makeText(MyOrderActivity.this, message, Toast.LENGTH_LONG).show();
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
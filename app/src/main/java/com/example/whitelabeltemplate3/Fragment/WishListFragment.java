package com.example.whitelabeltemplate3.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.whitelabeltemplate3.Adapters.ProductRecyclerForFragmentAdapter;
import com.example.whitelabeltemplate3.Models.ProductDetailsModel;
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

public class WishListFragment extends Fragment {
    RecyclerView wishListRecycler;
    ArrayList<ProductDetailsModel> newArrivalList;
    RelativeLayout noDataLayout,mainLayout;
    SessionManager sessionManager;
    String authToken;
    Dialog progressBarDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

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

        noDataLayout = view.findViewById(R.id.noDataLayout);
        mainLayout = view.findViewById(R.id.mainLayout);
        wishListRecycler = view.findViewById(R.id.wishListRecyclerView);
        wishListRecycler.setLayoutManager(new GridLayoutManager(getContext(),2));

        newArrivalList = new ArrayList<>();

        if (sessionManager.isLoggedIn()) {
            getWishList();
        }else {
            newArrivalList = sessionManager.getWishList();
            if (!newArrivalList.isEmpty()) {
                wishListRecycler.setAdapter(new ProductRecyclerForFragmentAdapter(newArrivalList, WishListFragment.this));
                mainLayout.setVisibility(View.VISIBLE);
                noDataLayout.setVisibility(View.GONE);
                progressBarDialog.dismiss();
            }else {
                mainLayout.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);
                progressBarDialog.dismiss();
            }
        }

        return view;
    }
    private void getWishList() {
        String wishlistURL = Constant.BASE_URL + "wishlist";
        Log.e("ProductsURL", wishlistURL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, wishlistURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.optJSONArray("data");

                            if (dataArray != null) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject productObj1 = dataArray.getJSONObject(i);
                                    JSONObject productObj = productObj1.getJSONObject("product");

                                    String productId = productObj.optString("_id", null);
                                    String title = productObj.optString("title", null);

                                    JSONObject slugObj = productObj.optJSONObject("meta");
                                    String slug = (slugObj != null) ? slugObj.optString("slug", null) : null;

                                    String MRP = productObj.optString("MRP", null);
                                    String price = productObj.optString("price", null);

                                    JSONObject discountObj = productObj.optJSONObject("discount");
                                    String discountAmount = (discountObj != null) ? discountObj.optString("amount", null) : null;
                                    String discountPercentage = (discountObj != null) ? discountObj.optString("percentage", null) : null;

                                    String stock = productObj.optString("stock", null);
                                    String description = productObj.optString("description", null);

                                    JSONArray tagsArray = productObj.optJSONArray("tags");
                                    String tags = (tagsArray != null) ? parseTags(tagsArray) : null;

                                    String SKU = productObj.optString("SKU", null);

                                    // Handling Images
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

                                    newArrivalList.add(new ProductDetailsModel(
                                            productId, title, slug, MRP, price, discountAmount, discountPercentage,
                                            stock, description, tags, SKU, store, category, inputTag, "4", 1, imagesList
                                    ));
                                }
                            }

                            if (!newArrivalList.isEmpty()) {
                                wishListRecycler.setAdapter(new ProductRecyclerForFragmentAdapter(newArrivalList, WishListFragment.this));
                                mainLayout.setVisibility(View.VISIBLE);
                                noDataLayout.setVisibility(View.GONE);
                                progressBarDialog.dismiss();
                            } else {
                                noDataLayout.setVisibility(View.VISIBLE);
                                mainLayout.setVisibility(View.GONE);
                                progressBarDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            progressBarDialog.dismiss();
                            e.printStackTrace();
                            Log.e("JSONParsingError", "Error parsing response: " + e.getMessage());
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
    public void checkWishListItemArraySize() {
        if (newArrivalList.isEmpty()){
            mainLayout.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }else {
            mainLayout.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        }
    }
}
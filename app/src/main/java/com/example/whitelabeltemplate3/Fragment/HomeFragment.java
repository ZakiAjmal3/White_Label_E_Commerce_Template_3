package com.example.whitelabeltemplate3.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.whitelabeltemplate3.Activities.HomePageActivity;
import com.example.whitelabeltemplate3.Adapters.ProductRecyclerForFragmentAdapter;
import com.example.whitelabeltemplate3.Models.AllCollectionsModel;
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

public class HomeFragment extends Fragment {

    ImageSlider imageSlider;
    ArrayList<SlideModel> imageList;
    ArrayList<ProductDetailsModel> topSellArrayList;
    RecyclerView newArrivalRecycler,topSellRecycler;
    RelativeLayout mainLayout;
    Dialog progressBarDialog;
    EditText searchView;
    LinearLayout collectionsContainer;
    ArrayList<AllCollectionsModel> collectionIdsArrayList = new ArrayList<>();
    SessionManager sessionManager;
    String storeId,authToken;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(getContext());
        storeId = sessionManager.getStoreId();
        authToken = sessionManager.getUserData().get("authToken");

        mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.GONE);
        progressBarDialog = new Dialog(getContext());
        progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBarDialog.setContentView(R.layout.progress_bar_dialog);
        progressBarDialog.setCancelable(false);
        progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressBarDialog.show();
        collectionsContainer = view.findViewById(R.id.collectionsContainer);
        imageSlider = view.findViewById(R.id.image_slider);
        searchView = view.findViewById(R.id.search_view);
        searchView.setFocusable(false);

        imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.crockery1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.sofa1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.table1, ScaleTypes.FIT));

        imageSlider.setImageList(imageList);

//        newArrivalRecycler = view.findViewById(R.id.newArrivalTxtRecycler);
//        topSellRecycler = view.findViewById(R.id.topSellRecycler);
//        newArrivalRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
//        topSellRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
//
//        topSellArrayList = new ArrayList<>();
//        topSellArrayList.add(new HomepageRecyclerModel("Sofa", "250", R.drawable.sofa1));
//        topSellArrayList.add(new HomepageRecyclerModel("Crockery", "300", R.drawable.crockery1));
//        topSellArrayList.add(new HomepageRecyclerModel("Table", "500", R.drawable.table1));
//        topSellArrayList.add(new HomepageRecyclerModel("Lamp", "450", R.drawable.lamp1));

//        newArrivalRecycler.setAdapter(new HomepageRecyclerAdapter(topSellArrayList,this));
//        topSellRecycler.setAdapter(new HomepageRecyclerAdapter(topSellArrayList,this));

        getCollections();

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomePageActivity) getActivity()).loadFragment(new SearchFragment());
                ((HomePageActivity) getActivity()).setSearchFragmentSelected();
            }
        });

        return view;
    }

    private void getCollections() {
        String newArrivalURL = Constant.BASE_URL + "collection/" + sessionManager.getStoreId() + "?pageNumber=1&pageSize=10";
        Log.e("ProductsURL", newArrivalURL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, newArrivalURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.optJSONArray("data");
                            if (dataArray != null) {

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject collectionObj = dataArray.optJSONObject(i);
                                    if (collectionObj != null) {
                                        String collectionId = collectionObj.optString("_id", null);
                                        String collectionName = collectionObj.optString("name", null);
                                        collectionIdsArrayList.add(new AllCollectionsModel(collectionId,collectionName));
                                    }
                                }
                                if (!isAdded()){
                                    progressBarDialog.dismiss();
                                    return;
                                }
                                setCollectionsAndProducts();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void setCollectionsAndProducts() {
        for (int i = 0; i < collectionIdsArrayList.size(); i++) {
            String collectionId = collectionIdsArrayList.get(i).getCollectionId();
            // 1. Create title TextView
            String title = collectionIdsArrayList.get(i).getCollectionName();
            TextView titleView = new TextView(requireContext());
            titleView.setText(title);
            titleView.setTextSize(22);
            titleView.setTypeface(null, Typeface.BOLD);
            titleView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            titleView.setPadding(16, 32, 16, 8);

            // 2. Create RecyclerView
            RecyclerView recyclerView = new RecyclerView(getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            recyclerView.setPadding(10, 0, 10, 20);
            // 3. Add views to layout first
            collectionsContainer.addView(titleView);
            collectionsContainer.addView(recyclerView);
            // 4. Fetch products for this collection
            fetchProductsForCollection(collectionId, recyclerView);
        }
        progressBarDialog.dismiss();
        mainLayout.setVisibility(View.VISIBLE);
    }

    private void fetchProductsForCollection(String collectionId, RecyclerView recyclerView) {
        ArrayList<ProductDetailsModel> productsRecyclerModelArrayList = new ArrayList<>();

        String newArrivalURL = Constant.BASE_URL + "collection/products/" + collectionId;
        Log.e("ProductsURL", newArrivalURL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, newArrivalURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray dataArray = data.optJSONArray("products");
                            if (dataArray != null) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject productObj = dataArray.optJSONObject(i);
                                    if (productObj != null) {
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

                                        productsRecyclerModelArrayList.add(new ProductDetailsModel(productId, title, slug, MRP, price,
                                                discountAmount, discountPercentage, stock, description, tags, SKU, store,
                                                category, inputTag, "4", 0, imagesList));
                                    }
                                }
                                if (changingWishListIcon(productsRecyclerModelArrayList)) {
                                    ProductRecyclerForFragmentAdapter adapter = new ProductRecyclerForFragmentAdapter(productsRecyclerModelArrayList,HomeFragment.this);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("JSONError", "Parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
    private boolean changingWishListIcon(ArrayList<ProductDetailsModel> productsRecyclerModelArrayList) {
        ArrayList<ProductDetailsModel> wishlistItem = new ArrayList<>();
        wishlistItem = sessionManager.getWishList();
        Log.e("wishlist",wishlistItem.toString());
        for (int i = 0; i < productsRecyclerModelArrayList.size(); i++) {
            for (int j = 0; j < wishlistItem.size(); j++) {
                if (productsRecyclerModelArrayList.get(i).getProductId().equals(wishlistItem.get(j).getProductId())) {
                    productsRecyclerModelArrayList.get(i).setWishListImgToggle(1);
                    Log.e("changing","true");
                }
            }
        }
        return true;
    }

}

package com.example.whitelabeltemplate3.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.whitelabeltemplate3.Adapters.ProductRecyclerForActivityAdapter;
import com.example.whitelabeltemplate3.Adapters.ProductRecyclerForFragmentAdapter;
import com.example.whitelabeltemplate3.Adapters.SearchingFragAdapter;
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

public class SearchFragment extends Fragment {
    private EditText searchView;
    ImageView backBtn;
    SessionManager sessionManager;
    String authToken;
    RecyclerView searchingRecycler;
    SearchingFragAdapter productRecyclerForActivityAdapter;
    ArrayList<ProductDetailsModel> casualDressArrayList = new ArrayList<>();
    RelativeLayout noDataLayout;
    ProgressBar nextItemLoadingProgressBar;
    NestedScrollView dressNestedScroll;
    String searchQuery = "";
    int itemPerPage = 15, totalPages = 1,currentPage = 1;
    Dialog progressBarDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        // Initialize the SearchView
        searchView = view.findViewById(R.id.searchView);
        backBtn = view.findViewById(R.id.imgBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        progressBarDialog = new Dialog(getContext());
        progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBarDialog.setContentView(R.layout.progress_bar_dialog);
        progressBarDialog.setCancelable(false);
        progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressBarDialog.show();

        // Add a small delay to ensure the SearchView is initialized before requesting focus
        new Handler().postDelayed(() -> {
            searchView.requestFocus();
        }, 100);  // Adjust the delay if needed

        searchingRecycler = view.findViewById(R.id.productRecyclerView);
        nextItemLoadingProgressBar = view.findViewById(R.id.nextItemLoadingProgressBar);
        dressNestedScroll = view.findViewById(R.id.dressNestedScroll);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        noDataLayout.setVisibility(View.GONE);
        searchingRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        getNewArrivalProducts();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchQuery = s.toString().trim();
                currentPage = 1;
                casualDressArrayList.clear();
                getNewArrivalProducts(); // This is your API call or product-fetching logic
            }
        });

        dressNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Check if we are near the bottom, but leave a small threshold to avoid issues with small screens
                Log.e("ScrollDebug", "scrollY: " + scrollY +
                        " measuredHeight: " + v.getChildAt(0).getMeasuredHeight() +
                        " scrollHeight: " + v.getMeasuredHeight());
                int scrollThreshold = 50; // threshold to trigger load more data
                int diff = (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) - scrollY;
                Log.e("ScrollDebug", "diff: " + diff);
                // Check if we have scrolled to the bottom or near bottom
                if (diff <= scrollThreshold && currentPage <= totalPages) {
                    // Only increment the page and load more data if there's more data to load
                    currentPage++;
                    nextItemLoadingProgressBar.setVisibility(View.VISIBLE);
                    getNewArrivalProducts();
                    Log.e("Scroll","Scroll Happened");
                }else {
                    nextItemLoadingProgressBar.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }
    private void getNewArrivalProducts() {
        String newArrivalURL = Constant.BASE_URL + "product/" + sessionManager.getStoreId() + "?searchQuery=" + searchQuery + "&pageNumber="
                + currentPage + "&pageSize=" + itemPerPage;
        Log.e("ProductsURL", newArrivalURL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, newArrivalURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.optJSONArray("data");
                            if (dataArray != null) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject productObj = dataArray.getJSONObject(i);

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

                                    ProductDetailsModel model = new ProductDetailsModel(
                                            productId, title, slug, MRP, price, discountAmount, discountPercentage,
                                            stock, description, tags, SKU, store, category, inputTag, "4", 0, imagesList);
                                    if (!isProductAlreadyInList(productId)) {
                                        casualDressArrayList.add(model);
                                    }
                                }
                            }
                            Log.e("Array List", casualDressArrayList.toString());
                            if (!casualDressArrayList.isEmpty()) {
                                if (changingWishListIcon())
                                    if (productRecyclerForActivityAdapter == null) {
                                        productRecyclerForActivityAdapter = new SearchingFragAdapter(casualDressArrayList, getContext());
                                        searchingRecycler.setAdapter(productRecyclerForActivityAdapter);
                                        nextItemLoadingProgressBar.setVisibility(View.GONE);
                                        dressNestedScroll.setVisibility(View.VISIBLE);
                                        noDataLayout.setVisibility(View.GONE);
                                        progressBarDialog.dismiss();
                                    }else {
                                        productRecyclerForActivityAdapter.notifyDataSetChanged();
                                        noDataLayout.setVisibility(View.GONE);
                                        dressNestedScroll.setVisibility(View.VISIBLE);
                                        searchingRecycler.setVisibility(View.VISIBLE);
                                        nextItemLoadingProgressBar.setVisibility(View.GONE);
                                        progressBarDialog.dismiss();
                                    }
                            } else {
                                noDataLayout.setVisibility(View.VISIBLE);
                                searchingRecycler.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONParsingError", "Error parsing response: " + e.getMessage());
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
    private boolean isProductAlreadyInList(String productId) {
        for (ProductDetailsModel model : casualDressArrayList) {
            if (model.getProductId().equals(productId)) {
                return true;
            }
        }
        return false;
    }
    private boolean changingWishListIcon() {
        ArrayList<ProductDetailsModel> wishlistItem = new ArrayList<>();
        wishlistItem = sessionManager.getWishList();
        Log.e("wish",wishlistItem.toString());
        for (int i = 0; i < casualDressArrayList.size(); i++) {
            for (int j = 0; j < wishlistItem.size(); j++) {
                if (casualDressArrayList.get(i).getProductId().equals(wishlistItem.get(j).getProductId())) {
                    casualDressArrayList.get(i).setWishListImgToggle(1);
                }
            }
        }
        return true;
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

    // Method to open the keyboard
//    private void openKeyboard(View view) {
//        // Get the InputMethodManager
//        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
//        }
//
//        // Ensure the keyboard stays visible
//        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//    }
}

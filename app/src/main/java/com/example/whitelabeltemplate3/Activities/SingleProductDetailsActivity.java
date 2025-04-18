package com.example.whitelabeltemplate3.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.whitelabeltemplate3.Adapters.BookImageAdapter;
import com.example.whitelabeltemplate3.Adapters.ProductRecyclerForActivityAdapter;
import com.example.whitelabeltemplate3.Models.CartItemModel;
import com.example.whitelabeltemplate3.Models.ProductDetailsModel;
import com.example.whitelabeltemplate3.Models.ProductImagesModel;
import com.example.whitelabeltemplate3.R;
import com.example.whitelabeltemplate3.Utils.Constant;
import com.example.whitelabeltemplate3.Utils.MySingleton;
import com.example.whitelabeltemplate3.Utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleProductDetailsActivity extends AppCompatActivity {
    ImageView backBtn, searchBtn,wishlistRLBtn,wishlistToggleBtn,cartRLBtn,quantityPlusIV, quantityMinusIV;
    ViewPager2 productImg;
    LinearLayout dotLayout;
    boolean wishlistToggle = false;
    TextView productTitleTxt, productPriceTxt, productDiscountTxt,productPriceStrikeThroughTxt,quantityTxt,wishlistItemCountTxt,cartItemCountTxt;
    WebView descriptionWebView;
    int quantityInt = 1;
    RecyclerView newProductRecyclerView,popularProductRecyclerView;
    ArrayList<ProductDetailsModel> singleProductArrayList,productsRecyclerModelArrayList;
    NestedScrollView nestedScrollView;
    MaterialCardView viewAllCard1,viewAllCard2;
    Button addToCartBtn;
    SessionManager sessionManager;
    String authToken;
    Dialog progressBarDialog;
    String productIdStr,productNameStr,productRatingStr, productDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_details);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            EdgeToEdge.enable(this);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
//        getWindow().setStatusBarColor(ContextCompat.getColor(SingleProductDetailsActivity.this, R.color.white));

        sessionManager = new SessionManager(SingleProductDetailsActivity.this);
        authToken = sessionManager.getUserData().get("authToken");

        progressBarDialog = new Dialog(SingleProductDetailsActivity.this);
        progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBarDialog.setContentView(R.layout.progress_bar_dialog);
        progressBarDialog.setCancelable(false);
        progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressBarDialog.show();

        backBtn = findViewById(R.id.imgBack);
        searchBtn = findViewById(R.id.searchBtn);
        wishlistRLBtn = findViewById(R.id.wishlistBtn);
        wishlistToggleBtn = findViewById(R.id.wishlistToggleBtn);
        cartRLBtn = findViewById(R.id.cartBtn);
        productImg = findViewById(R.id.productImgMain);
        dotLayout = findViewById(R.id.indicatorLayout);
        productTitleTxt = findViewById(R.id.productTitleTxt);
        productPriceTxt = findViewById(R.id.productPriceTxt);
        productDiscountTxt = findViewById(R.id.productDiscountTxt);
        productPriceStrikeThroughTxt = findViewById(R.id.productPriceStrikeThroughTxt);
        nestedScrollView = findViewById(R.id.mainNestedLayout);
        nestedScrollView.setVisibility(View.GONE);
        descriptionWebView = findViewById(R.id.productionDescriptionWebView);

        viewAllCard1 = findViewById(R.id.viewAllCard1);
        viewAllCard2 = findViewById(R.id.viewAllCard2);

        addToCartBtn = findViewById(R.id.addToCartBtn);

        productIdStr = getIntent().getStringExtra("productId");

        wishlistItemCountTxt = findViewById(R.id.wishlistItemCountTxt);
        cartItemCountTxt = findViewById(R.id.cartItemCountTxt);

        setItemsCountTxt();

        quantityPlusIV = findViewById(R.id.quantityPlusTxt);
        quantityMinusIV = findViewById(R.id.quantityMinusTxt);
        quantityTxt = findViewById(R.id.quantityDisplayTxt);

        quantityTxt.setText(String.valueOf(quantityInt));

        quantityPlusIV.setOnClickListener(v -> {
            quantityInt++;
            quantityTxt.setText(String.valueOf(quantityInt));
        });
        quantityMinusIV.setOnClickListener(v -> {
            if (quantityInt > 1) {
                quantityInt--;
                quantityTxt.setText(String.valueOf(quantityInt));
            }
        });

        newProductRecyclerView = findViewById(R.id.newProductRecyclerView);
        popularProductRecyclerView = findViewById(R.id.popularProductsRecyclerView);
        newProductRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        popularProductRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        productsRecyclerModelArrayList = new ArrayList<>();
        singleProductArrayList = new ArrayList<>();

        ArrayList<ProductDetailsModel> wishlistItem = sessionManager.getWishList();
        for (int i = 0; i < wishlistItem.size(); i++) {
            for (int j = 0; j < wishlistItem.size(); j++) {
                if (wishlistItem.get(i).getProductId().equals(productIdStr)) {
                    wishlistToggleBtn.setImageResource(R.drawable.ic_heart_red);
                    wishlistToggle = true;
                }else {
                    wishlistToggleBtn.setImageResource(R.drawable.ic_heart_grey);
                    wishlistToggle = false;
                }
            }
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleProductDetailsActivity.this, HomePageActivity.class);
                intent.putExtra("frag" , "search");
                startActivity(intent);
            }
        });
        wishlistToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishlistToggleBtn.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(150)
                        .withEndAction(() -> {
                            wishlistToggleBtn.animate().scaleX(1f).scaleY(1f).start();
                        }).start();
                if (sessionManager.isLoggedIn()) {
                    if (!wishlistToggle) {
                        addToWishList();
                        wishlistToggleBtn.setImageResource(R.drawable.ic_heart_red);
                        wishlistToggle = true;
                        setItemsCountTxt();
                    } else {
                        removeFromWishList();
                        wishlistToggleBtn.setImageResource(R.drawable.ic_heart_grey);
                        wishlistToggle = false;
                        setItemsCountTxt();
                    }
                }else {
                    if (!wishlistToggle) {
                        sessionManager.saveWishList(singleProductArrayList.get(0));
                        wishlistToggleBtn.setImageResource(R.drawable.ic_heart_red);
                        wishlistToggle = true;
                        setItemsCountTxt();
                        Toast.makeText(SingleProductDetailsActivity.this, "Item added to WishList", Toast.LENGTH_SHORT).show();
                    } else {
                        sessionManager.removeWishListItem(productIdStr);
                        wishlistToggleBtn.setImageResource(R.drawable.ic_heart_grey);
                        wishlistToggle = false;
                        setItemsCountTxt();
                        Toast.makeText(SingleProductDetailsActivity.this, "Item removed from WishList", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        cartRLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleProductDetailsActivity.this, HomePageActivity.class);
                intent.putExtra("frag" , "cart");
                startActivity(intent);
            }
        });
        wishlistRLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleProductDetailsActivity.this, HomePageActivity.class);
                intent.putExtra("frag" , "wish");
                startActivity(intent);
            }
        });
        viewAllCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingleProductDetailsActivity.this, HomePageActivity.class));
            }
        });
        viewAllCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingleProductDetailsActivity.this, HomePageActivity.class));
            }
        });

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sessionManager.isLoggedIn()){
                    sessionManager.saveCart(new CartItemModel(null,productIdStr,
                            singleProductArrayList.get(0).getProductTitle(),
                            String.valueOf(quantityInt),singleProductArrayList.get(0).getSlug(),
                            singleProductArrayList.get(0).getProductMRP(),
                            singleProductArrayList.get(0).getProductPrice(),
                            singleProductArrayList.get(0).getDiscountAmount(),
                            singleProductArrayList.get(0).getDiscountPercentage(),
                            singleProductArrayList.get(0).getStock(),
                            singleProductArrayList.get(0).getDescription(),
                            singleProductArrayList.get(0).getTags(),
                            singleProductArrayList.get(0).getProductSKU(),
                            singleProductArrayList.get(0).getStore(),
                            singleProductArrayList.get(0).getCategory(),
                            singleProductArrayList.get(0).getInputTag(),
                            "4.5f", 0,
                            singleProductArrayList.get(0).getProductImagesModelsArrList()));
                    Toast.makeText(SingleProductDetailsActivity.this, "Product Added to cart", Toast.LENGTH_SHORT).show();
                }else {
                    addToCart();
                }
            }
        });
        getProductById();
        getSuggestionProducts();
    }

    private void setItemsCountTxt() {
        int cartQuantity = sessionManager.getCartCount();
        if (cartQuantity != 0) {
            cartItemCountTxt.setVisibility(View.VISIBLE);
            cartItemCountTxt.setText(String.valueOf(cartQuantity));
        }else {
            cartItemCountTxt.setVisibility(View.GONE);
        }
        int wishlistQuantity = sessionManager.getWishListCount();
        if (wishlistQuantity != 0) {
            wishlistItemCountTxt.setVisibility(View.VISIBLE);
            wishlistItemCountTxt.setText(String.valueOf(wishlistQuantity));
        }else {
            wishlistItemCountTxt.setVisibility(View.GONE);
        }
    }
    private void removeFromWishList() {
        String orderURL = Constant.BASE_URL + "wishlist/remove/" + productIdStr;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, orderURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(SingleProductDetailsActivity.this, "Item removed from wishlist", Toast.LENGTH_SHORT).show();
                        sessionManager.removeWishListItem(productIdStr);
                        sessionManager.getWishlistFromServer();
                        setItemsCountTxt();
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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void addToWishList() {
        String orderURL = Constant.BASE_URL + "wishlist";
        String userId = sessionManager.getUserData().get("userId");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId", productIdStr);
            jsonObject.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, orderURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(SingleProductDetailsActivity.this, "Item added to wishlist", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SingleProductDetailsActivity.this, message, Toast.LENGTH_LONG).show();
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
    private void addToCart() {
        String newArrivalURL = Constant.BASE_URL + "cart";
        Log.e("addTCartURL", newArrivalURL);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId", productIdStr);
            jsonObject.put("quantity", quantityInt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, newArrivalURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String message = response.optString("message", null);
                        Toast.makeText(SingleProductDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        sessionManager.getCartFromServer();
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
                                Toast.makeText(SingleProductDetailsActivity.this, message, Toast.LENGTH_LONG).show();
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
    private void getProductById() {
        String productURL = Constant.BASE_URL + "product/productById/" + productIdStr;
        Log.e("ProductsURL", productURL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, productURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject dataObj = response.optJSONObject("data");
                            if (dataObj != null) {

                                String productId = dataObj.optString("_id", null);
                                String title = dataObj.optString("title", null);
//
                                JSONObject slugObj = dataObj.optJSONObject("meta");
                                String slug = (slugObj != null) ? slugObj.optString("slug", null) : null;

                                String MRP = dataObj.optString("MRP", null);
                                String price = dataObj.optString("price", null);

                                JSONObject discountObj = dataObj.optJSONObject("discount");
                                String discountAmount = (discountObj != null) ? discountObj.optString("amount", null) : null;
                                String discountPercentage = (discountObj != null) ? discountObj.optString("percentage", null) : null;

                                String stock = dataObj.optString("stock", null);
                                String description = dataObj.optString("description", null);

                                JSONArray tagsArray = dataObj.optJSONArray("tags");
                                String tags = (tagsArray != null) ? parseTags(tagsArray) : null;

                                String SKU = dataObj.optString("SKU", null);

                                // Handling Images
                                ArrayList<ProductImagesModel> imagesList = new ArrayList<>();
                                JSONArray imageArray = dataObj.optJSONArray("images");
                                if (imageArray != null) {
                                    for (int j = 0; j < imageArray.length(); j++) {
                                        String imageUrl = imageArray.optString(j, null);
                                        if (imageUrl != null) {
                                            Log.e("JSONIMG", imageUrl);
                                            imagesList.add(new ProductImagesModel(imageUrl));
                                        }
                                    }
                                }

                                String store = dataObj.optString("store", null);
                                String category = dataObj.optString("category", null);
                                String inputTag = dataObj.optString("inputTag", null);

                                singleProductArrayList.add(new ProductDetailsModel(
                                        productId, title, slug, MRP, price, discountAmount, discountPercentage,
                                        stock, description, tags, SKU, store, category, inputTag, "4", 0, imagesList
                                ));
                            }
                            setProductDetails();
//                            }
//
//                            if (!casualDressArrayList.isEmpty()) {
//                                casualDressRecyclerView.setAdapter(new CasualMensClothsForActivityAdapter(casualDressArrayList, MensCasualClothesActivity.this));
//                            }
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
                                Toast.makeText(SingleProductDetailsActivity.this, message, Toast.LENGTH_LONG).show();
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
    SpannableStringBuilder spannableText;
    private void setProductDetails() {
        ProductDetailsModel product = singleProductArrayList.get(0);

        // Set product name
        productNameStr = product.getProductTitle();
        productRatingStr = product.getProductRating();

        productTitleTxt.setText(productNameStr);
        productTitleTxt.setEllipsize(TextUtils.TruncateAt.END);
        productTitleTxt.setMaxLines(2);

        // Set product description in WebView
        productDescription = product.getDescription();
        descriptionWebView.loadData(productDescription, "text/html", "UTF-8");

//        // Load main product image
//        Glide.with(this)
//                .load(product.getProductImagesModelsArrList().get(0).getProductImage())
//                .error(R.drawable.no_image)
//                .into(productImg);
        BookImageAdapter bookImageAdapter = new BookImageAdapter(product.getProductImagesModelsArrList(),productImg,dotLayout);
        productImg.setAdapter(bookImageAdapter);

        // Set price and discount
        if (!product.getDiscountAmount().equals("0")) {
            String originalPrice = product.getProductMRP();
            String disPercent = product.getDiscountPercentage();
            String sellingPrice = product.getProductPrice();

            // Strikethrough for original price
            SpannableString spannableOriginalPrice = new SpannableString("₹" + originalPrice);
            spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);

            // Combine selling price and original price
            spannableText = new SpannableStringBuilder();
            spannableText.append("₹").append(sellingPrice).append(" ");
            productPriceStrikeThroughTxt.setText(spannableOriginalPrice);

            // Apply to price TextView
            productPriceTxt.setText(spannableText);

            // Set discount percent separately (you can show this in a separate TextView like: productDiscountTxt)
            String discountText = "(-" + disPercent + "%)";

            // Example usage if you have a separate TextView for discount
            productDiscountTxt.setVisibility(View.VISIBLE);
            productDiscountTxt.setText(discountText);
        } else {
            productPriceTxt.setText("₹" + product.getProductPrice());

            // Hide discount text if no discount
            productDiscountTxt.setVisibility(View.GONE);
        }

        // Load all images
        ArrayList<String> images = new ArrayList<>();
        for (ProductImagesModel imgModel : product.getProductImagesModelsArrList()) {
            images.add(imgModel.getProductImage());
        }

        // If you have the adapter ready, set it
        // productAllImagesRecycler.setAdapter(new AllImagesRecyclerAdapter(images, SingleProductDetailsActivity.this));

        progressBarDialog.dismiss();
        nestedScrollView.setVisibility(View.VISIBLE);
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
    private void getSuggestionProducts() {
        String newArrivalURL = Constant.BASE_URL + "product/" + sessionManager.getStoreId() + "?pageNumber=1&pageSize=5";
        Log.e("ProductsURL",newArrivalURL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, newArrivalURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject productObj = dataArray.getJSONObject(i);
                                String productId = productObj.getString("_id");
                                String title = productObj.getString("title");

                                JSONObject slugObj = productObj.getJSONObject("meta");
                                String slug = slugObj.getString("slug");

                                String MRP = productObj.getString("MRP");
                                String price = productObj.getString("price");

                                JSONObject discountObj = productObj.getJSONObject("discount");
                                String discountAmount = discountObj.getString("amount");
                                String discountPercentage = discountObj.getString("percentage");

                                String stock = productObj.getString("stock");
                                String description = productObj.getString("description");

                                String tags = parseTags(productObj.getJSONArray("tags"));

                                String SKU = productObj.getString("SKU");

                                ArrayList<ProductImagesModel> imagesList = new ArrayList<>();
                                JSONArray imageArray = productObj.getJSONArray("images");
                                for (int j = 0; j < imageArray.length(); j++) {
                                    String imageUrl = imageArray.getString(j);
                                    Log.e("JSONIMG",imageUrl);
                                    imagesList.add(new ProductImagesModel(imageUrl));
                                }

                                String store = productObj.getString("store");
                                String category = productObj.getString("category");
                                String inputTag = productObj.getString("inputTag");

                                productsRecyclerModelArrayList.add(new ProductDetailsModel(productId,title,slug,MRP,price,
                                        discountAmount,discountPercentage,stock,description,tags,SKU,store,
                                        category,inputTag,"4",0,imagesList));
                            }
                            if (!productsRecyclerModelArrayList.isEmpty()){
                                newProductRecyclerView.setAdapter(new ProductRecyclerForActivityAdapter(productsRecyclerModelArrayList, SingleProductDetailsActivity.this));
                                popularProductRecyclerView.setAdapter(new ProductRecyclerForActivityAdapter(productsRecyclerModelArrayList, SingleProductDetailsActivity.this));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        progressBarDialog.dismiss();
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
                                Toast.makeText(SingleProductDetailsActivity.this, message, Toast.LENGTH_LONG).show();
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
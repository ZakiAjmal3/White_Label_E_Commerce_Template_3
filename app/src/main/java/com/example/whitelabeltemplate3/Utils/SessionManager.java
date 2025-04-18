package com.example.whitelabeltemplate3.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.whitelabeltemplate3.Models.CartItemModel;
import com.example.whitelabeltemplate3.Models.ProductDetailsModel;
import com.example.whitelabeltemplate3.Models.ProductImagesModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionManager {
    private Context ctx;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private static final String PREF_NAME = "SessionPrefs";
    private static final String DEFAULT_VALUE = "DEFAULT";
    private static final String KEY_STORE_ID = "storeId";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_AUTH_TOKEN = "authToken";
    public static final String IS_LOGGED_IN = "IsLoggedIn";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String WISHLIST_KEY = "wishlist";
    private static final String CART_KEY = "cart";

    public SessionManager(Context context) {
        this.ctx = context;
        this.sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        this.gson = new Gson();
    }

    public HashMap<String, String> getUserData() {
        HashMap<String, String> user = new HashMap<>();
        user.put("userId", sharedPreferences.getString(KEY_USER_ID, DEFAULT_VALUE));
        user.put("authToken", sharedPreferences.getString(KEY_AUTH_TOKEN, DEFAULT_VALUE));
        user.put("fullName", sharedPreferences.getString(KEY_FULL_NAME, DEFAULT_VALUE));
        user.put("email", sharedPreferences.getString(KEY_EMAIL, DEFAULT_VALUE));
        user.put("role", sharedPreferences.getString(KEY_ROLE, DEFAULT_VALUE));
        return user;
    }

    public String getStoreId() {
//        return sharedPreferences.getString(KEY_STORE_ID, "67e2327769b20acb65bd7c62");
        return sharedPreferences.getString(KEY_STORE_ID, "67f79a2246b9a0fb595ab9a0");
    }

    public void saveAuthToken(String userId, String authToken, String storeId) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putString(KEY_STORE_ID, storeId);
        editor.apply();
    }

    public void saveUserDetails(String fullName, String email, String role) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        boolean isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN, false);
        Log.d("SessionManager", "IsLoggedIn: " + isLoggedIn);
        return isLoggedIn;
    }

    public void logout() {
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_FULL_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_ROLE);
        editor.remove(IS_LOGGED_IN);
        editor.apply();
        Log.d("SessionManager", "Logged out, session cleared.");
    }
    public void saveWishList(ProductDetailsModel wishListModel){

        ArrayList<ProductDetailsModel> wishList = getWishList(); // Retrieve the existing list
        wishList.add(wishListModel); // Add new item

        String json = gson.toJson(wishList); // Convert to JSON
        editor.putString(WISHLIST_KEY, json);
        editor.apply();
    }
    public void saveCart(CartItemModel cartItemModel){

        ArrayList<CartItemModel> cartItemModels = getCart(); // Retrieve the existing list
        cartItemModels.add(cartItemModel); // Add new item

        String json = gson.toJson(cartItemModels); // Convert to JSON
        editor.putString(CART_KEY, json);
        editor.apply();
    }

    // Get cart
    public ArrayList<CartItemModel> getCart() {
        String json = sharedPreferences.getString(CART_KEY, null);
        Type type = new TypeToken<ArrayList<CartItemModel>>() {}.getType();

        if (json != null) {
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>(); // Return empty list if no data found
        }
    }
    // Get wishlist
    public ArrayList<ProductDetailsModel> getWishList() {
        String json = sharedPreferences.getString(WISHLIST_KEY, null);
        Type type = new TypeToken<ArrayList<ProductDetailsModel>>() {}.getType();

        if (json != null) {
            Log.e("returning","fill");
            return gson.fromJson(json, type);
        } else {
            Log.e("returning","empty");
            return new ArrayList<>(); // Return empty list if no data found
        }
    }
    // Remove a single item from cart
    public void removeCartItem(String id) {
        ArrayList<CartItemModel> cartList = getCart();

        // Remove item using an Iterator (compatible with all Android versions)
        for (int i = 0; i < cartList.size(); i++) {
            if (cartList.get(i).getProductId().equals(id)) {
                cartList.remove(i);
                break; // Exit loop after removing the first matching item
            }
        }

        // Save updated list back to SharedPreferences
        String json = gson.toJson(cartList);
        editor.putString(CART_KEY, json);
        editor.apply();
    }
//    // Remove a single item from wishlist
    public void removeWishListItem(String id) {
        ArrayList<ProductDetailsModel> wishList = getWishList();

        // Remove item using an Iterator (compatible with all Android versions)
        for (int i = 0; i < wishList.size(); i++) {
            if (wishList.get(i).getProductId().equals(id)) {
                wishList.remove(i);
                break; // Exit loop after removing the first matching item
            }
        }

        // Save updated list back to SharedPreferences
        String json = gson.toJson(wishList);
        editor.putString(WISHLIST_KEY, json);
        editor.apply();
    }
    // Clear wishlist
    public void clearWishList() {
        editor.remove(WISHLIST_KEY);
        editor.apply();
    }
    public void clearCart() {
        editor.remove(CART_KEY);
        editor.apply();
    }
    public void getWishlistFromServer(){
        editor.remove(WISHLIST_KEY);
        ArrayList<ProductDetailsModel> wishList = new ArrayList<>();
        String wishlistURL = Constant.BASE_URL + "wishlist";
        Log.e("ProductsURL", wishlistURL);

        if (isLoggedIn()) {
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

                                        wishList.add(new ProductDetailsModel(
                                                productId, title, slug, MRP, price, discountAmount, discountPercentage,
                                                stock, description, null, SKU, store, category, inputTag, "4", 1, imagesList
                                        ));
                                    }
                                    editor.remove(WISHLIST_KEY  );
                                    String json = gson.toJson(wishList); // Convert to JSON
                                    editor.putString(WISHLIST_KEY, json);
                                    editor.apply();
                                }
                            } catch (JSONException e) {
//                            progressBarDialog.dismiss();
                                e.printStackTrace();
                                Log.e("JSONParsingError", "Error parsing response: " + e.getMessage());
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
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonObject = new JSONObject(jsonError);
                                    String message = jsonObject.optString("message", "Unknown error");
//                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
                    headers.put("Authorization", "Bearer " + getUserData().get("authToken"));
                    return headers;
                }
            };
            MySingleton.getInstance(ctx.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }
    }
    public void startAddingItemToCart(){
        ArrayList<CartItemModel> cart = new ArrayList<>();
        cart = getCart();

        ExecutorService executor = Executors.newFixedThreadPool(3); // Run 3 APIs in parallel

        for (CartItemModel item : cart) {
            executor.execute(() -> sendCartItemToServer(item.getProductId()));
        }

        executor.shutdown(); // Shutdown after execution
    }
    private void sendCartItemToServer(String productId) {
        String wishlistURL = Constant.BASE_URL + "cart";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("productId", productId);
            requestBody.put("quantity", getUserData().get("userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, wishlistURL, requestBody,
                response -> Log.d("WishlistAPI", "Added: " + productId),
                error -> Log.e("WishlistAPI_Error", "Failed for: " + productId + " | Error: " + error.toString())) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + getUserData().get("authToken"));
                return headers;
            }
        };

        MySingleton.getInstance(ctx.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
    public void startAddingItemToWishlist(){
        ArrayList<ProductDetailsModel> wishList = new ArrayList<>();
        wishList = getWishList();

        ExecutorService executor = Executors.newFixedThreadPool(3); // Run 3 APIs in parallel

        for (ProductDetailsModel item : wishList) {
            executor.execute(() -> sendWishlistItemToServer(item.getProductId()));
        }

        executor.shutdown(); // Shutdown after execution
    }
    private void sendWishlistItemToServer(String productId) {
        String wishlistURL = Constant.BASE_URL + "wishlist";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("productId", productId);
            requestBody.put("userId", getUserData().get("userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, wishlistURL, requestBody,
                response -> Log.d("WishlistAPI", "Added: " + productId),
                error -> Log.e("WishlistAPI_Error", "Failed for: " + productId + " | Error: " + error.toString())) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + getUserData().get("authToken"));
                return headers;
            }
        };

        MySingleton.getInstance(ctx.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
    public void addCartToServer(){
        ArrayList<CartItemModel> cartList = new ArrayList<>();
        cartList = getCart();

        JSONArray wishListIds = new JSONArray();
        for (CartItemModel item : cartList) {
            wishListIds.put(item.getProductId());
        }
        String cartListURL = Constant.BASE_URL + "cart";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("productIds", wishListIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isLoggedIn()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, cartListURL, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            getCartFromServer();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                        progressBarDialog.dismiss();
                            String errorMessage = "Error: " + error.toString();
                            if (error.networkResponse != null) {
                                try {
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonObject = new JSONObject(jsonError);
                                    String message = jsonObject.optString("message", "Unknown error");
//                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
                    headers.put("Authorization", "Bearer " + getUserData().get("authToken"));
                    return headers;
                }
            };
            MySingleton.getInstance(ctx.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }
    }
    public void getCartFromServer() {
        ArrayList<CartItemModel> cartItemModelArrayList = new ArrayList<>();
        String cartURL = Constant.BASE_URL + "cart";
        Log.e("ProductsURL", cartURL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, cartURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            clearCart();
                            JSONObject dataObj = response.optJSONObject("data");
                            if (dataObj != null) {
                                String cartId = dataObj.optString("_id", null);
                                JSONArray itemArray = dataObj.optJSONArray("items");
                                if (itemArray != null) {
                                    for (int i = 0; i < itemArray.length(); i++) {
                                        JSONObject productObj0 = itemArray.optJSONObject(i);
                                        if (productObj0 != null) {
                                            String quantity = productObj0.optString("quantity", "1");

                                            JSONObject productObj = productObj0.optJSONObject("product");
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

                                                cartItemModelArrayList.add(new CartItemModel(cartId, productId, title, quantity,
                                                        slug, MRP, price, discountAmount, discountPercentage, stock, description,
                                                        null, SKU, store, category, inputTag, "4", 0, imagesList));
                                            }
                                        }
                                    }
                                }
                            }

                            String json = gson.toJson(cartItemModelArrayList);
                            editor.putString(CART_KEY, json);
                            editor.apply();
                        } catch (Exception e) {
                            Log.e("JSONError", "Unexpected error: " + e.getMessage());
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
                                Log.e("API Error", message);
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
                headers.put("Authorization", "Bearer " + getUserData().get("authToken"));
                return headers;
            }
        };

        MySingleton.getInstance(ctx.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public int getWishListCount() {
        return getWishList().size();
    }
    public int getCartCount() {
        return getCart().size();
    }
}

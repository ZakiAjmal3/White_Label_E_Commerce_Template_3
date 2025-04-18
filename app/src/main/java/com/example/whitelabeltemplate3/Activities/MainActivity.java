package com.example.whitelabeltemplate3.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class MainActivity extends AppCompatActivity {

    ImageView logo;
    SessionManager sessionManager;
    String authToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            EdgeToEdge.enable(this);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
//        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));

        sessionManager = new SessionManager(MainActivity.this);
        authToken = sessionManager.getUserData().get("authToken");
        if (sessionManager.isLoggedIn()) {
            sessionManager.startAddingItemToWishlist();
            sessionManager.startAddingItemToCart();
        }
        logo = findViewById(R.id.logo);

        Animation logo_object = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_activity_logo_rotation);
        logo.startAnimation(logo_object);

//        getColors();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                intent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();  // Finish the MainActivity to prevent back navigation
            }
        }, 1600);  // Match the duration of the logo animation
    }
//    public void getColors(){
//        String orderURL = Constant.BASE_URL + "store/" + sessionManager.getStoreId();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, orderURL, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        String errorMessage = "Error: " + error.toString();
//                        if (error.networkResponse != null) {
//                            try {
//                                // Parse the error response
//                                String jsonError = new String(error.networkResponse.data);
//                                JSONObject jsonObject = new JSONObject(jsonError);
//                                String message = jsonObject.optString("message", "Unknown error");
//                                // Now you can use the message
//                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        Log.e("ExamListError", errorMessage);
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "Bearer " + authToken);
//                return headers;
//            }
//        };
//        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
//    }
}
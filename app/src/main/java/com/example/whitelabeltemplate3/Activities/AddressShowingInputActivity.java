package com.example.whitelabeltemplate3.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.whitelabeltemplate3.Adapters.AddressItemAdapter;
import com.example.whitelabeltemplate3.Models.AddressItemModel;
import com.example.whitelabeltemplate3.R;
import com.example.whitelabeltemplate3.Utils.Constant;
import com.example.whitelabeltemplate3.Utils.MySingleton;
import com.example.whitelabeltemplate3.Utils.SessionManager;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressShowingInputActivity extends AppCompatActivity {
    String addAddressURL = Constant.BASE_URL + "address";
    RecyclerView addressRecyclerView;
    ArrayList<AddressItemModel> addressItemArrayList = new ArrayList<>();
    AddressItemAdapter addressItemAdapter;
    CardView addAddressBtn;
    ImageView backBtn;
    RelativeLayout noDataLayout;
    Dialog progressBarDialog;
    SessionManager sessionManager;
    String authToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_showing_input);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            EdgeToEdge.enable(this);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
//        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        sessionManager = new SessionManager(AddressShowingInputActivity.this);
        authToken = sessionManager.getUserData().get("authToken");
        backBtn = findViewById(R.id.imgMenu);
        noDataLayout = findViewById(R.id.noDataLayout);
        noDataLayout.setVisibility(View.GONE);
        addAddressBtn = findViewById(R.id.addAddressBtn);
        addressRecyclerView = findViewById(R.id.addressRecyclerView);
        addressRecyclerView.setVisibility(View.GONE);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBarDialog = new Dialog(AddressShowingInputActivity.this);
        progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBarDialog.setContentView(R.layout.progress_bar_dialog);
        progressBarDialog.setCancelable(false);
        progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressBarDialog.show();
        getAllAddress();

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAddressDialog(0,false);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (getIntent().getBooleanExtra("openAddAddress",false)){
            showAddAddressDialog(0,false);
        }
    }
    private void getAllAddress() {
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
                            if (addressItemArrayList.isEmpty()) {
                                noDataLayout.setVisibility(View.VISIBLE);
                                addressRecyclerView.setVisibility(View.GONE);
                                progressBarDialog.dismiss();
                            } else {
                                if (addressItemAdapter != null){
                                    addressItemAdapter.notifyDataSetChanged();
                                    progressBarDialog.dismiss();
                                }else {
                                    noDataLayout.setVisibility(View.GONE);
                                    addressRecyclerView.setVisibility(View.VISIBLE);
                                    addressItemAdapter = new AddressItemAdapter(addressItemArrayList, AddressShowingInputActivity.this);
                                    addressRecyclerView.setAdapter(addressItemAdapter);
                                    progressBarDialog.dismiss();
                                }
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
                                Toast.makeText(AddressShowingInputActivity.this, message, Toast.LENGTH_LONG).show();
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
    Dialog drawerDialog;
    ImageView crossBtn;
    Button saveBtn;
    TextInputLayout firstNameLayout, lastNameLayout, emailLayout, phoneLayout, apartmentLayout, streetLayout, cityLayout, pincodeLayout;
    EditText firstNameEditText, lastNameEditText, emailEditText, phoneEditText, apartmentEditText, streetEditText, cityEditText, pincodeEditText;
    Spinner genderSpinner,stateSpinner, countrySpinner;
    private final String[] stateArray = {
            "Select State","Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
            "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
            "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
            "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan",
            "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh",
            "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands",
            "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu", "Lakshadweep",
            "Delhi", "Puducherry"
    };
    private final String[] countryArray = {"Select Country","UK", "USA", "India"};
    private final String[] genderArray = {"Select your gender","Male", "Female", "Other"};
    String genderStr,stateStr, countryStr;
    @SuppressLint("ResourceAsColor")
    public void showAddAddressDialog(int position,boolean isEditing) {
        drawerDialog = new Dialog(AddressShowingInputActivity.this);
        drawerDialog.setContentView(R.layout.address_edit_dialog);
        drawerDialog.setCancelable(true);

        crossBtn = drawerDialog.findViewById(R.id.crossBtn);

        saveBtn = drawerDialog.findViewById(R.id.saveBtn);
        firstNameLayout = drawerDialog.findViewById(R.id.firstNameLayout);
        lastNameLayout = drawerDialog.findViewById(R.id.lastNameLayout);
        emailLayout = drawerDialog.findViewById(R.id.emailNameLayout);
        phoneLayout = drawerDialog.findViewById(R.id.phoneLayout);
        apartmentLayout = drawerDialog.findViewById(R.id.apartmentLayout);
        streetLayout = drawerDialog.findViewById(R.id.streetAddressLayout);
        cityLayout = drawerDialog.findViewById(R.id.cityLayout);
        pincodeLayout = drawerDialog.findViewById(R.id.pinCodeLayout);

        firstNameEditText = drawerDialog.findViewById(R.id.firstNameEditText);
        lastNameEditText = drawerDialog.findViewById(R.id.lastNameEditText);
        emailEditText = drawerDialog.findViewById(R.id.emailEditText);
        phoneEditText = drawerDialog.findViewById(R.id.phoneEditText);
        apartmentEditText = drawerDialog.findViewById(R.id.apartmentEditText);
        streetEditText = drawerDialog.findViewById(R.id.streetAddressEditText);
        cityEditText = drawerDialog.findViewById(R.id.cityEditText);
        pincodeEditText = drawerDialog.findViewById(R.id.pinCodeEditText);

        genderSpinner = drawerDialog.findViewById(R.id.genderSpinner);
        stateSpinner = drawerDialog.findViewById(R.id.stateSpinner);
        countrySpinner = drawerDialog.findViewById(R.id.countrySpinner);

        if (!addressItemArrayList.isEmpty() && isEditing){
            firstNameEditText.setText(addressItemArrayList.get(position).getFirstName());
            lastNameEditText.setText(addressItemArrayList.get(position).getLastName());
            phoneEditText.setText(addressItemArrayList.get(position).getPhone());
            emailEditText.setText(addressItemArrayList.get(position).getEmail());
            apartmentEditText.setText(addressItemArrayList.get(position).getApartment());
            streetEditText.setText(addressItemArrayList.get(position).getStreet());
            cityEditText.setText(addressItemArrayList.get(position).getCity());
            pincodeEditText.setText(addressItemArrayList.get(position).getPincode());
            for (int i = 0; i < stateArray.length; i++) {
                if (stateArray[i].equals(addressItemArrayList.get(position).getState())) {
                    stateSpinner.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < countryArray.length; i++) {
                if (countryArray[i].equals(addressItemArrayList.get(position).getCountry())) {
                    countrySpinner.setSelection(i);
                    break;
                }
            }
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderArray);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter2);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryArray);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderStr = genderArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryStr = countryArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateStr = stateArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    firstNameLayout.setError(null);
                }else {
                    firstNameLayout.setError("First Name Required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    lastNameLayout.setError(null);
                }else {
                    lastNameLayout.setError("Last Name Required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    emailLayout.setError(null);
                }else {
                    emailLayout.setError("Email is Required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    phoneLayout.setError(null);
                }else {
                    phoneLayout.setError("Phone is Required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        apartmentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    apartmentLayout.setError(null);
                }else {
                    apartmentLayout.setError("Apartment Number is required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        streetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    streetLayout.setError(null);
                }else {
                    streetLayout.setError("Street address is required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    cityLayout.setError(null);
                }else {
                    cityLayout.setError("City is required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pincodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    pincodeLayout.setError(null);
                }else {
                    pincodeLayout.setError("Pin Code is required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarDialog = new Dialog(AddressShowingInputActivity.this);
                progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressBarDialog.setContentView(R.layout.progress_bar_dialog);
                progressBarDialog.setCancelable(false);
                progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressBarDialog.show();
                if(checkValidation()) {
                    submitAddress(position,isEditing);
//                    addressItemList.add(new AddressItemModel(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(), phoneEditText.getText().toString(), emailEditText.getText().toString(), apartmentEditText.getText().toString(), streetEditText.getText().toString(), cityEditText.getText().toString(), pincodeEditText.getText().toString(), genderStr,stateStr, countryStr));
//                    addressRecyclerView.setAdapter(new AddressItemAdapter(addressItemList, AddressShowingInputActivity.this));
//                    drawerDialog.dismiss();
                }else {
                    progressBarDialog.dismiss();
                }
            }
        });

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerDialog.dismiss();
            }
        });

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
    private void submitAddress(int position, boolean isEditing) {
        String firstName,lastName,email,phone,apartment,street,city,pincode;

        firstName = firstNameEditText.getText().toString().trim();
        lastName = lastNameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        phone = phoneEditText.getText().toString().trim();
        apartment = apartmentEditText.getText().toString().trim();
        street = streetEditText.getText().toString().trim();
        city = cityEditText.getText().toString().trim();
        pincode = pincodeEditText.getText().toString().trim();

        JSONObject userOBJ = new JSONObject();
        try {
            userOBJ.put("firstName",firstName);
            userOBJ.put("lastName",lastName);
            userOBJ.put("gender",genderStr);
            userOBJ.put("email",email);
            userOBJ.put("streetAddress",street);
            userOBJ.put("apartment",apartment);
            userOBJ.put("city",city);
            userOBJ.put("state",stateStr);
            userOBJ.put("pincode",pincode);
            userOBJ.put("country",countryStr);
            userOBJ.put("phone",phone);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (isEditing){
            addAddressURL = Constant.BASE_URL + "address/update/" + addressItemArrayList.get(position).getAddressId();
        }else {
            addAddressURL = Constant.BASE_URL + "address";
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addAddressURL, userOBJ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        drawerDialog.dismiss();
                        progressBarDialog.dismiss();
                        Toast.makeText(AddressShowingInputActivity.this, "Address added successfully", Toast.LENGTH_SHORT).show();

                        if (isEditing){
                            addressItemArrayList.get(position).setFirstName(firstName);
                            addressItemArrayList.get(position).setLastName(lastName);
                            addressItemArrayList.get(position).setEmail(email);
                            addressItemArrayList.get(position).setPhone(phone);
                            addressItemArrayList.get(position).setApartment(apartment);
                            addressItemArrayList.get(position).setStreet(street);
                            addressItemArrayList.get(position).setCity(city);
                            addressItemArrayList.get(position).setPincode(pincode);
                            addressItemArrayList.get(position).setState(stateStr);
                            addressItemArrayList.get(position).setCountry(countryStr);
                            addressItemArrayList.get(position).setPhone(phone);
                            addressItemAdapter.notifyDataSetChanged();
                        }
                        progressBarDialog = new Dialog(AddressShowingInputActivity.this);
                        progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        progressBarDialog.setContentView(R.layout.progress_bar_dialog);
                        progressBarDialog.setCancelable(false);
                        progressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        progressBarDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                        progressBarDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                        progressBarDialog.show();
                        getAllAddress();
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
                                Toast.makeText(AddressShowingInputActivity.this, message, Toast.LENGTH_LONG).show();
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
    Boolean allTrueOrFalse = true;
    private boolean checkValidation() {
        if (firstNameEditText.getText().toString().isEmpty()) {
            firstNameLayout.setError("First Name Required");
            allTrueOrFalse = false;
        }else {
            firstNameLayout.setErrorEnabled(false);
        }
        if (lastNameEditText.getText().toString().isEmpty()) {
            lastNameLayout.setError("Last Name Required");
            allTrueOrFalse = false;
        }else {
            lastNameLayout.setErrorEnabled(false);
        }
        if (emailEditText.getText().toString().isEmpty()) {
            emailLayout.setError("Email Required");
            allTrueOrFalse = false;
        }else {
            emailLayout.setErrorEnabled(false);
        }
        if (phoneEditText.getText().toString().isEmpty()) {
            phoneLayout.setError("Phone Required");
            allTrueOrFalse = false;
        }else {
            phoneLayout.setErrorEnabled(false);
        }
        if (apartmentEditText.getText().toString().isEmpty()) {
            apartmentLayout.setError("Apartment Required");
            allTrueOrFalse = false;
        }else {
            apartmentLayout.setErrorEnabled(false);
        }
        if (streetEditText.getText().toString().isEmpty()) {
            streetLayout.setError("Street Required");
            allTrueOrFalse = false;
        }else  {
            streetLayout.setErrorEnabled(false);
        }
        if (cityEditText.getText().toString().isEmpty()) {
            cityLayout.setError("City Required");
            allTrueOrFalse = false;
        }else {
            cityLayout.setErrorEnabled(false);
        }
        if (pincodeEditText.getText().toString().isEmpty()) {
            pincodeLayout.setError("Pin Code Required");
            allTrueOrFalse = false;
        }else {
            pincodeLayout.setErrorEnabled(false);
        }
        if (stateSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "State not selected", Toast.LENGTH_SHORT).show();
            allTrueOrFalse = false;
        }
        if (countrySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Country not selected", Toast.LENGTH_SHORT).show();
            allTrueOrFalse = false;
        }
        return allTrueOrFalse;
    }
    public void checkAddressArrayListSize(){
        if (addressItemArrayList.isEmpty()) {
            noDataLayout.setVisibility(View.VISIBLE);
            addressRecyclerView.setVisibility(View.GONE);
        }
    }
    public void deleteAddress(int position){
        String deleteURL = Constant.BASE_URL + "address/delete/" + addressItemArrayList.get(position).getAddressId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(AddressShowingInputActivity.this, "Address Deleted SuccessFully", Toast.LENGTH_SHORT).show();
                        addressItemArrayList.remove(position);
                        addressRecyclerView.setAdapter(new AddressItemAdapter(addressItemArrayList, AddressShowingInputActivity.this));
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
                                Toast.makeText(AddressShowingInputActivity.this, message, Toast.LENGTH_LONG).show();
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
package com.example.whitelabeltemplate3.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.whitelabeltemplate3.Activities.AddressShowingInputActivity;
import com.example.whitelabeltemplate3.Activities.MainActivity;
import com.example.whitelabeltemplate3.Activities.MyOrderActivity;
import com.example.whitelabeltemplate3.R;
import com.example.whitelabeltemplate3.Utils.SessionManager;

public class ProfileFragment extends Fragment {
    ImageView backBtn;
    CardView userDetailsCard,myOrderCard,addressCard, logoutCard;
    TextView userNameTxt,userPhoneTxt,userEmailTxt;
    String userNameStr,userPhoneStr,userEmailStr;
    SessionManager sessionManager;
    String storeId,authToken;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(getContext());
        storeId = sessionManager.getStoreId();
        authToken = sessionManager.getUserData().get("authToken");

        backBtn = view.findViewById(R.id.backBtn);
        userDetailsCard = view.findViewById(R.id.userDetailsCard);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        userNameTxt = view.findViewById(R.id.userNameTxt);
        userPhoneTxt = view.findViewById(R.id.userPhoneTxt);
        userEmailTxt = view.findViewById(R.id.userEmailTxt);

        userEmailStr = sessionManager.getUserData().get("email");
        userNameStr = sessionManager.getUserData().get("fullName");
        userPhoneStr = sessionManager.getUserData().get("role");

        Log.e("token",sessionManager.getUserData().get("authToken"));

        userNameTxt.setText(userNameStr);
        userEmailTxt.setText(userEmailStr);
        userPhoneTxt.setText(userPhoneStr);

        myOrderCard = view.findViewById(R.id.myOrderCard);
        addressCard = view.findViewById(R.id.addressCard);
        logoutCard = view.findViewById(R.id.logoutCard);

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
        addressCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddressShowingInputActivity.class));
            }
        });
        myOrderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyOrderActivity.class));
            }
        });

        userDetailsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDialog();
            }
        });

        return view;
    }
    Dialog filterDialog;
    ImageView crossBtn;
    private void openEditDialog() {
        filterDialog = new Dialog(getContext());
        filterDialog.setContentView(R.layout.profile_edit_dialog);

        crossBtn = filterDialog.findViewById(R.id.crossBtn);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });

        filterDialog.show();
        filterDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        filterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        filterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationDisplayBottomTop;
        filterDialog.getWindow().setGravity(Gravity.TOP);
        filterDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            filterDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),R.color.white));
        }
    }
}

package com.example.whitelabeltemplate3.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitelabeltemplate3.Activities.AddressShowingInputActivity;
import com.example.whitelabeltemplate3.Models.AddressItemModel;
import com.example.whitelabeltemplate3.R;

import java.util.ArrayList;

public class AddressItemAdapter extends RecyclerView.Adapter<AddressItemAdapter.ViewHolder> {
    ArrayList<AddressItemModel> productDetailsList;
    Context context;
    public AddressItemAdapter(ArrayList<AddressItemModel> productDetailsList, Context context) {
        this.productDetailsList = productDetailsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_recycler_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String userName, userPhone, userEmail, addressLine1, addressLine2, addressLine3;

        userName = productDetailsList.get(position).getFirstName() + " " + productDetailsList.get(position).getLastName();
        userPhone = productDetailsList.get(position).getPhone();
        userEmail = productDetailsList.get(position).getEmail();
        addressLine1 = productDetailsList.get(position).getApartment() + ", " + productDetailsList.get(position).getStreet();
        addressLine2 = productDetailsList.get(position).getCity() + ", " + productDetailsList.get(position).getPincode();
        addressLine3 = productDetailsList.get(position).getState() + ", " + productDetailsList.get(position).getCountry();

        holder.userNameTxt.setText(userName);
        holder.userPhoneTxt.setText(userPhone);
        holder.userEmailTxt.setText(userEmail);
        holder.addressLine2Txt.setText(addressLine1);
        holder.addressLine3Txt.setText(addressLine2 + ", " + addressLine3);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                productDetailsList.remove(position);
//                notifyDataSetChanged();
                ((AddressShowingInputActivity) context).checkAddressArrayListSize();
                ((AddressShowingInputActivity) context).deleteAddress(position);
            }
        });
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AddressShowingInputActivity) context).showAddAddressDialog(position,true);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productDetailsList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTxt, userPhoneTxt, userEmailTxt, addressLine2Txt, addressLine3Txt;
        ImageView productImg;
        CardView deleteBtn, editBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            userPhoneTxt = itemView.findViewById(R.id.userPhoneTxt);
            userEmailTxt = itemView.findViewById(R.id.addressLine1Txt);
            addressLine2Txt = itemView.findViewById(R.id.addressLine2Txt);
            addressLine3Txt = itemView.findViewById(R.id.addressLine3Txt);
            productImg = itemView.findViewById(R.id.productImg);
            deleteBtn = itemView.findViewById(R.id.deleteCardView);
            editBtn = itemView.findViewById(R.id.editCardView);

        }
    }
}

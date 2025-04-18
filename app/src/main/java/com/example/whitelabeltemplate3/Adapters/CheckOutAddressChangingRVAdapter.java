package com.example.whitelabeltemplate3.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitelabeltemplate3.Activities.CheckOutOrderActivity;
import com.example.whitelabeltemplate3.Models.AddressItemModel;
import com.example.whitelabeltemplate3.R;

import java.util.ArrayList;

public class CheckOutAddressChangingRVAdapter extends RecyclerView.Adapter<CheckOutAddressChangingRVAdapter.ViewHolder> {
    ArrayList<AddressItemModel> addressItemModelArrayList;
    Context context;
    public CheckOutAddressChangingRVAdapter(ArrayList<AddressItemModel> addressItemModelArrayList, Context context) {
        this.addressItemModelArrayList = addressItemModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_address_recycler_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String addressLine1, addressLine2, addressLine3;

        addressLine1 = addressItemModelArrayList.get(position).getFirstName() + " " + addressItemModelArrayList.get(position).getLastName()+ "," + addressItemModelArrayList.get(position).getPhone();
        addressLine2 = addressItemModelArrayList.get(position).getApartment() + ", " + addressItemModelArrayList.get(position).getStreet() + ", " + addressItemModelArrayList.get(position).getCity() + ", " + addressItemModelArrayList.get(position).getPincode();
        addressLine3 = addressItemModelArrayList.get(position).getState() + ", " + addressItemModelArrayList.get(position).getCountry();

        holder.userNamePhone.setText(addressLine1);
        holder.addressLine2Txt.setText(addressLine2);
        holder.addressLine3Txt.setText(addressLine3);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CheckOutOrderActivity)context).setAddressFromAdapter(addressLine1,addressLine2,addressLine3,addressItemModelArrayList.get(position).getAddressId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressItemModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNamePhone, addressLine2Txt, addressLine3Txt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userNamePhone = itemView.findViewById(R.id.addressLine1);
            addressLine2Txt = itemView.findViewById(R.id.addressLine2);
            addressLine3Txt = itemView.findViewById(R.id.addressLine3);

        }
    }
}

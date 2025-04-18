package com.example.whitelabeltemplate3.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitelabeltemplate3.Fragment.CartItemFragment;
import com.example.whitelabeltemplate3.Models.CouponSelectingModel;
import com.example.whitelabeltemplate3.R;

import java.util.ArrayList;

public class CouponSelectingAdapter extends RecyclerView.Adapter<CouponSelectingAdapter.ViewHolder> {
    ArrayList<CouponSelectingModel> couponSelectingModelArrayList;
    Fragment context;
    int finalAmount,productQuantity;
    public CouponSelectingAdapter(ArrayList<CouponSelectingModel> couponSelectingModelArrayList, Fragment context, int finalAmount, int productQuantity) {
        this.couponSelectingModelArrayList = couponSelectingModelArrayList;
        this.context = context;
        this.finalAmount = finalAmount;
        this.productQuantity = productQuantity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_selecting_recycler_item_layout,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String couponCode,couponType,discountType,discountValue,minimumPurchaseType,text1value = "0",text2Value = "0";
        int minimumPurchaseValue;
        couponCode = couponSelectingModelArrayList.get(position).getCouponCode();
        couponType = couponSelectingModelArrayList.get(position).getCouponType();
        discountType = couponSelectingModelArrayList.get(position).getDiscountType();
        discountValue = couponSelectingModelArrayList.get(position).getDiscountValue();
        minimumPurchaseType = couponSelectingModelArrayList.get(position).getMinimumPurchaseType();
        minimumPurchaseValue = Integer.parseInt(couponSelectingModelArrayList.get(position).getMinimumPurchaseValue());

        holder.couponTxt.setText(couponCode);
        if (couponType.equalsIgnoreCase("ORDER")) {
            if (discountType.equalsIgnoreCase("PERCENTAGE") && minimumPurchaseType.equalsIgnoreCase("AMOUNT")) {
                text1value = "GET " + discountValue + " % OFF";
                if (finalAmount >= minimumPurchaseValue) {
                    text2Value = "You are eligible for this coupon";
                } else {
                    text2Value = "Add more ₹ " + (minimumPurchaseValue - finalAmount) + " worth of item to avail this offer";
                    holder.mainLayout.setEnabled(false);
                    holder.copyCouponCode.setEnabled(false);
                    holder.discountIcon.setImageResource(R.drawable.ic_discount_grey);
                    holder.text1.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.text2.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.couponTxt.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.tapToApply.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                }
            } else if (discountType.equalsIgnoreCase("PERCENTAGE") && minimumPurchaseType.equalsIgnoreCase("QUANTITY")) {
                text1value = "GET " + discountValue + " % OFF";
                if (productQuantity >= minimumPurchaseValue) {
                    text2Value = "You are eligible for this coupon";
                } else {
                    text2Value = "Add more " + (minimumPurchaseValue - productQuantity) + " more item to avail this offer";
                    holder.mainLayout.setEnabled(false);
                    holder.copyCouponCode.setEnabled(false);
                    holder.discountIcon.setImageResource(R.drawable.ic_discount_grey);
                    holder.text1.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.text2.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.couponTxt.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.tapToApply.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                }
            }else if (discountType.equalsIgnoreCase("FIXED_AMOUNT") && minimumPurchaseType.equalsIgnoreCase("AMOUNT")) {
                text1value = "GET ₹ " + discountValue + " OFF";
                if (finalAmount > minimumPurchaseValue) {
                    text2Value = "You are eligible for this coupon";
                } else if (finalAmount < minimumPurchaseValue) {
                    text2Value = "Add more ₹ " + (minimumPurchaseValue - finalAmount) + " worth of item to avail this offer";
                    holder.mainLayout.setEnabled(false);
                    holder.copyCouponCode.setEnabled(false);
                    holder.discountIcon.setImageResource(R.drawable.ic_discount_grey);
                    holder.text1.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.text2.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.couponTxt.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.tapToApply.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                }
            }else if (discountType.equalsIgnoreCase("FIXED_AMOUNT") && minimumPurchaseType.equalsIgnoreCase("QUANTITY")) {
                text1value = "GET ₹ " + discountValue + " OFF";
                if (productQuantity > minimumPurchaseValue) {
                    text2Value = "You are eligible for this coupon";
                } else if (productQuantity < minimumPurchaseValue) {
                    text2Value = "Add more " + (minimumPurchaseValue - productQuantity) + " more item to avail this offer";
                    holder.mainLayout.setEnabled(false);
                    holder.copyCouponCode.setEnabled(false);
                    holder.discountIcon.setImageResource(R.drawable.ic_discount_grey);
                    holder.text1.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.text2.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.couponTxt.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.tapToApply.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                }
            }
        }else {
            int shippingCharge = ((CartItemFragment) context).getShippingCharge();
            if (shippingCharge == 0){
                text1value = "GET ₹ 0 SHIPPING CHARGES";
                text2Value = "Your shipping charge is already ₹ 0";
                holder.mainLayout.setEnabled(false);
                holder.copyCouponCode.setEnabled(false);
                holder.discountIcon.setImageResource(R.drawable.ic_discount_grey);
                holder.text1.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                holder.text2.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                holder.couponTxt.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                holder.tapToApply.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
            }else {
                text1value = "GET ₹ 99 OFF";
                if (finalAmount > minimumPurchaseValue) {
                    text2Value = "You are eligible for this coupon";
                } else if (finalAmount < minimumPurchaseValue) {
                    text2Value = "Add more ₹ " + (minimumPurchaseValue - finalAmount) + " worth of item to avail this offer";
                    holder.mainLayout.setEnabled(false);
                    holder.copyCouponCode.setEnabled(false);
                    holder.discountIcon.setImageResource(R.drawable.ic_discount_grey);
                    holder.text1.setTextColor(ContextCompat.getColor(context.getContext(), R.color.grey_dark_bg));
                    holder.text2.setTextColor(ContextCompat.getColor(context.getContext(), R.color.grey_dark_bg));
                    holder.couponTxt.setTextColor(ContextCompat.getColor(context.getContext(),R.color.grey_dark_bg));
                    holder.tapToApply.setTextColor(ContextCompat.getColor(context.getContext(), R.color.grey_dark_bg));
                }
            }

        }
        holder.text1.setText(text1value);
        holder.text2.setText(text2Value);
        holder.couponTxt.setText(couponCode);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CartItemFragment) context).closeCouponDialog(position);
            }
        });
        holder.copyCouponCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from couponTxt
                String couponCode = couponSelectingModelArrayList.get(position).getCouponCode();

                // Get clipboard manager
                android.content.ClipboardManager clipboard =
                        (android.content.ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);

                // Create a clip with the coupon code
                android.content.ClipData clip = android.content.ClipData.newPlainText("Coupon Code", couponCode);

                // Set the clip to clipboard
                clipboard.setPrimaryClip(clip);

                // Show a toast message to inform the user
                Toast.makeText(v.getContext(), "Coupon copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return couponSelectingModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2,couponTxt,tapToApply;
        ImageView discountIcon,copyCouponCode;
        RelativeLayout mainLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            discountIcon = itemView.findViewById(R.id.disIcon);
            text1 = itemView.findViewById(R.id.text1);
            text2 = itemView.findViewById(R.id.text2);
            couponTxt = itemView.findViewById(R.id.couponTxt);
            tapToApply = itemView.findViewById(R.id.tapToApply);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            copyCouponCode = itemView.findViewById(R.id.copyCouponCode);

        }
    }
}

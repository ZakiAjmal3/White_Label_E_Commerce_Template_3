package com.example.whitelabeltemplate3.Models;

public class CouponSelectingModel {
    String couponId,couponCode,couponType,startDate,endDate,discountType,discountValue,
            minimumPurchaseType,minimumPurchaseValue,store;

    public CouponSelectingModel(String couponId, String couponCode, String couponType, String startDate, String endDate, String discountType, String discountValue, String minimumPurchaseType, String minimumPurchaseValue, String store) {
        this.couponId = couponId;
        this.couponCode = couponCode;
        this.couponType = couponType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minimumPurchaseType = minimumPurchaseType;
        this.minimumPurchaseValue = minimumPurchaseValue;
        this.store = store;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getMinimumPurchaseType() {
        return minimumPurchaseType;
    }

    public void setMinimumPurchaseType(String minimumPurchaseType) {
        this.minimumPurchaseType = minimumPurchaseType;
    }

    public String getMinimumPurchaseValue() {
        return minimumPurchaseValue;
    }

    public void setMinimumPurchaseValue(String minimumPurchaseValue) {
        this.minimumPurchaseValue = minimumPurchaseValue;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}

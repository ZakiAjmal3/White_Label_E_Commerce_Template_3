package com.example.whitelabeltemplate3.Models;

import java.util.ArrayList;

public class MyOrderModel {
    String orderId,finalAmount,orderStatus,orderDate,productTitle;
    ArrayList<ProductImagesModel> imagesModelArrayList;

    public MyOrderModel(String orderId, String finalAmount, String orderStatus, String orderDate, String productTitle, ArrayList<ProductImagesModel> imagesModelArrayList) {
        this.orderId = orderId;
        this.finalAmount = finalAmount;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.productTitle = productTitle;
        this.imagesModelArrayList = imagesModelArrayList;
    }

    public ArrayList<ProductImagesModel> getImagesModelArrayList() {
        return imagesModelArrayList;
    }

    public void setImagesModelArrayList(ArrayList<ProductImagesModel> imagesModelArrayList) {
        this.imagesModelArrayList = imagesModelArrayList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(String finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
}
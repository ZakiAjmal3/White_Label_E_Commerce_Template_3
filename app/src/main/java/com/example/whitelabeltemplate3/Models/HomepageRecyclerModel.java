package com.example.whitelabeltemplate3.Models;

public class HomepageRecyclerModel {
    String productName,productPrice;
    int productImg;

    public HomepageRecyclerModel(String productName, String productPrice, int productImg) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImg = productImg;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductImg() {
        return productImg;
    }

    public void setProductImg(int productImg) {
        this.productImg = productImg;
    }
}

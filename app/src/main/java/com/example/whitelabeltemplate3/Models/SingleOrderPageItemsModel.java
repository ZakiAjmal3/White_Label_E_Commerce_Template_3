package com.example.whitelabeltemplate3.Models;

public class SingleOrderPageItemsModel {
    String productId,productImgUrl,productTitle,productPrice;
    public SingleOrderPageItemsModel(String productId, String productImgUrl, String productTitle, String productPrice) {
        this.productId = productId;
        this.productImgUrl = productImgUrl;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImgUrl() {
        return productImgUrl;
    }

    public void setProductImgUrl(String productImgUrl) {
        this.productImgUrl = productImgUrl;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}

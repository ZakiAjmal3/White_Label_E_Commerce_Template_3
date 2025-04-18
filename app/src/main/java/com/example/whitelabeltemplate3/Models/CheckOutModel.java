package com.example.whitelabeltemplate3.Models;

public class CheckOutModel {
    String cartId,productId,productTitle,productPrice,productMRP,productDiscountAMT,productQuantity,productImg;

    public CheckOutModel(String cartId, String productId, String productTitle, String productPrice,
                         String productMRP, String productDiscountAMT, String productQuantity,
                         String productImg) {
        this.cartId = cartId;
        this.productId = productId;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productMRP = productMRP;
        this.productDiscountAMT = productDiscountAMT;
        this.productQuantity = productQuantity;
        this.productImg = productImg;
    }

    public String getProductDiscountAMT() {
        return productDiscountAMT;
    }

    public void setProductDiscountAMT(String productDiscountAMT) {
        this.productDiscountAMT = productDiscountAMT;
    }

    public String getProductMRP() {
        return productMRP;
    }

    public void setProductMRP(String productMRP) {
        this.productMRP = productMRP;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }
}

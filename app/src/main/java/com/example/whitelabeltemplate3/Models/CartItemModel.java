package com.example.whitelabeltemplate3.Models;

import java.util.ArrayList;

public class CartItemModel {
    String cartId,productId, productTitle,productQuantity,slug,productMRP,productPrice,discountAmount,discountPercentage,stock,
            description,tags,productSKU,store,category,inputTag,productRating;
    int wishListImgToggle;
    ArrayList<ProductImagesModel> productImagesModelsArrList;

    public CartItemModel(String cartId, String productId, String productTitle, String productQuantity,
                         String slug, String productMRP, String productPrice, String discountAmount,
                         String discountPercentage, String stock, String description, String tags,
                         String productSKU, String store, String category, String inputTag,
                         String productRating, int wishListImgToggle,
                         ArrayList<ProductImagesModel> productImagesModelsArrList) {
        this.cartId = cartId;
        this.productId = productId;
        this.productTitle = productTitle;
        this.productQuantity = productQuantity;
        this.slug = slug;
        this.productMRP = productMRP;
        this.productPrice = productPrice;
        this.discountAmount = discountAmount;
        this.discountPercentage = discountPercentage;
        this.stock = stock;
        this.description = description;
        this.tags = tags;
        this.productSKU = productSKU;
        this.store = store;
        this.category = category;
        this.inputTag = inputTag;
        this.productRating = productRating;
        this.wishListImgToggle = wishListImgToggle;
        this.productImagesModelsArrList = productImagesModelsArrList;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getProductMRP() {
        return productMRP;
    }

    public void setProductMRP(String productMRP) {
        this.productMRP = productMRP;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getProductSKU() {
        return productSKU;
    }

    public void setProductSKU(String productSKU) {
        this.productSKU = productSKU;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInputTag() {
        return inputTag;
    }

    public void setInputTag(String inputTag) {
        this.inputTag = inputTag;
    }

    public String getProductRating() {
        return productRating;
    }

    public void setProductRating(String productRating) {
        this.productRating = productRating;
    }

    public int getWishListImgToggle() {
        return wishListImgToggle;
    }

    public void setWishListImgToggle(int wishListImgToggle) {
        this.wishListImgToggle = wishListImgToggle;
    }

    public ArrayList<ProductImagesModel> getProductImagesModelsArrList() {
        return productImagesModelsArrList;
    }

    public void setProductImagesModelsArrList(ArrayList<ProductImagesModel> productImagesModelsArrList) {
        this.productImagesModelsArrList = productImagesModelsArrList;
    }
}

package com.analyse.pme.models;

public class Sale {
    private String date;
    private String productRef;
    private int quantity;
    private int shopId;

    public Sale() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProductRef() {
        return productRef;
    }

    public void setProductRef(String productRef) {
        this.productRef = productRef;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }


    @Override
    public String toString() {
        return "Sale{" +
                "date='" + date + '\'' +
                ", productRef='" + productRef + '\'' +
                ", quantity=" + quantity +
                ", shopId=" + shopId +
                '}';
    }
}
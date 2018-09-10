package com.weblaxy.skr.ModelClass;

import org.json.JSONArray;

import java.lang.reflect.Array;

public class OrderedLists {

    //Data Variables
    private String orderId;
    private String orderStatus;
    private String totalAmount;
    private String createdDate;
    private String deliveryDate;
    private String deliveryAddress;

    public JSONArray getItems() {
        return items;
    }

    public void setItems(JSONArray items) {
        this.items = items;
    }

    private JSONArray items;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createddate) {
        this.createdDate = createdDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}

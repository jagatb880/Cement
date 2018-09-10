package com.weblaxy.skr.WebService;

public class WebAddress {

    public static String mdUrl = "http://weblaxy.in/skr/api/rest/";

    public static String getCategoryListUrl(){
        return mdUrl+"getCategoriyList";
    }
    public static String getProductListUrl(){
        return mdUrl+"getVariantList?data=";
    }
    public static String getProductDetailsUrl(){
        return mdUrl+"getProductDetails?data=";
    }
    public static String getProfileDetailsUrl(){
        return mdUrl+"getProfileDetails?data=";
    }
    public static String setProfileDetailsUrl(){
        return mdUrl+"setProfileDetails?data=";
    }
    public static String getOrderListUrl(){
        return mdUrl+"getOrderList?data=";
    }
    public static String getOrderDetailsUrl(){
        return mdUrl+"getOrderDetails?data=";
    }
}

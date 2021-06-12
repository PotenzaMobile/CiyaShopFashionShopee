package com.example.ciyashop.utils;

import com.ciyashop.library.apicall.URLS;

public class APIS {

    //TODO:Copy and Paste URL and Key Below from Admin Panel.

    public final String APP_URL = "https://fashionshopie.com/";
    public final String WOO_MAIN_URL = APP_URL + "wp-json/wc/v2/";
    public final String MAIN_URL = APP_URL + "wp-json/pgs-woo-api/v1/";

    public static final String CONSUMERKEY = "foNq5LpmiWTO";
    public static final String CONSUMERSECRET = "9bj9rpHkDTmIz6AD17hcJjFR2K8orDW1lgxEB66NR2WZM86U";
    public static final String OAUTH_TOKEN = "HYiVhMOCo0lUAzQIyRqqKXR6";
    public static final String OAUTH_TOKEN_SECRET = "e6IoERBqiH0VtwnCctkFqPW2KEgNCNIzKCIgndC4dLabtcli";

    public static final String WOOCONSUMERKEY = "ck_4f0017f8a2d05e2f1510dde86d3f9e293da6f47e";
    public static final String WOOCONSUMERSECRET = "cs_4be626fb164055cb3a771fa96f26678a59316ca0";
    public static final String version="4.2.0";
    public static final String purchasekey="8a668a1d-c2f2-4d0c-bf7f-c3ee1dc8363f";
    
    public APIS() {
        URLS.APP_URL = APP_URL;
        URLS.NATIVE_API = APP_URL + "wp-json/wc/v3/";
        URLS.WOO_MAIN_URL = WOO_MAIN_URL;
        URLS.MAIN_URL = MAIN_URL;
        URLS.version = version;
        URLS.CONSUMERKEY = CONSUMERKEY;
        URLS.CONSUMERSECRET = CONSUMERSECRET;
        URLS.OAUTH_TOKEN = OAUTH_TOKEN;
        URLS.OAUTH_TOKEN_SECRET = OAUTH_TOKEN_SECRET;
        URLS.WOOCONSUMERKEY = WOOCONSUMERKEY;
        URLS.WOOCONSUMERSECRET = WOOCONSUMERSECRET;
        URLS.PURCHASE_KEY=purchasekey;
    }
}
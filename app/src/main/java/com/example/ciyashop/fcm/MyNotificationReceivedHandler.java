package com.example.ciyashop.fcm;

import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import com.example.ciyashop.utils.RequestParamUtils;

import org.json.JSONObject;

public class MyNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String customKey;

        if (data != null) {
            //While sending a Push notification from OneSignal dashboard
            // you can send an addtional data named "customkey" and retrieve the value of it and do necessary operation
            customKey = data.optString(RequestParamUtils.customkey, null);
            if (customKey != null)
               Log.e("OneSignalExample", "customkey set with value: " + customKey);
        }
    }
}
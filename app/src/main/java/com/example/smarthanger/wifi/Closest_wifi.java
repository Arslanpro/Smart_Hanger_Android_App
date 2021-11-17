package com.example.smarthanger.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

@SuppressLint("MissingPermission")
public class Closest_wifi {

    private static final String TAG = "";
    Context context;
    public Closest_wifi(Context context)  {
        this.context = context;
    }

    public String getClosestMac() {
        ScanResult closest_wifi = getClosestWifi();
        if (closest_wifi == null)    {
            return "0:0:0";
        }
        return closest_wifi.BSSID;
    }

    public ScanResult getClosestWifi() {
        ScanResult bestSignal = null;
        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> results = wifiManager.getScanResults();
        for (ScanResult result : results) {
            if(bestSignal == null)  {
                bestSignal = result;
            } else if(bestSignal.level < result.level)  {
                bestSignal = result;
            }
        }
        return bestSignal;
    }
}

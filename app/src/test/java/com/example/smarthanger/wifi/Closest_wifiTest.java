package com.example.smarthanger.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Closest_wifiTest extends TestCase {

    Context context;
    private Closest_wifi closest_wifi;
    private String mac;

    @Before
    public void setUp() {
        context = PowerMockito.mock(Context.class);
        closest_wifi = new Closest_wifi(context);
        mac = "0:0:0:23";
    }
    @Test
    public void testGetClosestMac() {
        ConnectivityManager connManager = PowerMockito.mock(ConnectivityManager.class);
        WifiManager wifiManager = PowerMockito.mock(WifiManager.class);
        ScanResult scanResult = PowerMockito.mock(ScanResult.class);
        // one wifi
        PowerMockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connManager);
        PowerMockito.when((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).thenReturn(wifiManager);
        PowerMockito.when(wifiManager.getScanResults()).thenReturn(Collections.singletonList(scanResult));
        scanResult.BSSID = mac;
        scanResult.level = 10;
        //PowerMockito.when(scanResult.BSSID).thenReturn(mac);
        String id = closest_wifi.getClosestMac();
        assertEquals(mac, id);
        ScanResult scanResult2 = PowerMockito.mock(ScanResult.class);
        scanResult2.level = 0;
        //multiple wifis
        LinkedList<ScanResult> scanResultList = new LinkedList<>();
        scanResultList.add(scanResult2);
        scanResultList.add(scanResult);
        scanResultList.add(scanResult);
        PowerMockito.when(wifiManager.getScanResults()).thenReturn(scanResultList);
        //PowerMockito.when(scanResult.BSSID).thenReturn(mac);
        id = closest_wifi.getClosestMac();
        assertEquals(mac, id);
    }
}
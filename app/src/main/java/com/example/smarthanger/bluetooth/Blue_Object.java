package com.example.smarthanger.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class Blue_Object {
    static BluetoothSocket btSocket = null;
    private static final List<String> dataList = new ArrayList<>();
    private static final int PERMISSION_ASK = 1001;
    static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public static BluetoothDevice bluetoothDevice;
    static String deviceName;
    private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String lastMessage;
    private BluetoothSocket mmSocket;
    public boolean isConnected() {
        return btSocket.isConnected();
    }
    private boolean connected;
    public Blue_Object() {

    }

    public static void sendMessage(OutputStream outputStream)    {
        // request the info
        try {
            String data = "send info";
            System.out.println("sending data");
            outputStream.write(data.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String receiveMessage(InputStream inputStream)   {
        StringBuilder message = new StringBuilder();
        try {
            inputStream.skip(inputStream.available());
            for (int i = 0; i < 20; i++) {
                byte b = (byte) inputStream.read();
                System.out.print((char) b);
                message.append((char) b);
            }
            System.out.print("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message.toString();
    }

    public static BluetoothSocket getBtSocket() {
        return btSocket;
    }
    public void createConnection()    {
        bluetoothDevice = bluetoothAdapter.getRemoteDevice("98:D3:41:F6:09:F7");
        System.out.println(bluetoothDevice.getName());
        int counter = 0;
        if(btSocket == null ||
                !btSocket.isConnected() || !btSocket.getRemoteDevice().equals(bluetoothDevice)) {
            do {
                try {
                    btSocket = bluetoothDevice.createRfcommSocketToServiceRecord(mUUID);
                    deviceName = btSocket.getRemoteDevice().getName();
                    System.out.println(btSocket);
                    btSocket.connect();
                    this.connected = btSocket.isConnected();
                    System.out.println(connected);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                counter++;
            } while (!Objects.requireNonNull(btSocket).isConnected() && counter < 3);
        }
    }

    public UUID getmUUID() {
        return mUUID;
    }

    public void disconnect() {
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

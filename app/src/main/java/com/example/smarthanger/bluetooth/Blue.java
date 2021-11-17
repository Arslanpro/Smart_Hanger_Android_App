package com.example.smarthanger.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import com.example.smarthanger.MainActivity;
import com.example.smarthanger.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class Blue extends AppCompatActivity {
    static BluetoothSocket btSocket = null;
    private static final List<String> dataList = new ArrayList<>();
    private static final int PERMISSION_ASK = 1001;
    static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public static BluetoothDevice bluetoothDevice;
    static String deviceName;

    public static UUID getmUUID() {
        return mUUID;
    }

    private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    String lastMessage;
    private BluetoothSocket mmSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blue);

        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        Button connect = findViewById(R.id.connect);
        connect.setOnClickListener(v -> {
            this.createConnection();
            if (btSocket.isConnected()) {
                try {
                    sendMessage(btSocket.getOutputStream());
                    this.lastMessage = receiveMessage(btSocket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dataList.add(lastMessage);
            }
        });
        TextView txt = findViewById(R.id.textView);
        txt.setText(Html.fromHtml(getString(R.string.intro_text)));
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
            for (int i = 0; i < 26; i++) {
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
                    boolean connected = btSocket.isConnected();
                    System.out.println(connected);
                    if (connected) {
                        Intent intent = new Intent(Blue.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                counter++;
            } while (!Objects.requireNonNull(btSocket).isConnected() && counter < 3);
        }
    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.cancelDiscovery();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isBluetoothPermissionGranted() {
        boolean granted = false;
        int bluetoothGranted = checkSelfPermission(Manifest.permission.BLUETOOTH);
        int bluetoothAdminGranted = checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN);

        if (bluetoothGranted == PackageManager.PERMISSION_GRANTED &&
                bluetoothAdminGranted == PackageManager.PERMISSION_GRANTED) {
            granted = true;
        }

        return granted;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForBluetoothPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        };
        requestPermissions(permissions, PERMISSION_ASK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ASK) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // all requested permissions were granted
                // perform your task here
            } else {
                // permissions not granted
                // DO NOT PERFORM THE TASK, it will fail/crash
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean isConnected() {
        return btSocket.isConnected();
    }

    public void disconnect() {
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

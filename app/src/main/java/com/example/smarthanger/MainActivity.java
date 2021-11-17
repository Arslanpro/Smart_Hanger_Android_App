package com.example.smarthanger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthanger.bluetooth.Blue;
import com.example.smarthanger.bluetooth.Blue_Object;
import com.example.smarthanger.server.ServerRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

@SuppressLint("MissingPermission")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private final BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
    private final BluetoothDevice hc05 = ba.getRemoteDevice("98:D3:41:F6:09:F7");
    private final Set<BluetoothDevice> pairedDevices = ba.getBondedDevices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // No night dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        //TODO remove true for final release true ||
        if (pairedDevices.contains(hc05)) {
            if (isServiceOK()) {
                setContentView(R.layout.activity_main);
                init();
            }
        } else {
            Intent intent = new Intent(MainActivity.this, Blue.class);
            startActivity(intent);
        }

        ServerRequest serverRequest = new ServerRequest(getCacheDir(), this);
        try {
            serverRequest.sendGetRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //disable back button
    @Override
    public void onBackPressed() {
        finish();
    }

    private void init() {
        Drawable grey = AppCompatResources.getDrawable(this, R.drawable.grey_btn);
        Drawable green = AppCompatResources.getDrawable(this, R.drawable.green_btn);
        Drawable off = AppCompatResources.getDrawable(this, R.drawable.hangeroff);
        Drawable on = AppCompatResources.getDrawable(this, R.drawable.hangeron);
        Drawable setGrey, setGreen, setOff, setOn;

        {
            assert grey != null;
            assert green != null;
            assert off != null;
            assert on != null;
            setGrey = DrawableCompat.wrap(grey);
            setGreen = DrawableCompat.wrap(green);
            setOff = DrawableCompat.wrap(off);
            setOn = DrawableCompat.wrap(on);

        }

        Button btnEvents = findViewById(R.id.btnEvents);
        btnEvents.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(MainActivity.this, EventActivity.class);
            startActivity(intent);
        });

        ToggleButton btnPick = findViewById(R.id.btnPick);
        ImageView light = findViewById(R.id.light);
        btnPick.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                patch("true");
                btnPick.setBackground(setGreen);
                light.setBackground(setOn);
            } else {
                patch("false");
                btnPick.setBackground(setGrey);
                light.setBackground(setOff);
            }
        });
    }

    public boolean isServiceOK() {
        Log.d(TAG, "isServiceOK: checking google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServiceOK: Google Play Service is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            assert dialog != null;
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void patch(String bool) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("status", bool);
            postData.put("job", "Software Engineer");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String STATUS_URL = "https://immense-anchorage-52068.herokuapp.com/app/status";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, STATUS_URL, postData, System.out::println, Throwable::printStackTrace);

        requestQueue.add(jsonObjectRequest);

    }
}
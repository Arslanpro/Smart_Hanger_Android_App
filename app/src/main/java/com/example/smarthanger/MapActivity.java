package com.example.smarthanger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
//import com.example.smarthanger.bluetooth.Blue;
import com.example.smarthanger.bluetooth.Blue_Object;
import com.example.smarthanger.server.ServerRequest;
import com.example.smarthanger.wifi.Closest_wifi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint("MissingPermission")
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Display on screen when map is ready
        // Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();


        }
    }

    private static final String TAG = "MapActivity";
    private static final String URL2 = "https://immense-anchorage-52068.herokuapp.com/app/calendar";
    private static final String key = BuildConfig.MAPS_API_KEY;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15F;
    //widgets
    private EditText mSearchText;
    private EditText mDate;
    private EditText mTime;
    private ImageView mGps;
    private ImageView back;
    private ImageView home;
    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = findViewById(R.id.input_search);
        mDate = findViewById(R.id.date);
        mTime = findViewById(R.id.time);
        mGps = findViewById(R.id.ic_gbs);
        back = findViewById(R.id.back);
        home = findViewById(R.id.home);

        getLocationPermission();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                geoLocate();
            }
            return false;
        });

        mSearchText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                geoLocate();
            }
        });

        mGps.setOnClickListener(view -> {
            Log.d(TAG, "onClick: click gps icon");
            getDeviceLocation();
            mSearchText.setText("");
            mSearchText.clearFocus();
            hideKeys();
        });

        back.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(MapActivity.this, EventActivity.class);
            startActivity(intent);
        });

        home.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(MapActivity.this, MainActivity.class);
            startActivity(intent);
        });

        hideSoftKeyboard();
    }

    //disable back button
    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(MapActivity.this, EventActivity.class);
        startActivity(intent);
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate:geolocating");
        // Clear focus and hide the keyboard
        mSearchText.clearFocus();
        hideKeys();

        String searchString = mSearchText.getText().toString();

        Uri.Builder builder = new Uri.Builder();

//        https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Museum%20of%20Contemporary%20Art%20Australia&inputtype=textquery&fields=
//        // formatted_address%2Cname%2Crating%2Copening_hours%2Cgeometry&key=YOUR_API_KEY

        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("findplacefromtext")
                .appendPath("json")
                .appendQueryParameter("input", searchString)
                .appendQueryParameter("inputtype", "textquery")
                .appendQueryParameter("fields", "formatted_address,name,geometry")
                .appendQueryParameter("key", key);

        String myUrl = builder.build().toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, myUrl, null, response -> {
            Log.d("onResponse", response.toString());

            String jsonData = response.toString();

            //Converting jsonData string into JSON object
            JSONObject jsnobject = null;
            try {
                jsnobject = new JSONObject(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Getting languages JSON array from the JSON object
            JSONArray jsonArray = null;

            try {
                assert jsnobject != null;
                jsonArray = jsnobject.getJSONArray("candidates");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Creating an empty ArrayList of type Object
            ArrayList<Object> listdata = new ArrayList<>();

            //Checking whether the JSON array has some value or not
            if (jsonArray != null) {

                //Iterating JSON array
                for (int i = 0; i < jsonArray.length(); i++) {

                    //Adding each element of JSON array into ArrayList
                    try {
                        listdata.add(jsonArray.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.d(TAG, "list data created" + listdata.toString());

            try {
                assert jsonArray != null;
                String formatted_address = jsonArray.getJSONObject(0).getString("formatted_address");
                String name = jsonArray.getJSONObject(0).getString("name");

                JSONObject location = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");

                Log.d(TAG, "test " + formatted_address);
                Log.d(TAG, "name " + name);
                Log.d(TAG, "lat " + lat);
                Log.d(TAG, "lng " + lng);

                moveCamera(new LatLng(lat, lng), DEFAULT_ZOOM, formatted_address);

                Button btnAddEvent = findViewById(R.id.btnAddEvent);

                btnAddEvent.setOnClickListener(view -> {
                    mSearchText.setText("");

                    Double latitude = lat;
                    Double longitude = lng;

                    String[] dateFix = mDate.getText().toString().split("/");
                    String date = dateFix[2] + "-" + dateFix[1] + "-" + dateFix[0];

                    String combine = date + "T" + mTime.getText() + ":00Z";

                    Log.d(TAG, "things going to put into jsonObj" + lat + lng + name + formatted_address + combine + latitude + longitude);

                    DecimalFormat df = new DecimalFormat("#.00000");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        Log.d(TAG, "create jsonmessage");
                        jsonObject.put("location_name", name);
                        jsonObject.put("description", formatted_address);
                        jsonObject.put("date_time", combine);
                        jsonObject.put("latitude", latitude);
                        jsonObject.put("longitude", longitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, URL2, jsonObject, response1 -> Log.d("onResponse", response1.toString()),
                            error -> {
                                String body;
                                body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                Log.d("ErrorServerResponse", body);
                            });

                    //clearing fields
                    mDate.setText("");
                    mTime.setText("");

                    Log.d(TAG, "send data to the server");
                    // Add the request to the RequestQueue.
                    requestQueue.add(jsonObjectRequest1);
                    Intent intent = new Intent(MapActivity.this, EventActivity.class);
                    startActivity(intent);
                    //update local file
                    ServerRequest serverRequest = new ServerRequest(getCacheDir(), this);
                    try {
                        serverRequest.sendGetRequest();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),"EVENT ADDED",Toast.LENGTH_LONG).show();
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


        },
                error -> {
                    String body;
                    body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Log.d("ErrorServerResponse", body);
                });

        requestQueue.add(jsonObjectRequest);

    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Button dataButton = findViewById(R.id.btnSend);
        try {
            if (mLocationPermissionsGranted) {

                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location");
                        Location currentLocation = task.getResult();


                        assert currentLocation != null;
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                DEFAULT_ZOOM, "My Location");

                        dataButton.setOnClickListener(view -> {
                            Double latitude = currentLocation.getLatitude();
                            Double longitude = currentLocation.getLongitude();
                            Blue_Object blue_object = new Blue_Object();
                            blue_object.createConnection();
                            String lastMessage = "empty";
                            if (blue_object.isConnected()) {
                                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            BluetoothSocket btSocket = Blue_Object.getBtSocket();
                            if (Blue_Object.getBtSocket().isConnected()) {
                                try {
                                    Blue_Object.sendMessage(btSocket.getOutputStream());
                                    lastMessage = Blue_Object.receiveMessage(btSocket.getInputStream());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Closest_wifi closest_wifi = new Closest_wifi(getApplicationContext());
                            String mac = closest_wifi.getClosestMac();
                            if(!lastMessage.equals("empty")) {
                                String[] individual_data = lastMessage.split(";");
                                String temp_data = individual_data[0].split(":")[1];
                                ServerRequest serverRequest = new ServerRequest(latitude,
                                        longitude, Double.parseDouble(temp_data),
                                        Double.parseDouble(individual_data[1]), mac, getCacheDir());
                                serverRequest.sendPostRequest();
                            }
                            Toast.makeText(getApplicationContext(),"DATA SENDING DONE!",Toast.LENGTH_LONG).show();
                        });

                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ",lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false;
                        Log.d(TAG, "onRequestPermissionsResult: permission failed");
                        return;
                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: permission granted");
                mLocationPermissionsGranted = true;
                //initialize our map
                initMap();
            }
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void hideKeys() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
    }
}

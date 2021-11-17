package com.example.smarthanger.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ServerRequest {
    private static final String TAG = "Server request";
    private static final String SENSOR_URL = "https://immense-anchorage-52068.herokuapp.com/app/sensor_data";
    private static final String CALENDER_URL = "https://immense-anchorage-52068.herokuapp.com/app/calendar";
    JSONObject jsonObject;
    RequestQueue requestQueue;
    Context context;



    List<String> responseNewData;

    public ServerRequest(Double latitude, Double longitude, Double external_temperature, Double gsr_value,
                         String mac, File cacheDir) {
        responseNewData = new LinkedList<>();
        // Setup connection with backend
        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(cacheDir, 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();
        this.jsonObject = new JSONObject();
        this.requestQueue = requestQueue;
        try {
            Log.d(TAG, "create JSONmessage");
            jsonObject.put("temperature", external_temperature);
            jsonObject.put("gsr_reading", gsr_value);
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("mac_address", mac);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ServerRequest(File cacheDir, Context context) {
        // Setup connection with backend
        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(cacheDir, 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();
        this.requestQueue = requestQueue;
        this.context = context;
    }

    public void sendPostRequest() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SENSOR_URL,
                jsonObject, response -> Log.d("onResponse", response.toString()),
                error -> {
                    String body;
                    body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Log.d("ErrorServerResponse", body);
                });
        Log.d(TAG, "send data to the server");
        requestQueue.add(jsonObjectRequest);
    }

    public void sendGetRequest() throws IOException {
        final String[] responseNew = {null};
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, CALENDER_URL, null, response -> {
                    List<List<String[]>> responsedata = formatData(response.toString());
                    Log.d("ServerResponse", response.toString());
                }, error -> {
                    String body = Objects.requireNonNull(error.getCause()).toString();
                    if (body.contains("org.json.JSONException: Value")) {
                        body = body.replace("org.json.JSONException: Value", "");
                        try {
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("event.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write(body);
                            outputStreamWriter.close();
                        } catch (IOException e) {
                            Log.e("Exception", "File write failed: " + e.toString());
                        }
                        readFromFile(context);
                    }
//                        body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
//                        Log.d("ErrorServerResponse", body);
                });
        requestQueue.add(jsonObjectRequest);
    }

    public List<List<String[]>> formatData(String response) {
        //String response = responseNewData.get(0);
        //Remove beginning and end of json
        response = response.replace("[{", "");
        response = response.replace("}]", "");
        //split into json objects
        String[] responseSplit = response.split("\\},");
        List<String[]> objects = new LinkedList<>();
        //add parts of objects to string, add to list
        for (String responseString : responseSplit) {
            //String responseString = responseSplit[i];
            //responseSplit[i] = responseString;
            String[] objectString = responseString.split(",");
            objects.add(objectString);
        }
        int prev_day = -1;
        int prev_month = -1;
        List<String[]> info = new LinkedList<>();
        List<List<String[]>> data = new LinkedList<>();
        //iterate over all objects
        for (String[] object : objects) {
            String date_time = null;
            //get date of event
            for (String string : object) {
                if (string.contains("date_time")) {
                    date_time = string;
                }
            }
            if (date_time != null) {
                //split the data to get day
                date_time = date_time.split(":")[1];
                date_time = date_time.split("T")[0];
                String day = date_time.split("-")[2];
                String month = date_time.split("-")[1];
                if (prev_day == -1) {
                    prev_day = Integer.parseInt(day);
                    prev_month = Integer.parseInt(month);
                } else if (Integer.parseInt(day) != prev_day
                        || Integer.parseInt(month) != prev_month) {
                    prev_day = Integer.parseInt(day);
                    prev_month = Integer.parseInt(month);
                    data.add(info);
                    info = new LinkedList<>();
                }
                String[] new_data = new String[3];
                new_data[2] = object[1].split(":")[1].replace("\"", "");
                new_data[0] = object[2].split(":")[1].replace("\"", "");
                for (String string : object) {
                    if (string.contains("date_time")) {
                        String time = string.split("T")[1];
                        String[] timeArray = time.split(":");
                        time = timeArray[0] + ":" + timeArray[1];
                        String[] strings = string.split(":");
                        new_data[1] = strings[1] + strings[2];
                        new_data[1] = new_data[1].replace(" \"", "");
                        String[] dateReformat2 = new_data[1].split("T");
                        String[] dateReformat = dateReformat2[0].split("-");
                        new_data[1] = dateReformat[2] + "-" + dateReformat[1] + "-" +
                                dateReformat[0].replace("\"", "") + "T" + time;
                    }
                }
                info.add(new_data);
            }
        }
        data.add(info);
        return data;
    }

    public String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("event.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

}

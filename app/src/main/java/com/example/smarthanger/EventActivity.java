package com.example.smarthanger;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.smarthanger.server.ServerRequest;

import java.io.IOException;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);
        showOnScreen();
        Button btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(view -> {
            Intent intent = new Intent(EventActivity.this, MapActivity.class);
            startActivity(intent);
        });

        Button btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(view -> {
            showOnScreen();
        });

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(EventActivity.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(EventActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    //disable back button
    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(EventActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    public void showOnScreen() {
        ServerRequest serverRequest = new ServerRequest(getCacheDir(), this);


        List<List<String[]>> data = null;
        try {
            serverRequest.sendGetRequest();
            data = serverRequest.formatData(serverRequest.readFromFile(this));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_event);
        TextView tv1 = new TextView(this);
        LinearLayout linearLayout = this.findViewById(R.id.data);

        if (data.size() != 0 && data.get(0).size() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < data.size(); i++) {
                stringBuilder.append(data.get(i).get(0)[1].split("T")[0] + "" + "\n");
                List<String[]> day = data.get(i);
                for (int j = 0; j < day.size(); j++) {
                    stringBuilder.append(day.get(j)[2] + " " + day.get(j)[0] + " " +
                            data.get(i).get(j)[1].split("T")[1] + " " + "\n");
                }
                tv1.setText(stringBuilder.toString());
                tv1.setGravity(Gravity.CENTER);
                tv1.setTextColor(Color.BLACK);
                Drawable border = AppCompatResources.getDrawable(this, R.drawable.text_border);
                assert border != null;
                tv1.setBackground(DrawableCompat.wrap(border));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(10, 10, 10, 10);
                tv1.setLayoutParams(params);
                stringBuilder = new StringBuilder();
                linearLayout.addView(tv1);
                tv1 = new TextView(this);
            }
        } else {
            tv1.setText("No Events Registered");
            tv1.setGravity(Gravity.CENTER);
            tv1.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(10, 10, 10, 10);
            tv1.setLayoutParams(params);
            linearLayout.addView(tv1);
        }
    }
}
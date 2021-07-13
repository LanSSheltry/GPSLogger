package com.example.mygpslogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LOCListenerInterface {
    private LocationManager locManager;
    private MyLocListener myLocListener;
    private TextView speed, latitude, longitude, timer;
    private Button isOn;
    private int seconds = 0;
    boolean isInProcess = false;
    FileManager fileManager;
    String fileName="noName";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileManager = new FileManager(this, this);
        Init();
    }

    private void Init()
    {
        timer = findViewById(R.id.timer);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        speed = findViewById(R.id.speed);
        isOn = (Button) findViewById(R.id.isOn);
        runTimer();
        View.OnClickListener isOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInProcess = !isInProcess;
                if (isInProcess) {
                    isOn.setText("Stop");
                    fileName=fileManager.getAvailableNameOfFile();
                    fileManager.writeToFile(String.valueOf(Calendar.getInstance().getTime())+"\n", fileName);
                } else {
                    latitude.setText("Latitude");
                    longitude.setText("Longitude");
                    speed.setText("Speed m/s");
                    isOn.setText("Start");
                }
            }
        };
        isOn.setOnClickListener(isOnClick);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLocListener();
        myLocListener.setLocaListenerInterface(this);
        CheckPermissions();
    }


    private void runTimer()
    {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int sec = seconds % 60;

                String currentTime = String.format("%d:%02d:%02d", hours, minutes, sec);
                timer.setText(String.valueOf(currentTime));
                if (isInProcess) {
                    seconds++;
                } else {
                    seconds = 0;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }


    private void CheckPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2, myLocListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == RESULT_OK) {
            CheckPermissions();
        } else {
            Toast.makeText(this, "GPS access denied!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location loc) //GPS coordinates was updated
    {
        if (isInProcess) {
            latitude.setText(String.valueOf(loc.getLatitude()));
            longitude.setText(String.valueOf(loc.getLongitude()));
            speed.setText(String.valueOf(loc.getSpeed()) + " m/s");
            try {
                if (isInProcess) {
                    fileManager.writeToFile(
                            String.valueOf(loc.getLatitude()) +
                                    ";" +
                                    String.valueOf(loc.getLongitude()) +
                                    ";" +
                                    String.valueOf(loc.getSpeed()) +
                                    ";" +
                                    String.valueOf(seconds) + "\n", fileName);
                }
            } catch (Exception e) {
                Toast.makeText(this, "File error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

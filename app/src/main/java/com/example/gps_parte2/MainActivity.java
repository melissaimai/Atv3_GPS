package com.example.gps_parte2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final static short PERMISSION = (short) new Random().nextInt(Short.MAX_VALUE);

    private EditText distanceEditText;
    private EditText timeEditText;

    private LocationManager locationManager;
    private float distance;
    private int time;
    private Location last;
    private Timer timer;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            if (last != null) {
                distance += last.distanceTo(location);
                distanceEditText.setText(distance + " metros");
            }
            last = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private void startTimer() {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeEditText.setText((++time) + " segundos");
            }
        }, 0, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        distanceEditText = findViewById(R.id.distancia);
        timeEditText = findViewById(R.id.tempo);
        final Button activate = findViewById(R.id.btnAtivar);
        final Button deactivate = findViewById(R.id.btnDesativar);
        final Button terminate = findViewById(R.id.btnTerminar);

        findViewById(R.id.btnConceder).setOnClickListener(ignored -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION);
        });
        activate.setOnClickListener(ignored -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            else
                Toast.makeText(this, "Favor conceder permissão", Toast.LENGTH_SHORT).show();
        });
        deactivate.setOnClickListener(ignored -> {
            locationManager = null;
        });
        findViewById(R.id.btnIniciar).setOnClickListener(ignored -> {
            distance = time = 0;
            if (locationManager == null || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Favor ativar localização", Toast.LENGTH_SHORT).show();
            else {
                activate.setEnabled(false);
                deactivate.setEnabled(false);
                distanceEditText.setText("Requisitando...");
                timeEditText.setText("Requisitando...");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, locationListener);
                startTimer();
                terminate.setEnabled(true);
            }
        });
        terminate.setOnClickListener(ignored -> {
            activate.setEnabled(true);
            deactivate.setEnabled(true);
            timer.cancel();
            if (locationManager != null)
                locationManager.removeUpdates(locationListener);
            last = null;
        });
        findViewById(R.id.floatingActionButton).setOnClickListener(ignored -> {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + ((EditText) findViewById(R.id.search)).getText()));
            if (mapIntent.resolveActivity(getPackageManager()) != null)
                startActivity(mapIntent);
        });
    }

    @Override
    public void onRequestPermissionsResult(final int code, @NonNull final String[] permissions, @NonNull final int[] results) {
    }
}

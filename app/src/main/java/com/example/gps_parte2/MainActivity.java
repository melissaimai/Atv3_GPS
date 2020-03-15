package com.example.gps_parte2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final static short PERMISSION = (short) new Random().nextInt(Short.MAX_VALUE);
    private LocationManager locationManager;
    private float distance;
    private int time;
    private Location last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        findViewById(R.id.btnConceder).setOnClickListener(ignored -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION);
        });
        findViewById(R.id.btnAtivar).setOnClickListener(ignored -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            else
                Toast.makeText(this, "Favor conceder permissão", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnIniciar).setOnClickListener(ignored -> {
            distance = time = 0;
            if (locationManager == null || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Favor ativar localização", Toast.LENGTH_SHORT).show();
            else
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(final Location location) {
                        if (last == null)
                            last = location;
                        else {
                            distance += last.distanceTo(location);
                            ((EditText) findViewById(R.id.distancia)).setText(distance + " metros");
                        }
                        ((EditText) findViewById(R.id.tempo)).setText((++time) + " segundos");
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
                });
        });
    }

    @Override
    public void onRequestPermissionsResult(final int code, @NonNull final String[] permissions, @NonNull final int[] results) {
    }
}

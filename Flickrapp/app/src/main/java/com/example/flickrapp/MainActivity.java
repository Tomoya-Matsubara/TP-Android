package com.example.flickrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {
    private String image_url;
    private FusedLocationProviderClient fusedLocationClient;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getImageButton = (Button) findViewById(R.id.get_image_button);
        getImageButton.setOnClickListener(new GetImageOnClickListener());

        Button toListViewButton = (Button) findViewById(R.id.button_to_listView);
        toListViewButton.setOnClickListener(new toListViewClickListener());

        Button settingButton = (Button) findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new toPreferenceClickListener());

        Button gpsButton = (Button) findViewById(R.id.gps);
        gpsButton.setOnClickListener(new gpsClickListener());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startUpdateLocation();
    }

    class GetImageOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Click!", Toast.LENGTH_SHORT).show();
            AsyncFlickrJSONData task = new AsyncFlickrJSONData(MainActivity.this);
            task.execute("https://www.flickr.com/services/feeds/photos_public.gne?tags=trees&format=json");
        }
    }

    class toListViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent callListView = new Intent(getApplicationContext(), ListActivity.class);
            callListView.putExtra("lat", String.valueOf(location.getLatitude()));
            callListView.putExtra("lon", String.valueOf(location.getLongitude()));
            startActivity(callListView);
        }
    }

    class toPreferenceClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent callPreference = new Intent(getApplicationContext(), PrefActivity.class);
            startActivity(callPreference);
        }
    }

    class gpsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.i("TMM", "(Debug) Latitude:" + location.getLatitude() + " Longitude:" + location.getLongitude());
            Toast.makeText(getApplicationContext(), "Latitude:" + location.getLatitude() +
                    " Longitude:" + location.getLongitude(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setImageURL(String url) {
        this.image_url = url;
        Toast.makeText(this, "URL set!", Toast.LENGTH_SHORT).show();

        ImageView imageView = (ImageView) findViewById(R.id.image);
        AsyncBitmapDownloader task = new AsyncBitmapDownloader(imageView);
        task.execute(this.image_url);
    }

    private void startUpdateLocation() {
        // Check if using GPS is permitted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If GPS isn't permitted, show a dialog to ask users to permit it
            String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION };
            ActivityCompat.requestPermissions(this, permissions, 2000);
            return;
        }

        // Configuration of how to get location information
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, new MyLocationCallback(),null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startUpdateLocation();
        }
    }

    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            location = locationResult.getLastLocation();

            Log.i("TMM", "Latitude:" + location.getLatitude() + " Longitude:" + location.getLongitude());
        };
    }
}


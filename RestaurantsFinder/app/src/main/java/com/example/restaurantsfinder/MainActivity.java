package com.example.restaurantsfinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, BottomNavigationView.OnNavigationItemSelectedListener {


    // initializing
    // FusedLocationProviderClient
    FusedLocationProviderClient mFusedLocationClient;
    //private int PERMISSION_ID = 44;

    private Double latitude, longitude;
    private List<Restaurant> restaurants = new ArrayList<>();

    private RequestQueue queue;

    private static final String APIKeyGoogle = "AIzaSyBJlBV7wLuK_0Rq2WVaTP_PNllzvnSsC6o";

    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
    private String api_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.showRestaurantsButton);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // fragments
        Fragment fragment = new RestaurantFragment();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home); // se non va provare a cambiare questo
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, DisplayRestaurants.class));

            }
        });

    }




    private void hideProgressBar() {
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.bottom_res:
                //showProgressBar();
                //requestNewLocationData();
                intent = new Intent(MainActivity.this, DisplayRestaurants.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;


            case R.id.bottom_fav:
                intent = new Intent(MainActivity.this, Favorites.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            case R.id.bottom_home:
                return true;


        }

        return false;
    }


}

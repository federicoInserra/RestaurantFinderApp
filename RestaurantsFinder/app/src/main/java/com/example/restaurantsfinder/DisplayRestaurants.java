package com.example.restaurantsfinder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;
import com.google.maps.model.LatLng;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;



public class DisplayRestaurants extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, BottomNavigationView.OnNavigationItemSelectedListener {



    private Double latitude, longitude;

    //private static final String APIKeyGoogle = "AIzaSyC5B74OoRPoAYEeTyC91ML8g45rcPWMbIU";
    private static final String APIKeyGoogle = "AIzaSyBJlBV7wLuK_0Rq2WVaTP_PNllzvnSsC6o";

    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
    private String api_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";



    // Initializing other items


    int PERMISSION_ID = 44;

    private RequestQueue queue;

    private List<Restaurant> restaurants;

    FusedLocationProviderClient mFusedLocationClient;


    // Initialize recycler view to display restaurants
    private RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_restaurants);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(DisplayRestaurants.this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewRes);
        bottomNavigationView.setSelectedItemId(R.id.bottom_res); // se non va provare a cambiare questo
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        restaurants = new ArrayList<>();


        recyclerView = findViewById(R.id.restaurantsRecyclerView);
        mAdapter = new MyAdapter(restaurants);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(DisplayRestaurants.this);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        showProgressBar();
        requestNewLocationData();
        //getLastLocation();



    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {

                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            String url = composeUrl();
                            callRestaurantApi(url);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            longitude = mLastLocation.getLongitude();
            latitude = mLastLocation.getLatitude();
            String url = composeUrl();
            callRestaurantApi(url);

        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }




    private String composeUrl(){
        // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
        String url;
        url = api_url + "location="+latitude+","+longitude+"&radius=1000&type=restaurant&key="+APIKeyGoogle;
        System.out.println("QUESTO E L'URL");
        System.out.println(url);

        return url;
    }


    private void callRestaurantApi(String url){



        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // LIST OF RESTAURANTS
                        System.out.println(response);


                        try {
                            JSONArray results = response.getJSONArray("results");
                            System.out.println(results);

                            int numberToDisplay = 50;
                            if(results.length() < 50){
                                numberToDisplay = results.length();
                            }

                            for (int i=0; i < numberToDisplay; i++){
                                JSONObject res = results.getJSONObject(i);

                                try {
                                    Restaurant restaurant = new Restaurant(res.get("name").toString(), (Integer) res.get("user_ratings_total"), (Double) res.get("rating"), res.get("vicinity").toString());
                                    restaurants.add(restaurant);
                                } catch (Exception e) {
                                    continue;
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        hideProgressBar();
                        mAdapter.notifyDataSetChanged();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        // Add the request to the RequestQueue.
        hideProgressBar();
        queue.add(jsonRequest);

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.bottom_home:
                intent = new Intent(DisplayRestaurants.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;


            case R.id.bottom_fav:
                intent = new Intent(DisplayRestaurants.this, Favorites.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            case R.id.bottom_res:
                return true;


        }

        return false;
    }

    private void hideProgressBar() {
        findViewById(R.id.progressBarRestaurant).setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        findViewById(R.id.progressBarRestaurant).setVisibility(View.VISIBLE);
    }




}
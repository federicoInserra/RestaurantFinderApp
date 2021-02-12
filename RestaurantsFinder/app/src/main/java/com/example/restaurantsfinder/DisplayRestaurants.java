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



    private String latitude, longitude;

    //private static final String APIKeyGoogle = "AIzaSyC5B74OoRPoAYEeTyC91ML8g45rcPWMbIU";
    private static final String APIKeyGoogle = "AIzaSyBJlBV7wLuK_0Rq2WVaTP_PNllzvnSsC6o";

    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
    private String api_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";



    // Initializing other items


    int PERMISSION_ID = 44;

    private RequestQueue queue;

    private List<Restaurant> restaurants;

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
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        restaurants = new ArrayList<>();


        recyclerView = findViewById(R.id.restaurantsRecyclerView);
        mAdapter = new MyAdapter(restaurants);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(DisplayRestaurants.this);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        String url = composeUrl();
        callRestaurantApi(url);

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

                        mAdapter.notifyDataSetChanged();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        // Add the request to the RequestQueue.

        queue.add(jsonRequest);

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.bottom_home:
                intent = new Intent(DisplayRestaurants.this, MainActivity.class);
                startActivity(intent);


            case R.id.bottom_fav:
                intent = new Intent(DisplayRestaurants.this, Favorites.class);
                startActivity(intent);


        }

        return false;
    }




}
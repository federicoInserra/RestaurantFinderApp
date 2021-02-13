package com.example.restaurantsfinder;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, BottomNavigationView.OnNavigationItemSelectedListener {


    private List<Restaurant> restaurants = new ArrayList<>();

    private static final String APIKeyGoogle = "AIzaSyBJlBV7wLuK_0Rq2WVaTP_PNllzvnSsC6o";

    private String api_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.showRestaurantsButton);

        // Initialize bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        // Display restaurants on click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, DisplayRestaurants.class));

            }
        });

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Define actions of bottom bar

        Intent intent;

        switch (item.getItemId()) {
            case R.id.bottom_res:
                // Call display restaurants
                intent = new Intent(MainActivity.this, DisplayRestaurants.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;


            case R.id.bottom_fav:
                // Call favorites
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

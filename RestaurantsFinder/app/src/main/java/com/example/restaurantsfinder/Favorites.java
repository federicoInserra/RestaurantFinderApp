package com.example.restaurantsfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private RequestQueue queue;
    private String userID;

    private RecyclerView recyclerView;
    RecyclerView.Adapter favAdapter;

    private List<Restaurant> favs;

    // Firebase
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        queue = Volley.newRequestQueue(this);

        favs = new ArrayList<>();



        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) { userID = user.getEmail(); }
        else {
            Toast.makeText(Favorites.this, "Problem with user Login again", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Favorites.this, LoginActivity.class));
        }





        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewFav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_fav); // se non va provare a cambiare questo
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.favoritesRecyclerView);
        favAdapter = new FavAdapter(favs);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(Favorites.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(favAdapter);


        try {
            getFavs();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    private void getFavs() throws JSONException {

        String url = "http://10.0.2.2:5000/getFavs?userID="+ userID;


        JsonObjectRequest serverRequest = new JsonObjectRequest(Request.Method.GET, url, null , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    JSONArray results = response.getJSONArray("results");

                    if (results.length() == 0) {
                        Toast.makeText(Favorites.this, "No restaurants added yet!", Toast.LENGTH_LONG).show();
                    }


                    int numberToDisplay = 50;
                    if(results.length() < 50){
                        numberToDisplay = results.length();
                    }

                    for (int i=0; i < numberToDisplay; i++){
                        JSONObject res = results.getJSONObject(i);

                        try {
                            Restaurant restaurant = new Restaurant(res.get("name").toString(), (Integer) res.get("numberRatings"), (Double) res.get("rating"), res.get("vicinity").toString());
                            favs.add(restaurant);

                        } catch (Exception e) {
                            System.out.println(e);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                favAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });


        // Add the request to the RequestQueue.

        queue.add(serverRequest);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.bottom_home:
                intent = new Intent(Favorites.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;


            case R.id.bottom_res:
                intent = new Intent(Favorites.this, DisplayRestaurants.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            case R.id.bottom_fav:
                return true;


        }

        return false;
    }


}
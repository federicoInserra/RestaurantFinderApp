package com.example.restaurantsfinder;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Restaurant> values;
    private static RequestQueue queue;

    private static String  userID;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView restaurantName;
        public TextView rating;
        public TextView reviews;
        public TextView vicinity;
        public ImageView favorite;
        public View layout;



        public ViewHolder(View v) {
            super(v);
            layout = v;
            restaurantName = v.findViewById(R.id.restaurantNameText);
            rating = v.findViewById(R.id.ratingText);
            reviews = v.findViewById(R.id.numberOfReviewsText);
            vicinity = v.findViewById(R.id.vicinityText);
            favorite = v.findViewById(R.id.favoriteButton);

        }
    }



    public MyAdapter(List<Restaurant> myDataset) {
        values = myDataset;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.item_restaurant_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        queue = Volley.newRequestQueue(parent.getContext());

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Restaurant restaurant = values.get(position);

        holder.restaurantName.setText(restaurant.name);
        holder.vicinity.setText(restaurant.vicinity);
        holder.reviews.setText(restaurant.numberRatings.toString());
        holder.rating.setText(restaurant.rating.toString());
        holder.favorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar.make(v, "Added to favorites!", Snackbar.LENGTH_LONG).show();

                try {
                    // Add the restaurant to the user's favorites calling db
                    addFav(restaurant);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }


    public static void addFav(Restaurant restaurant) throws JSONException {

        String url = "http://10.0.2.2:5000/add_fav";
        JSONObject postData = new JSONObject();


        try {
            // Prepare data to save in db

            postData.put("name", restaurant.name);
            postData.put("vicinity", restaurant.vicinity);
            postData.put("rating", restaurant.rating);
            postData.put("numberRatings", restaurant.numberRatings);

            userID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            postData.put("userID", userID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest serverRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);

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

}
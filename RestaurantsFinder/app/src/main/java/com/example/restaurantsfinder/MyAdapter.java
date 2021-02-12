package com.example.restaurantsfinder;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Restaurant> values = new ArrayList<>();
    private static RequestQueue queue;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
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

    public void add(int position, Restaurant item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
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
                    callServer(restaurant);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

    public static void callServer(Restaurant restaurant) throws JSONException {


        String url = "http://10.0.2.2:5000/ciao";
        JSONObject postData = new JSONObject();
        try {
            postData.put("name", restaurant.name);
            postData.put("vicinity", restaurant.vicinity);
            postData.put("rating", restaurant.rating);
            postData.put("numberRatings", restaurant.numberRatings);
            postData.put("userID", "feffo");

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
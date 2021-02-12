package com.example.restaurantsfinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    private List<Restaurant> favs;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView restaurantName;
        public TextView rating;
        public TextView reviews;
        public TextView vicinity;
        public ImageView delete;
        public View layout;






        public ViewHolder(View v) {
            super(v);
            layout = v;
            restaurantName = v.findViewById(R.id.restaurantNameTextFav);
            rating = v.findViewById(R.id.ratingTextFav);
            reviews = v.findViewById(R.id.numberOfReviewsTextFav);
            vicinity = v.findViewById(R.id.vicinityTextFav);
            delete = v.findViewById(R.id.deleteButton);

            restaurantName.setText("CIAOOO");

        }
    }

    public FavAdapter(List<Restaurant> myDataset){ favs = myDataset; }


    @Override
    public FavAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.item_favorite_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        FavAdapter.ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Restaurant restaurant = favs.get(position);

        holder.restaurantName.setText(restaurant.name);
        holder.vicinity.setText(restaurant.vicinity);
        holder.reviews.setText(restaurant.numberRatings.toString());
        holder.rating.setText(restaurant.rating.toString());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //remove(position);
                Snackbar.make(v, "Removed from favorites!", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return favs.size();
    }

    public void remove(int position) {
        favs.remove(position);
        notifyItemRemoved(position);
    }
}

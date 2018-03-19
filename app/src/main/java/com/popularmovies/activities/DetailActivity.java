package com.popularmovies.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.popularmovies.R;
import com.popularmovies.classes.Movie;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Calendar;

/**
 * Activity used to display data about a particular movie. Started from MainActivity by clicking on
 * a MovieViewHolder in the RecyclerView.
 */
public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView ivPoster = findViewById(R.id.ivPoster);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvYear = findViewById(R.id.tvYear);
        TextView tvRating = findViewById(R.id.tvRating);
        TextView tvSynopsis = findViewById(R.id.tvSynopsis);

        // get the passed movie and display its data

        Movie movie = getIntent().getParcelableExtra("movie");

        URL movieImageURL = movie.getImageUrl();
        if(movieImageURL != null) {
            Picasso.get().load(movieImageURL.toString()).into(ivPoster);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(movie.getReleaseDate());

        tvTitle.setText(movie.getTitle());
        tvYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        tvRating.setText(String.valueOf(movie.getUserRating()) + getString(R.string.out_of_10));
        tvSynopsis.setText(movie.getSynopsis());
    }
}

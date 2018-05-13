package com.popularmovies.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.popularmovies.R;
import com.popularmovies.classes.Movie;
import com.popularmovies.classes.Review;
import com.popularmovies.database.FavoritesDBHelper;
import com.popularmovies.utils.JsonUtils;
import com.popularmovies.utils.NetworkUtils;
import com.popularmovies.utils.UrlUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

/**
 * Activity used to display data about a particular movie. Started from MainActivity by clicking on
 * a MovieViewHolder in the RecyclerView.
 */
public class DetailActivity extends Activity {
	boolean movieInFavorites = false;

    Button btnFavorites, btnTrailer;
    TextView tvReviews;

	FavoritesDBHelper dbHelper;

    Movie movie;
    List<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        btnFavorites = findViewById(R.id.btnFavorites);
        btnTrailer = findViewById(R.id.btnTrailer);

        ImageView ivPoster = findViewById(R.id.ivPoster);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvYear = findViewById(R.id.tvYear);
        TextView tvRating = findViewById(R.id.tvRating);
        TextView tvSynopsis = findViewById(R.id.tvSynopsis);
        tvReviews = findViewById(R.id.tvReviews);

        dbHelper = new FavoritesDBHelper(this);

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if(!movieInFavorites) { // add to favorites
					setBtnFavorites(true);
            		dbHelper.insertMovie(movie);
            		if(!reviews.isEmpty()) {
						dbHelper.insertReviews(reviews, movie);
            		}
				}
				else { // remove from favorites
					setBtnFavorites(false);
					dbHelper.deleteMovie(movie);
				}
            }
        });

        btnTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				queryTrailers(movie);
            }
        });

        // get the passed movie and display its data

		movie = getIntent().getParcelableExtra("movie");

		if (movie == null) {
			Toast.makeText(this, "Couldn't load movie data", Toast.LENGTH_LONG).show();
			finish();
		}

		if(dbHelper.movieInDatabase(movie)) {
			setBtnFavorites(true);
		}

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

        queryReviews(movie);
    }

	/**
	 * Fetch the reviews for the selected movie.
	 * @param movie the selected movie.
	 */
	private void queryReviews(Movie movie) {
    	if (movieInFavorites) {
    		reviews = dbHelper.getReviewsForMovie(movie);
			setReviewsTextView(reviews);
		}

        URL reviewsURL = UrlUtils.buildTrailersOrReviewsUrl(
                String.valueOf(movie.getId()),
                getString(R.string.TMDB_API_KEY),
                UrlUtils.reviewsPath
        );

        new reviewQueryTask().execute(reviewsURL);
    }

    /**
     * AsyncTask for fetching review data from TMDB.
     */
    public class reviewQueryTask extends AsyncTask<URL, Void, List<Review>> {
        @Override
        protected List<Review> doInBackground(URL... params) {
            URL reviewsURL = params[0];
            List<Review> reviews = null;

            try {
                while (!isOnline()) {}
                String response = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
                reviews = JsonUtils.parseReviewJsonArray(response);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return reviews;
        }

        @Override
        protected void onPostExecute(List<Review> reviewList) {
        	reviews = reviewList;
			setReviewsTextView(reviewList);
        }
    }

	/**
	 * Displays the review data in the appropriate TextView.
	 * @param reviews a list of reviews.
	 */
	private void setReviewsTextView(List<Review> reviews) {
		StringBuilder sb = new StringBuilder();
		for (Review review : reviews) {
			sb.append(review.toString());
			sb.append("\n\n");
		}
		tvReviews.setText(sb.toString().trim());
	}


	private void queryTrailers(Movie movie) {
        URL trailersURL = UrlUtils.buildTrailersOrReviewsUrl(
                String.valueOf(movie.getId()),
                getString(R.string.TMDB_API_KEY),
                UrlUtils.trailersPath
        );

        new trailerQueryTask().execute(trailersURL);
	}

	/**
	 * AsyncTask for fetching trailer data from TMDB.
	 */
	public class trailerQueryTask extends AsyncTask<URL, Void, List<String>> {
		@Override
		protected List<String> doInBackground(URL... params) {
			URL trailersURL = params[0];
			List<String> trailerIds = null;

			try {
				while (!isOnline()) {}
				String response = NetworkUtils.getResponseFromHttpUrl(trailersURL);
				trailerIds = JsonUtils.parseVideoIdJsonArray(response);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return trailerIds;
		}

		@Override
		protected void onPostExecute(List<String> trailerIds) {
			String videoUrl = UrlUtils.buildYoutubeVideoURL(trailerIds.get(0)).toString();
			Intent playVideo = new Intent(Intent.ACTION_VIEW);
			playVideo.setData(Uri.parse(videoUrl));
			startActivity(playVideo);
		}
	}

	/**
	 * Sets the add to/remove from favorites to the appropriate state.
	 * @param state the new state of the favorites button (true: movie added to favorites, false:
	 *              movie removed from favorites).
	 */
	private void setBtnFavorites(boolean state) {
		if (!state) {
			movieInFavorites = false;
			btnFavorites.setText(getString(R.string.favorites_add));
		}
		else {
			movieInFavorites = true;
			btnFavorites.setText(getString(R.string.favorites_remove));
		}
	}

    /**
     * Checks if the device currently has Internet access.
     * @return true if the device is connected to the Internet, false otherwise.
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnected();
    }
}

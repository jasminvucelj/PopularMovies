package com.popularmovies.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.popularmovies.adapters.MoviesAdapter;
import com.popularmovies.R;
import com.popularmovies.classes.Movie;
import com.popularmovies.utils.JsonUtils;
import com.popularmovies.utils.NetworkUtils;
import com.popularmovies.utils.UrlUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * The main activity of the app, contains the RecyclerView which display the movies as a grid, and
 * a Spinner to select the criterion for movie selection.
 */
public class MainActivity extends Activity implements MoviesAdapter.MoviesAdapterOnClickHandler {
    private static final int SPAN_COUNT=4;

    private String sortBy;
    private List<Movie> mList = null;

    private SharedPreferences sharedPrefs;

    private Spinner spinnerSortBy;
    private ProgressBar progressBar;
    private RecyclerView rvMovies;
    private MoviesAdapter moviesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        progressBar = findViewById(R.id.progressBar);
        rvMovies = findViewById(R.id.rvMovies);


        // setup spinner - change the movie display on selection change

        spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = getResources().getStringArray(R.array.options_values)[position];
                queryMovies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        // setup shared preferences - store the position of the selected sorting criterion

        sharedPrefs = getPreferences(Context.MODE_PRIVATE);

        if(sharedPrefs.contains("sortby")) {
            int position = sharedPrefs.getInt("sortby", 0);
            spinnerSortBy.setSelection(position);
            sortBy = getResources().getStringArray(R.array.options_values)[position];
        }
        else {
            spinnerSortBy.setSelection(0);
            sortBy = getResources().getStringArray(R.array.options_values)[0];

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt("sortby", 0);
            editor.apply();
        }


        // setup recycler view & adapter

        rvMovies.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(
                this,
                SPAN_COUNT,
                GridLayoutManager.VERTICAL,
                false);
        rvMovies.setLayoutManager(layoutManager);

        if (mList == null) {
            queryMovies();
        }
    }


    /**
     * Hide the RecyclerView, show the progress indicator, build the movie request URL, and start an
     * AsyncTask to fetch the movie data.
     */
    private void queryMovies() {
        progressBar.setVisibility(View.VISIBLE);
        rvMovies.setVisibility(View.GONE);

        URL moviesURL = UrlUtils.buildMoviesRequestUrl(
                sortBy,
                getString(R.string.TMDB_API_KEY)
        );

        new movieQueryTask().execute(moviesURL);
    }


    /**
     * When a movie is selected from the RecyclerView, start DetailActivity to display its data.
     * @param movie the selected movie (to be passed to DetailActivity).
     */
    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent (this, DetailActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }


    /**
     * AsyncTask for fetching movie data from TMDB.
     */
    public class movieQueryTask extends AsyncTask<URL, Void, List<Movie>> {
        /**
         * Sends a HTTP request to the given endpoint and handles the JSON response.
         * @param params the URL for the HTTP request.
         * @return A list of movies obtained by parsing the JSON response.
         */
        @Override
        protected List<Movie> doInBackground(URL... params) {
            URL moviesURL = params[0];
            List<Movie> moviesList = null;

            try {
                while (!isOnline()) {}
                String response = NetworkUtils.getResponseFromHttpUrl(moviesURL);
                moviesList = JsonUtils.parseMovieJsonArray(response);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return moviesList;
        }

        /**
         * Sets up the RecyclerView after the movie data has been fetched.
         * @param moviesList list of movies - data for the RecyclerView.
         */
        @Override
        protected void onPostExecute(List<Movie> moviesList) {
            setupRecyclerView(moviesList);
        }
    }


    /**
     * Binds the movie data to an adapter, and then to the RecyclerView, and shows the RecyclerView,
     * hiding the progress indicator.
     * @param moviesList list of movies - data for the RecyclerView.
     */
    private void setupRecyclerView(List<Movie> moviesList) {
        if (moviesList != null && !moviesList.isEmpty()) {
            mList = moviesList;
            moviesAdapter = new MoviesAdapter(mList, MainActivity.this);
            rvMovies.setAdapter(moviesAdapter);
            rvMovies.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", (ArrayList<Movie>) mList);

        sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("sortby", spinnerSortBy.getSelectedItemPosition());
        editor.apply();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mList = savedInstanceState.getParcelableArrayList("movies");

        sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        int position = sharedPrefs.getInt("sortby", 0);
        spinnerSortBy.setSelection(position);
        sortBy = getResources().getStringArray(R.array.options_values)[position];

        setupRecyclerView(mList);
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

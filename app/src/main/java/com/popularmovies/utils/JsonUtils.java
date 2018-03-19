package com.popularmovies.utils;

import com.popularmovies.classes.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonUtils {

    /**
     * Parses a JSON string containing a single movie.
     * @param in string in JSON format, containing a single movie.
     * @return a single Movie object.
     */
    private static Movie parseMovieJson(String in) {
        try {
            JSONObject json = new JSONObject(in);

            String title = json.getString("original_title");
            String imageRelativePath = json.getString("poster_path");
            URL imageUrl = UrlUtils.buildImageUrl(imageRelativePath, "w185");
            String synopsis = json.getString("overview");
            double userRating = Double.parseDouble(json.getString("vote_average"));

            Date releaseDate = null;
            try {
                String sdfPattern = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(sdfPattern);
                releaseDate = sdf.parse(json.getString("release_date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return new Movie(title, imageUrl, synopsis, userRating, releaseDate);
        }  catch (JSONException e) {
            return null;
        }
    }


    /**
     * Parses a JSON string containing a list of movies.
     * @param in string in JSON format, containing a list of movies.
     * @return a list of Movie objects.
     */
    public static List<Movie> parseMovieJsonArray(String in) {
        List<Movie> movieList = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(in);
            JSONArray results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject movieJson = results.getJSONObject(i);
                Movie movie = parseMovieJson(movieJson.toString());
                movieList.add(movie);
            }

            return movieList;

        }  catch (JSONException e) {
            return null;
        }
    }
}

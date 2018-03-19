package com.popularmovies.utils;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {

    /**
     * Constructs a URL for a movie image of a given size from a given relative path.
     * @param relativePath the path of the image, to be appended to the base URL.
     * @param imageSize the size of the requested image.
     * @return the complete image URL.
     */
    @SuppressWarnings("SameParameterValue")
    public static URL buildImageUrl(String relativePath, String imageSize) {
        final String imagesBaseUrl = "http://image.tmdb.org/t/p";

        Uri builtUri = Uri.parse(imagesBaseUrl).buildUpon()
                .appendPath(imageSize)
                .appendPath(relativePath.substring(1))
                .build();

        return UriToUrl(builtUri);
    }

    /**
     * Constructs a URL that requests a list of movies from a given endpoint, with a given API key.
     * @param endpoint the path appended to the base URL, determines the criteria for movie selection.
     * @param apiKey the API key required for a valid request.
     * @return the complete URL for the required request.
     */
    public static URL buildMoviesRequestUrl(String endpoint, String apiKey) {
        final String moviesBaseUrl = "http://api.themoviedb.org/3";
        final String moviesPath = "movie";
        final String PARAM_API_KEY = "api_key";

        Uri builtUri = Uri.parse(moviesBaseUrl).buildUpon()
                .appendPath(moviesPath)
                .appendPath(endpoint)
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        return UriToUrl(builtUri);
    }


    /**
     * Converts a Uri address format into URL.
     * @param builtUri Uri address to be converted.
     * @return address in URL format.
     */
    @Nullable
    private static URL UriToUrl(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}

package com.popularmovies.utils;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {

    public static final String trailersPath = "videos";
    public static final String reviewsPath = "reviews";

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
     * Constructs a URL that requests either a list of trailers or reviews for a given
     * movie, with a given API key.
     * @param movieId the ID of the movie for trailers/reviews of which the URL is to request.
     * @param apiKey the API key required for a valid request.
     * @param endpoint the desired endpoint (trailers or reviews)
     * @return the complete URL for the required request.
     */
    public static URL buildTrailersOrReviewsUrl(String movieId, String apiKey, String endpoint) {
        final String moviesBaseUrl = "http://api.themoviedb.org/3";
        final String moviesPath = "movie";
        final String PARAM_API_KEY = "api_key";

        Uri builtUri = Uri.parse(moviesBaseUrl).buildUpon()
                .appendPath(moviesPath)
                .appendPath(movieId)
                .appendPath(endpoint)
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        return UriToUrl(builtUri);
    }


	/**
	 * Constructs a YouTube video URL of a trailer from the ID of that trailer.
	 * @param videoId the ID of the requested video.
	 * @return the complete YouTube URL of the requested video.
	 */
    public static URL buildYoutubeVideoURL(String videoId) {
    	final String videoBaseUrl = "https://www.youtube.com";
    	final String watchPath = "watch";
    	final String PARAM_VIDEO = "v";

		Uri builtUri = Uri.parse(videoBaseUrl).buildUpon()
				.appendPath(watchPath)
				.appendQueryParameter(PARAM_VIDEO, videoId)
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

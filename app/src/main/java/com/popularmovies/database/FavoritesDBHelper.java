package com.popularmovies.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.popularmovies.classes.Movie;
import com.popularmovies.classes.Review;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper class for interaction with the SQLite database.
 */
public class FavoritesDBHelper extends SQLiteOpenHelper {

	// Database info
	private static final String DATABASE_NAME = "favorites.db";
	private static final int DATABASE_VERSION = 1;

	// Table names
	private static final String TABLE_MOVIES = "movies"; // "todos"
	private static final String TABLE_REVIEWS = "reviews"; // "tags"

	// Common column names
	private static final String KEY_ID = "id";

	// Movies table - column nmaes
	private static final String KEY_TITLE = "title";
	private static final String KEY_IMAGE_URL = "imageUrl";
	private static final String KEY_SYNOPSIS = "synopsis";
	private static final String KEY_USER_RATING = "userRating";
	private static final String KEY_RELEASE_DATE = "releaseDate";

	// Reviews table - column names
	private static final String KEY_AUTHOR = "author";
	private static final String KEY_CONTENT = "content";
	private static final String KEY_MOVIE_ID = "movieId";


	// Table Create Statements
	// Movies table create statement
	private static final String CREATE_TABLE_MOVIES = "CREATE TABLE " + TABLE_MOVIES
			+ "("
			+ KEY_ID + " INTEGER,"
			+ KEY_TITLE + " TEXT,"
			+ KEY_IMAGE_URL + " TEXT,"
			+ KEY_SYNOPSIS + " TEXT,"
			+ KEY_USER_RATING + " DOUBLE,"
			+ KEY_RELEASE_DATE + " LONG"
			+ ")";

	// Reviews table create statement
	private static final String CREATE_TABLE_REVIEWS = "CREATE TABLE " + TABLE_REVIEWS
			+ "("
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ KEY_AUTHOR + " TEXT,"
			+ KEY_CONTENT + " TEXT,"
			+ KEY_MOVIE_ID + " INTEGER NOT NULL"
			+ ")";


	public FavoritesDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_MOVIES);
		db.execSQL(CREATE_TABLE_REVIEWS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);

		// create new tables
		onCreate(db);
	}

	//region movies

	/*
	 * Insert a Movie
	 */
	public long insertMovie(Movie movie) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, movie.getId());
		values.put(KEY_TITLE, movie.getTitle());
		values.put(KEY_IMAGE_URL, movie.getImageUrl().toString());
		values.put(KEY_SYNOPSIS, movie.getSynopsis());
		values.put(KEY_USER_RATING, movie.getUserRating());
		values.put(KEY_RELEASE_DATE, movie.getReleaseDate().getTime());

		// insert row
		return db.insert(TABLE_MOVIES, null, values);
	}


	/*
	 * Get all Movies in database
	 * */
	public List<Movie> getAllMovies() {
		List<Movie> movies = new ArrayList<>();
		String selectQuery = "SELECT  * FROM " + TABLE_MOVIES;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Movie movie = new Movie();
				movie.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				movie.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
				try {
					movie.setImageUrl(new URL(c.getString(c.getColumnIndex(KEY_IMAGE_URL))));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				movie.setSynopsis(c.getString(c.getColumnIndex(KEY_SYNOPSIS)));
				movie.setUserRating(c.getDouble(c.getColumnIndex(KEY_USER_RATING)));
				movie.setReleaseDate(new Date(c.getLong(c.getColumnIndex(KEY_RELEASE_DATE))));

				// adding to movie list
				movies.add(movie);
			} while (c.moveToNext());
		}

		c.close();
		return movies;
	}


	public boolean movieInDatabase(Movie movie) {
		List<Movie> movieList = getAllMovies();

		for(Movie m : movieList) {
			if(m.equals(movie)) {
				return true;
			}
		}

		return false;
	}


	/*
	 * Delete a Movie
	 */
	public void deleteMovie(Movie movie) {
		long movieId = movie.getId();
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MOVIES, KEY_ID + " = ?",
				new String[] { String.valueOf(movieId) });

		// delete all reviews for that movie

		List<Review> reviewsOfDeletedMovie = getReviewsForMovie(movie);
		for (Review review : reviewsOfDeletedMovie) {
			deleteReview(review);
		}
	}

	// endregion

	// region reviews

	/*
	 * Insert a Review for a given Movie
	 */
	public long insertReview(Review review, Movie movie) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_AUTHOR,review.getAuthor());
		values.put(KEY_CONTENT, review.getContent());
		values.put(KEY_MOVIE_ID, movie.getId());

		// insert row
		return db.insert(TABLE_REVIEWS, null, values);
	}

	/*
	 * Insert a list of Reviews for a given Movie
	 */
	public void insertReviews(List<Review> reviews, Movie movie) {
		for (Review review : reviews) {
			insertReview(review, movie);
		}
	}


	/*
	 * Get all Reviews for a single Movie
	 * */
	public List<Review> getReviewsForMovie(Movie movie) {
		List<Review> reviews = new ArrayList<>();
		long movieId = movie.getId();
		String selectQuery = "SELECT  * FROM " + TABLE_REVIEWS
				+ " WHERE " + KEY_MOVIE_ID + " = " + movieId;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Review review = new Review();
				review.setAuthor(c.getString(c.getColumnIndex(KEY_AUTHOR)));
				review.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));

				// adding to review list
				reviews.add(review);
			} while (c.moveToNext());
		}

		c.close();
		return reviews;
	}

	/*
	 * Get the id of a Review
	 */
	private long getReviewId(Review queryReview) {
		String selectQuery = "SELECT  * FROM " + TABLE_REVIEWS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Review review = new Review();
				review.setAuthor(c.getString(c.getColumnIndex(KEY_AUTHOR)));
				review.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));

				if(review.equals(queryReview)) {
					long id = c.getLong(c.getColumnIndex(KEY_ID));
					c.close();
					return id;
				}
			} while (c.moveToNext());
		}
		return -1;
	}

	/*
	 * Delete a Review
	 */
	public void deleteReview(Review review) {
		long reviewId = getReviewId(review);
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_REVIEWS, KEY_ID + " = ?",
				new String[] { String.valueOf(reviewId) });
	}

	// endregion
}
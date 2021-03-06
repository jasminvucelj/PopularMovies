package com.popularmovies.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.Date;
import java.util.Objects;


/**
 * The class used to store the relevant data about a movie. Implements the Parcelable interface for
 * passing the data between activities.
 */
public class Movie implements Parcelable {
    private int id;
    private String title;
    private URL imageUrl;
    private String synopsis;
    private double userRating;
    private Date releaseDate;

    public Movie() {

    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Movie movie = (Movie) o;

		return id == movie.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public Movie(int id, String title, URL imageUrl, String synopsis, double userRating, Date releaseDate) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        imageUrl = (URL) in.readValue(URL.class.getClassLoader());
        synopsis = in.readString();
        userRating = in.readDouble();
        long tmpReleaseDate = in.readLong();
        releaseDate = tmpReleaseDate != -1 ? new Date(tmpReleaseDate) : null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeValue(imageUrl);
        dest.writeString(synopsis);
        dest.writeDouble(userRating);
        dest.writeLong(releaseDate != null ? releaseDate.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

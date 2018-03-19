package com.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.popularmovies.R;
import com.popularmovies.classes.Movie;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private final List<Movie> movieList;
    private final MoviesAdapterOnClickHandler clickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    /**
     * Constructor for the adapter.
     * @param movieList the list of movies to display.
     * @param clickHandler the item click handler.
     */
    public MoviesAdapter(List<Movie> movieList, MoviesAdapterOnClickHandler clickHandler) {
        this.movieList = movieList;
        this.clickHandler = clickHandler;
    }

    /**
     * Inflates a new view from the XML layout to be used as a ViewHolder for a movie in the
     * RecylerView.
     * @param parent ViewGroup that is to contain the new ViewHolder.
     * @param viewType type of item in the parent ViewGroup (not used).
     * @return a new MovieViewHolder object from the inflated view.
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.movie_list_item,
                parent,
                false);

        return new MovieViewHolder(view);
    }


    /**
     * For a ViewHolder at a certain position, displays the poster image of a movie at that
     * position.
     * @param holder the ViewHolder that represents the movie at the given position.
     * @param position the position of the movie in the RecyclerView adapter dataset.
     */
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        URL movieImageURL = movie.getImageUrl();
        if(movieImageURL != null) {
            Picasso.get().load(movieImageURL.toString()).into(holder.listItemMovieView);
        }

    }

    /**
     * Returns the number of items to display.
     * @return size of the movie list.
     */
    @Override
    public int getItemCount() {
        return movieList.size();
    }

    /**
     * Custom ViewHolder class to represent each movie item in the RecyclerView adapter dataset.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView listItemMovieView;

        /**
         * Constructor, sets up the itemView and binds the ImageView in it, as well as the
         * OnClickListener for the item.
         * @param itemView the view inflated to hold the item.
         */
        MovieViewHolder(View itemView) {
            super(itemView);
            listItemMovieView = itemView.findViewById(R.id.iv_movie);
            itemView.setOnClickListener(this);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked.
         */
        @Override
        public void onClick(View v) {
            Movie movie = movieList.get(getAdapterPosition());
            clickHandler.onClick(movie);
        }
    }
}

package com.alelievangelista.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aevangelista on 15-07-14.
 */
public class MovieElement implements Parcelable {

    private String movieId;
    private String movieName;
    private String moviePoster;
    private String movieBackdrop;
    private String movieSynopsis;
    private String movieRating;
    private String movieVotes;
    private String movieDate;


    public MovieElement (String movieId, String name, String poster, String backdrop, String synopsis,
                         String rating, String votes, String releaseDate) {
        this.movieId = movieId;
        this.movieName = name;
        this.moviePoster = poster;
        this.movieBackdrop = backdrop;
        this.movieSynopsis = synopsis;
        this.movieRating = rating;
        this.movieVotes = votes;
        this.movieDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Movie ID: " + this.movieId + "\n" +
                "Name: " + this.movieName  + "\n"
                + "Poster: " + this.moviePoster + "\n" +
                "Backdrop: " + this.movieBackdrop + "\n" +
                "Synopsis: " + this.movieSynopsis  + "\n" +
                "Rating: " + this.movieRating  + "\n" +
                "Votes: " + this.movieVotes  + "\n" +
                "Date: " + this.movieDate;


    }

    /**
     * Getters and Setters
     */

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String name) {
        this.movieId = name;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String name) {
        this.movieName = name;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String a) {
        this.moviePoster = a;
    }

    public String getMovieBackdrop() {
        return movieBackdrop;
    }

    public void setMovieBackdrop(String a) {
        this.movieBackdrop = a;
    }

    public String getMovieSynopsis() {
        return movieSynopsis;
    }

    public void setMovieSynopsis(String a) {
        this.movieSynopsis = a;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String a) {
        this.movieRating = a;
    }

    public String getMovieVotes() {
        return movieVotes;
    }

    public void setMovieVotes(String a) {
        this.movieVotes = a;
    }

    public String getMovieDate() {
        return movieDate;
    }

    public void setMovieDate(String a) {
        this.movieDate = a;
    }

    /**
     * For use with Parcelable creator
     */
    private MovieElement(Parcel in){
        movieId = in.readString();
        movieName = in.readString();
        moviePoster = in.readString();
        movieBackdrop = in.readString();
        movieSynopsis = in.readString();
        movieRating = in.readString();
        movieVotes = in.readString();
        movieDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeString(movieId);
        p.writeString(movieName);
        p.writeString(moviePoster);
        p.writeString(movieBackdrop);
        p.writeString(movieSynopsis);
        p.writeString(movieRating);
        p.writeString(movieVotes);
        p.writeString(movieDate);
    }

    /**
     * Receiving and decoding parcels
     */
    public final Parcelable.Creator<MovieElement> CREATOR = new Parcelable.Creator<MovieElement>(){

        @Override
        public MovieElement createFromParcel(Parcel parcel) {
            return new MovieElement(parcel);
        }

        @Override
        public MovieElement[] newArray(int size) {
            return new MovieElement[size];
        }
    };
}

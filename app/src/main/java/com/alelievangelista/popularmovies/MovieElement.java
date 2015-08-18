package com.alelievangelista.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aevangelista on 15-07-14.
 */
public class MovieElement implements Parcelable {

    private String movieName;
    private String moviePoster;
    private String movieSynopsis;
    private String movieRating;
    private String movieDate;


    public MovieElement (String name, String poster, String synopsis, String rating, String releaseDate) {
        this.movieName = name;
        this.moviePoster = poster;
        this.movieSynopsis = synopsis;
        this.movieRating = rating;
        this.movieDate = releaseDate;
    }

    /**
     * For use with Parcelable creator
     */
    private MovieElement(Parcel in){
        movieName = in.readString();
        moviePoster = in.readString();
        movieSynopsis = in.readString();
        movieRating = in.readString();
        movieDate = in.readString();
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

    public String getMovieDate() {
        return movieDate;
    }

    public void setMovieDate(String a) {
        this.movieDate = a;
    }

    @Override
    public String toString() {
        return "Name: " + this.movieName  + "\n"
                + "Poster: " + this.moviePoster + "\n" +
                "Synopsis: " + this.movieSynopsis  + "\n" +
                "Rating: " + this.movieRating  + "\n" +
                "Date: " + this.movieDate;


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeString(movieName);
        p.writeString(moviePoster);
        p.writeString(movieSynopsis);
        p.writeString(movieRating);
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

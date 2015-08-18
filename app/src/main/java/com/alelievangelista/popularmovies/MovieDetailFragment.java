package com.alelievangelista.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormatSymbols;

public class MovieDetailFragment extends Fragment {

    private MovieElement movieElement;
    public TextView movieName;
    public ImageView movieImage;
    public TextView movieYear;
    public TextView movieMonth;
    public TextView movieRating;
    public TextView movieSynopsis;


    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.getParcelable("BUNDLE_MOVIE") != null) {
            movieElement = savedInstanceState.getParcelable("BUNDLE_MOVIE");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(movieElement != null){
            outState.putParcelable("BUNDLE_MOVIE", movieElement);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.movie_detail_fragment, container, false);

        //Fragment components
        movieName = (TextView) rootView.findViewById(R.id.movieName);
        movieImage = (ImageView) rootView.findViewById(R.id.movieImage);

        movieYear = (TextView) rootView.findViewById(R.id.movieYear);
        movieMonth = (TextView) rootView.findViewById(R.id.movieMonth);

        movieRating = (TextView) rootView.findViewById(R.id.movieRating);
        movieSynopsis = (TextView) rootView.findViewById(R.id.movieSynopsis);

        //Set up
        if(movieElement != null){
            loadMovieDetails(movieElement);
        }

        return rootView;
    }


    public void loadMovieDetails(MovieElement m) {

        movieElement = m;

        //Determine movie name
        movieName.setText(getName(movieElement.getMovieName()));

        //Determine date components
        String[] splitDate = movieElement.getMovieDate().split("-");
        if (splitDate.length == 3){
            movieMonth.setText(getMonth(splitDate[1]));
            movieYear.setText(splitDate[0]);
        }else{
            movieMonth.setText("");
            movieYear.setText("");
        }

        //Determine movie rating
        movieRating.setText(getRating(movieElement.getMovieRating()));

        //Determine synopsis
        movieSynopsis.setText(getSynopsis(movieElement.getMovieSynopsis()));

        //Set movie poster image
        Picasso.with(getActivity()).load(movieElement.getMoviePoster()).into(movieImage);


    }

    private String getName(String n){
        if(n != null){
            return n;
        }else{
            return "";
        }
    }

    private String getRating(String r){
        if(r != null){
            //Transform raw rating
            int i = Double.valueOf(r).intValue();
            String u = String.valueOf(i);

            return u + "/10";
        }else{
            return "UNRATED";
        }
    }

    private String getSynopsis(String s){
        if(s.equals(null)|| s.equals("No overview found.") || s.equals("null")){
            return "";
        }else{

            return s;
        }
    }

    private String getMonth(String m) {
        int intMonth = Integer.parseInt(m);
        String monthString = "";
        monthString = new DateFormatSymbols().getMonths()[intMonth-1];

        return monthString;
    }
}

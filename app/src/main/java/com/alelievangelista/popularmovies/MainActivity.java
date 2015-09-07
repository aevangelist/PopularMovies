package com.alelievangelista.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements OnMovieSelectListener {

    public static final String MOVIE_DETAILS_TAG = "MOVIE_DETAILS";
    public static final String MOVIE_MAIN_TAG = "MOVIE_MAIN";

    public boolean isTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load movie fragment
        MovieFragment movieFragment = new MovieFragment();
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(MOVIE_MAIN_TAG)
                .add(R.id.container, movieFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();


        if (findViewById(R.id.movie_detail_fragment) != null) {
            isTwoPane = true;
        }

    }

    @Override
    public void onSelect(MovieElement currMovie) {

        MovieDetailFragment movieDetailFragment = (MovieDetailFragment)getSupportFragmentManager().
                findFragmentByTag(MOVIE_DETAILS_TAG);

        if(movieDetailFragment != null){ //Restoring from a previous session
            movieDetailFragment.loadMovieDetails(currMovie);
        }else{


            MovieDetailFragment fragment = new MovieDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(MOVIE_DETAILS_TAG).commit();

            getSupportFragmentManager().executePendingTransactions();

            fragment.loadMovieDetails(currMovie);
        }
    }
}

package com.alelievangelista.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnMovieSelectListener {

    private ArrayList<MovieElement> movieElements;
    private PreferenceChangeListener listener = null;
    public ImageAdapter adapter;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = (GridView) findViewById(R.id.gridview);

        if(savedInstanceState == null || !savedInstanceState.containsKey("BUNDLE_MOVIES")) {
            movieElements = new ArrayList<MovieElement>();
            GetMovieDataTask task = new GetMovieDataTask();
            task.execute();

        }
        else {
            movieElements = savedInstanceState.getParcelableArrayList("BUNDLE_MOVIES");
        }

        adapter = new ImageAdapter(this, R.layout.movie_element, movieElements);
        gridView.setAdapter(adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(listener);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("BUNDLE_MOVIES", movieElements);
    }

    // Handle preferences changes
    private class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs,
                                              String key) {
            GetMovieDataTask task = new GetMovieDataTask();
            task.execute();
        }
    }

    @Override
    /**
     * Set up menu overflow items
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    /**
     * Control menu behaviour when a menu item has been selected
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Settings
                Intent i = new Intent(getApplicationContext(), PrefActivity.class);
                startActivityForResult(i,0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSelect(MovieElement currMovie) {

        MovieDetailFragment movieDetailFragment = (MovieDetailFragment)getSupportFragmentManager().findFragmentByTag("MOVIE_DETAILS");

        if (movieDetailFragment != null){
            movieDetailFragment.loadMovieDetails(currMovie);
        } else {
            MovieDetailFragment fragment = new MovieDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null).commit();

            getSupportFragmentManager().executePendingTransactions();

            fragment.loadMovieDetails(currMovie);
        }

    }

    private class GetMovieDataTask extends AsyncTask<Void, Void, ArrayList<MovieElement>> {

        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        //Read Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String API_URL = "https://api.themoviedb.org/3";
        String API_IMAGE_URL = "https://image.tmdb.org/t/p/w300";

        String API_TAG = "api_key=";
        String API_KEY = "387e9b1680afd71a65f13ee0670dc6c6";
        String API_PARAM1 = "/movie/popular?";
        String API_PARAM2 = "/movie/top_rated?";


        //JSON node names
        private static final String TAG_RESULTS = "results";
        private static final String TAG_TITLE = "original_title";
        private static final String TAG_OVERVIEW = "overview";
        private static final String TAG_DATE = "release_date";
        private static final String TAG_RATING = "vote_average";
        private static final String TAG_POSTER = "poster_path";

        private int counter = 0;
        private JSONArray arr;
        private ArrayList<MovieElement> result = new ArrayList<MovieElement>();
        private JSONObject obj;


        @Override
        protected void onPostExecute(ArrayList<MovieElement> result) {
            super.onPostExecute(result);
            adapter.setItemList(result);
            adapter.notifyDataSetChanged();
            movieElements = adapter.getItemList(); //Set the movie array
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading movies...");
            dialog.show();
        }

        @Override
        protected ArrayList<MovieElement> doInBackground(Void... params) {

            JSONParser jParser = new JSONParser();

            String apiParams = findSortingMethod(sharedPref);
            String url = API_URL + apiParams + API_TAG + API_KEY;

            try {
                //Get JSON from URL
                obj = jParser.getJSONFromUrl(url);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            arr = obj.getJSONArray(TAG_RESULTS);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject o = arr.getJSONObject(i);
                                MovieElement m = convertMovie(o);
                                result.add(m);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }finally{
                return result;
            }
        }

        /**
         * Handle sorting method types
         */
        private String findSortingMethod(SharedPreferences s){

            String sortingMethod = s.getString("PREF_SORT", "1");

            if(sortingMethod.equals("2")){
                return API_PARAM2;
            } else{
                return API_PARAM1;
            }

        }


        private MovieElement convertMovie(JSONObject obj) throws JSONException {

            String name = obj.getString(TAG_TITLE);
            String poster = obj.getString(TAG_POSTER);
            String synopsis = obj.getString(TAG_OVERVIEW);
            String rating = obj.getString(TAG_RATING);
            String releaseDate = obj.getString(TAG_DATE);

            return new MovieElement(name, API_IMAGE_URL + poster, synopsis, rating, releaseDate);
        }

    }

}

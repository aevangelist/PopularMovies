package com.alelievangelista.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements OnMovieSelectListener {

    public ImageAdapter adapter;

    private ArrayList<MovieElement> movieElements;
    private PreferenceChangeListener listener = null;
    private SharedPreferences prefs;

    //SharedPreferences for favourites
    public static final String PREFS = "PREF_FAVOURITES";
    public static final String PREFS_SORT = "PREF_SORT";
    public static final String BUNDLE_MOVIES = "BUNDLE_MOVIES";
    public static final String MOVIE_DETAILS_TAG = "MOVIE_DETAILS";

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public boolean isTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_fragment) != null) {
            isTwoPane = true;
        }

        GridView gridView = (GridView) findViewById(R.id.gridview);

        if(savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_MOVIES)) {
            movieElements = new ArrayList<MovieElement>();
            if(isNetworkAvailable()){
                GetMovieDataTask task = new GetMovieDataTask();
                task.execute();
            }

        }
        else {
            movieElements = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES);
        }

        adapter = new ImageAdapter(this, R.layout.movie_element, movieElements);
        gridView.setAdapter(adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(listener);

        //Set up SharedPreference for favourites
        sharedPref = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putStringSet(PREFS, new HashSet<String>()); //Initialize
        editor.commit();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_MOVIES, movieElements);
    }

    // Handle preferences changes
    private class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs,
                                              String key) {
            if(isNetworkAvailable()){
                GetMovieDataTask task = new GetMovieDataTask();
                task.execute();
            }
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Do Activity menu item stuff here
                return true;
            case R.id.action_share:
                // Not implemented here
                return false;
            default:
                break;
        }

        return false;
    }*/


    @Override
    public void onBackPressed() {
        Log.d("MAINACTIVITY", "onBackPressed");
        //supportInvalidateOptionsMenu();
        //startActivity(new Intent(this, MainActivity.class));
        //invalidateOptionsMenu();
        super.onBackPressed();

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

    //Check network connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isTwoPane(){
        if(isTwoPane) {
            return true;
        }else{
            return false;
        }
    }


    private class GetMovieDataTask extends AsyncTask<Void, Void, ArrayList<MovieElement>> {

        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        //Read Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String API_URL = getResources().getString(R.string.api_url);
        String API_IMAGE_URL = getResources().getString(R.string.api_image_url);
        String API_KEY = getResources().getString(R.string.api_key);
        String API_PARAM1 = getResources().getString(R.string.api_params1);
        String API_PARAM2 = getResources().getString(R.string.api_params2);
        String API_PARAM4 = getResources().getString(R.string.api_params4);
        String API_PARAM5 = getResources().getString(R.string.api_params5);


        String API_TAG = "?api_key=";

        //JSON node names
        private static final String TAG_RESULTS = "results";
        private static final String TAG_ID = "id";
        private static final String TAG_TITLE = "original_title";
        private static final String TAG_OVERVIEW = "overview";
        private static final String TAG_DATE = "release_date";
        private static final String TAG_RATING = "vote_average";
        private static final String TAG_VOTES = "vote_count";
        private static final String TAG_POSTER = "poster_path";
        private static final String TAG_BACKDROP = "backdrop_path";

        private int counter = 0;
        private JSONArray arr;
        private ArrayList<MovieElement> result = new ArrayList<MovieElement>();
        private MovieElement movie;
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

            String url;

            //Determine sorting method
            String sortingMethod = sharedPref.getString(PREFS_SORT, "1");

            switch (sortingMethod) {
                case "1":
                    url = API_URL + API_PARAM1 + API_KEY;
                    return createAPICallMultiple(url);
                case "2":
                    url = API_URL + API_PARAM2 + API_KEY;
                    return createAPICallMultiple(url);
                case "3":
                    Log.d("MainActivity: ", "You are in favourite mode");

                    sharedPref = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                    Set allMovies = sharedPref.getStringSet(PREFS, null);
                    ArrayList<MovieElement> favouriteMovies = new ArrayList<MovieElement>();
                    Log.d("MainActivity: ", "There are currently " + Integer.toString(allMovies.size()) + " favourites");

                    //If there are favourites
                    if (allMovies != null) {
                        //Iterate through favourite movies
                        Iterator<String> iterator = allMovies.iterator();
                        while (iterator.hasNext()) {
                            String setElement = iterator.next();
                            //Build url
                            url = API_URL + setElement + API_TAG + API_KEY;

                            MovieElement current = createAPICallSingle(url);
                            favouriteMovies.add(current);
                        }
                        return favouriteMovies;
                    }
                case "4":
                    url = API_URL + API_PARAM4 + API_KEY;
                    return createAPICallMultiple(url);
                case "5":
                    url = API_URL + API_PARAM5 + API_KEY;
                    return createAPICallMultiple(url);
                default:
                    url = API_URL + API_PARAM1 + API_KEY;
                    return createAPICallMultiple(url);
            }
        }

        private MovieElement createAPICallSingle(String url){

            JSONParser jParser = new JSONParser();

            try {
                //Get JSON from URL
                obj = jParser.getJSONFromUrl(url);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            movie = convertMovie(obj); //Overwrite private variable
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }finally{
                return movie;
            }
        }


        private ArrayList<MovieElement> createAPICallMultiple(String url){

            JSONParser jParser = new JSONParser();

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


        private MovieElement convertMovie(JSONObject obj) throws JSONException {

            String id = obj.getString(TAG_ID);
            String name = obj.getString(TAG_TITLE);
            String backdrop = obj.getString(TAG_BACKDROP);
            String poster = obj.getString(TAG_POSTER);
            String synopsis = obj.getString(TAG_OVERVIEW);
            String rating = obj.getString(TAG_RATING);
            String releaseDate = obj.getString(TAG_DATE);
            String votes = obj.getString(TAG_VOTES);

            return new MovieElement(id, name, API_IMAGE_URL + poster, API_IMAGE_URL + backdrop, synopsis, rating, votes, releaseDate);
        }

    }

}

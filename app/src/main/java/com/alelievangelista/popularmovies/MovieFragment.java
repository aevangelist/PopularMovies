package com.alelievangelista.popularmovies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MovieFragment extends Fragment {

    //Preferences
    public ImageAdapter adapter;
    private PreferenceChangeListener listener = null;
    private SharedPreferences settingsPref;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    //Tags
    public static final String PREFS_SORT = "PREF_SORT";
    public static final String BUNDLE_MOVIES = "BUNDLE_MOVIES";
    public static final String PREFS = "PREF_FAVOURITES";

    OnMovieSelectListener selectListener;

    private ArrayList<MovieElement> movieElements;
    private Toolbar mainToolbar;
    private GridView gridView;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainFrag", "Creating movie main FRAGMENT");
        setHasOptionsMenu(true);

        if(savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_MOVIES)) {
            movieElements = new ArrayList<MovieElement>();
            if(isNetworkAvailable()){
                Log.d("MovieFragment", "Network available - getting movies");
                GetMovieDataTask task = new GetMovieDataTask();
                task.execute();
            }

        }
        else {
            movieElements = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES);
        }

        settingsPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        listener = new PreferenceChangeListener();
        settingsPref.registerOnSharedPreferenceChangeListener(listener);

        preferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putStringSet(PREFS, new HashSet<String>()); //Initialize
        editor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.movie_fragment, container, false);

        mainToolbar = (Toolbar) rootView.findViewById(R.id.main_toolbar);

        gridView = (GridView) rootView.findViewById(R.id.gridview);

        adapter = new ImageAdapter(getActivity(), R.layout.movie_element, movieElements);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                ImageAdapter adapter = (ImageAdapter) gridView.getAdapter();

                movieElements = adapter.getItemList();
                MovieElement currMovie = movieElements.get(position);

                Toast.makeText(getActivity(), currMovie.getMovieName(),
                        Toast.LENGTH_SHORT).show();

                //Use interface to send out data and load new fragment
                selectListener.onSelect(currMovie);

            }
        });

        ((AppCompatActivity) getActivity()).setSupportActionBar(mainToolbar);
        mainToolbar.setLogo(R.drawable.logo);

        getCompat();
        return rootView;

    }

    /**
     * Set up overflow menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d("MovieFrag", "Creating menu for general movie frag");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), PrefActivity.class));
                return true;
            case R.id.action_about:
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("About");
                alertDialog.setMessage("Submission for Popular Movies 2" + "\n" + "Android Nanodegree" + "\n"
                        + "Credits: " + "Icon8, Udacity");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YAY",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Remove toolbar if using tablet
     */
    private void getCompat(){
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Log.d("SCREEN LAYOUT: ", "LARGE");
            mainToolbar.setVisibility(View.GONE);
        }if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Log.d("SCREEN LAYOUT: ", "XLARGE");
            mainToolbar.setVisibility(View.GONE);
        }
    }

    /**
     * Determine number of columns that should appear
     */
    private void refreshGridView() {

        int gridViewEntrySize = getResources().getDimensionPixelSize(R.dimen.grid_view_entry_size);
        int gridViewSpacing = getResources().getDimensionPixelSize(R.dimen.grid_view_spacing);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int numColumns = (width - gridViewSpacing) / (gridViewEntrySize + gridViewSpacing);

        gridView.setNumColumns(numColumns);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refreshGridView();
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

    //Check network connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class GetMovieDataTask extends AsyncTask<Void, Void, ArrayList<MovieElement>> {

        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        //Read Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

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

            String sortingMethod;
            String url;

            //Determine sorting method
            sortingMethod = sharedPref.getString(PREFS_SORT, "1");

            switch (sortingMethod) {
                case "1":
                    url = API_URL + API_PARAM1 + API_KEY;
                    return createAPICallMultiple(url);
                case "2":
                    url = API_URL + API_PARAM2 + API_KEY;
                    return createAPICallMultiple(url);
                case "3":
                    Log.d("MainActivity: ", "You are in favourite mode");

                    sharedPref = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
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

                getActivity().runOnUiThread(new Runnable() {
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

                getActivity().runOnUiThread(new Runnable() {
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

    @Override
    public void onResume() {
        super.onResume();
        refreshGridView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            selectListener = (OnMovieSelectListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSelect");
        }
    }

}

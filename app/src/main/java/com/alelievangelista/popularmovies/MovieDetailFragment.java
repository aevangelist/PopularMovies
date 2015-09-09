package com.alelievangelista.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class MovieDetailFragment extends Fragment {

    //SharedPreferences
    public static final String PREFS = "PREF_FAVOURITES";
    private Set allMovies;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private boolean isFavourite;

    //Share Intent
    private Toolbar toolbar;
    private ShareActionProvider shareActionProvider;
    private Intent shareIntent;

    //Page components
    private MovieElement movieElement;
    private ArrayList<ReviewElement> reviewElements;
    private ArrayList<TrailerElement> trailerElements;

    private LinearLayout reviewList;
    private LinearLayout trailerList;
    private HorizontalScrollView trailerView;

    //UI Elements
    public TextView movieName;
    public ImageView movieImage;
    public TextView movieDate;
    public TextView movieRating;
    public ImageView star1;
    public ImageView star2;
    public ImageView star3;
    public ImageView star4;
    public ImageView star5;
    public TextView movieRateCount;
    public TextView movieSynopsis;
    public ImageView favouriteIcon;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView toolbarBackground;

    private MenuItem shareItem;

    //Colours
    private int color = Color.parseColor("#C62828");

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DetailFrag", "Creating movie detail FRAGMENT");

        setHasOptionsMenu(true);

        if (savedInstanceState != null && savedInstanceState.getParcelable("BUNDLE_MOVIE") != null) {
            movieElement = savedInstanceState.getParcelable("BUNDLE_MOVIE");
            reviewElements = savedInstanceState.getParcelable("BUNDLE_REVIEWS");
            trailerElements= savedInstanceState.getParcelable("BUNDLE_TRAILERS");
        }else{
            reviewElements = new ArrayList<ReviewElement>();
            trailerElements = new ArrayList<TrailerElement>();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail_fragment, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        reviewList = (LinearLayout) rootView.findViewById(R.id.reviewList);

        trailerView = (HorizontalScrollView) rootView.findViewById(R.id.trailerView);
        trailerList = (LinearLayout) rootView.findViewById(R.id.trailerList);

        //Fragment components
        movieName = (TextView) rootView.findViewById(R.id.movieName);
        movieImage = (ImageView) rootView.findViewById(R.id.movieImage);

        movieDate = (TextView) rootView.findViewById(R.id.movieDate);

        movieRating = (TextView) rootView.findViewById(R.id.movieRating);
        movieRateCount = (TextView) rootView.findViewById(R.id.movieRateCount);
        //Stars
        star1 = (ImageView) rootView.findViewById(R.id.star1);
        star2 = (ImageView) rootView.findViewById(R.id.star2);
        star3 = (ImageView) rootView.findViewById(R.id.star3);
        star4 = (ImageView) rootView.findViewById(R.id.star4);
        star5 = (ImageView) rootView.findViewById(R.id.star5);


        movieSynopsis = (TextView) rootView.findViewById(R.id.movieSynopsis);

        favouriteIcon = (ImageView) rootView.findViewById(R.id.favouriteIcon);

        //Toolbar components
        collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapse_toolbar);
        toolbarBackground = (ImageView) rootView.findViewById(R.id.collapse_toolbar_bg);

        //Set up
        if (movieElement != null) {
            loadMovieDetails(movieElement);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Log.d("DetailFrag", "Creating menu for detail frag");
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.details_menu, menu);

        // Find the MenuItem that we know has the ShareActionProvider
        shareItem = menu.findItem(R.id.action_share);

        // Get its ShareActionProvider
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        // Connect the dots: give the ShareActionProvider its Share Intent
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(getShareIntent());
        }

    }

    /**
     * React to the user tapping the back/up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Intent getShareIntent(){
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (movieElement != null) {
            //Build the movie page element
            outState.putParcelable("BUNDLE_MOVIE", movieElement);
            outState.putParcelableArrayList("BUNDLE_REVIEWS", reviewElements);
            outState.putParcelableArrayList("BUNDLE_TRAILERS", trailerElements);
        }
    }

    /**
     * Only show back button if not using a tablet
     */
    private void getCompat(){
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Log.d("SCREEN LAYOUT: ", "LARGE");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }else if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Log.d("SCREEN LAYOUT: ", "XLARGE");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }else{
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void loadMovieDetails(MovieElement m) {

        movieElement = m;


        //Set up toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        getCompat();
        collapsingToolbar.setTitle(movieElement.getMovieName());
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedToolbar);

        //Determine movie name
        movieName.setText(getName(movieElement.getMovieName()));

        //Determine date components
        String[] splitDate = movieElement.getMovieDate().split("-");
        if (splitDate.length == 3) {
            movieDate.setText(getMonth(splitDate[1]) + " " + splitDate[2] + " " + splitDate[0]);
        } else {
            movieDate.setText("");
        }

        //Determine movie rating
        movieRating.setText(getRating(movieElement.getMovieRating()));
        movieRateCount.setText(movieElement.getMovieVotes());

        //Determine number of stars
        loadStars(movieElement.getMovieRating());

        //Determine synopsis
        movieSynopsis.setText(getSynopsis(movieElement.getMovieSynopsis()));

        //Load images
        Picasso.with(getActivity()).load(movieElement.getMovieBackdrop()).into(toolbarBackground);
        Picasso.with(getActivity()).load(movieElement.getMoviePoster()).into(movieImage);

        //Load reviews and trailers
        loadReviews();
        loadTrailers();

        //Set up favourites icon
        loadFavourites();

    }

    private void loadStars(String r){

        if(r != null){
            //Attempt to turn into integer
            float f = Float.valueOf(r.trim()).floatValue();
            float divided = (f/2);
            int stars = Math.round(divided); //Number of stars

            switch (stars) {
                case 1:
                    star1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    star4.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    star4.setVisibility(View.VISIBLE);
                    star5.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

        }else{
            return; //Do nothing
        }

    }

    private void loadReviews(){
        reviewElements = new ArrayList<ReviewElement>();
        if(isNetworkAvailable() && movieElement != null){
            GetReviewsTask task = new GetReviewsTask();
            task.execute();
        }


    }

    private void loadTrailers(){
        trailerElements = new ArrayList<TrailerElement>();
        if(isNetworkAvailable() && movieElement != null){
            GetTrailersTask task = new GetTrailersTask();
            task.execute();
        }

    }

    private void loadFavourites(){

        sharedPref = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        allMovies = sharedPref.getStringSet(PREFS, null);
        isFavourite = false;

        //If there are favourites
        if(allMovies != null){
            //Iterate through favourite movies
            Iterator<String> iterator = allMovies.iterator();
            String id = movieElement.getMovieId();
            while(iterator.hasNext() && !isFavourite) {
                String setElement = iterator.next();
                if(setElement.equals(id)) {
                    //The movie is a favourite
                    isFavourite = true;
                }
            }
        }

        if(isFavourite){
            favouriteIcon.setImageResource(R.drawable.heart_filled); //Set icon
            favouriteIcon.setColorFilter(color);
        }else if(!isFavourite){
            favouriteIcon.setImageResource(R.drawable.heart_unfilled);
            favouriteIcon.setColorFilter(color);//Set icon
        }

        favouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor = sharedPref.edit();
                String id = movieElement.getMovieId();

                if(isFavourite && allMovies != null){
                    //Remove from favourites
                    allMovies.remove(id);
                    editor.putStringSet(PREFS, allMovies);
                    Boolean flag = editor.commit();
                    if(flag) {
                        favouriteIcon.setImageResource(R.drawable.heart_unfilled); //Set icon
                        favouriteIcon.setColorFilter(color);
                        isFavourite = false;
                        Toast.makeText(v.getContext(),
                                "Removed From Favourites",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //Save to favourites
                    allMovies.add(id);
                    editor.putStringSet(PREFS, allMovies);
                    Boolean flag = editor.commit();

                    if(flag) {
                        favouriteIcon.setImageResource(R.drawable.heart_filled); //Set icon
                        favouriteIcon.setColorFilter(color);
                        isFavourite = true;
                        Toast.makeText(v.getContext(),
                                "Saved To Favourites",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });    }

    private String getName(String n) {
        if (n != null) {
            return n;
        } else {
            return "";
        }
    }

    private String getRating(String r) {
        if (r != null) {
            //Transform raw rating
            int i = Double.valueOf(r).intValue();
            String u = String.valueOf(i);

            return u;
        } else {
            return "";
        }
    }

    private String getSynopsis(String s) {
        if (s == null || s.equals("No overview found.") || s.equals("null")) {
            return "";
        } else {

            return s;
        }
    }

    private String getMonth(String m) {
        int intMonth = Integer.parseInt(m);
        String monthString = "";
        monthString = new DateFormatSymbols().getMonths()[intMonth - 1];

        return monthString;
    }

    //Check network connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * AsyncTask for pulling movie review data
     */
    private class GetReviewsTask extends AsyncTask<Void, Void, ArrayList<ReviewElement>> {

        String API_URL_HEAD = "https://api.themoviedb.org/3/movie/";
        String API_URL_TAIL = "/reviews?api_key=387e9b1680afd71a65f13ee0670dc6c6";

        //JSON node names
        private static final String TAG_RESULTS = "results";
        private static final String TAG_AUTHOR = "author";
        private static final String TAG_CONTENT = "content";

        private int counter = 0;
        private JSONArray arr;
        private ArrayList<ReviewElement> result = new ArrayList<ReviewElement>();
        private JSONObject obj;

        @Override
        protected void onPostExecute(ArrayList<ReviewElement> result) {
            super.onPostExecute(result);
            reviewElements = result;

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            LinearLayout container = (LinearLayout) getActivity().findViewById(R.id.fragLayout);

            //List view set up
            for (int i = 0; i < reviewElements.size(); i++){
                View r = inflater.inflate(R.layout.review_element, container, false);

                TextView reviewer = (TextView) r
                        .findViewById(R.id.reviewerName);
                TextView review = (TextView) r
                        .findViewById(R.id.reviewContent);

                final String reviewAuthor = reviewElements.get(i).getReviewAuthor();
                final String reviewContent = reviewElements.get(i).getReviewContent();
                reviewer.setText(reviewAuthor);
                review.setText(reviewContent);

                reviewList.addView(r);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ReviewElement> doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String url = API_URL_HEAD + movieElement.getMovieId() + API_URL_TAIL;

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
                                ReviewElement r = convertReview(o);
                                result.add(r);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }finally{
                return result;
            }        }

        private ReviewElement convertReview(JSONObject obj) throws JSONException {

            String author = obj.getString(TAG_AUTHOR);
            String content = obj.getString(TAG_CONTENT);

            return new ReviewElement(author, content);
        }
    }

    /**
     * AsyncTask for pulling trailer data
     */
    private class GetTrailersTask extends AsyncTask<Void, Void, ArrayList<TrailerElement>> {

        String API_URL_HEAD = "https://api.themoviedb.org/3/movie/";
        String API_URL_TAIL = "/videos?api_key=387e9b1680afd71a65f13ee0670dc6c6";

        //JSON node names
        private static final String TAG_RESULTS = "results";
        private static final String TAG_NAME = "name";
        private static final String TAG_KEY = "key";
        private static final String TAG_SITE = "site";
        private static final String TAG_SIZE = "size";

        private JSONArray arr;
        private ArrayList<TrailerElement> result = new ArrayList<TrailerElement>();
        private JSONObject obj;

        @Override
        protected void onPostExecute(ArrayList<TrailerElement> result) {
            super.onPostExecute(result);
            trailerElements = result;

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            LinearLayout container = (LinearLayout) getActivity().findViewById(R.id.fragLayout);

            //List view set up
            for (int i = 0; i < trailerElements.size(); i++){

                View t = inflater.inflate(R.layout.trailer_element, container, false);

                TextView title = (TextView) t
                        .findViewById(R.id.title);
                ImageView thumbnail = (ImageView) t
                        .findViewById(R.id.thumbnail);

                final String titleName = trailerElements.get(i).getTrailerName();
                final String thumbnailUrl = trailerElements.get(i).getTrailerImageUrl();
                final String url = trailerElements.get(i).getTrailerUrl();

                title.setText(titleName);
                Picasso.with(getActivity()).load(thumbnailUrl).into(thumbnail);

                //Set up onclick listener
                thumbnail.setOnClickListener(new View.OnClickListener() {
                                                 public void onClick(View v) {
                                                     Intent intent = new Intent();
                                                     intent.setAction(Intent.ACTION_VIEW);
                                                     intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                                     intent.setData(Uri.parse(url));
                                                     startActivity(intent);
                                                 }
                                             });
                trailerList.addView(t);

            }

            trailerView.removeAllViews(); //Clear
            trailerView.addView(trailerList);

            //Set up share link
            if(trailerElements.size() > 0){
                String url = "";
                TrailerElement t = trailerElements.get(0);
                url = t.getTrailerUrl();

                if(shareIntent != null){
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this out: " + url);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<TrailerElement> doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String url = API_URL_HEAD + movieElement.getMovieId() + API_URL_TAIL;

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

                                //YouTube only
                                if(o.getString(TAG_SITE).equals("YouTube")){
                                    TrailerElement t = convertTrailer(o);
                                    result.add(t);
                                }
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

        private TrailerElement convertTrailer(JSONObject obj) throws JSONException {

            String name = obj.getString(TAG_NAME);
            String key = obj.getString(TAG_KEY);
            String site = obj.getString(TAG_SITE);
            String size = obj.getString(TAG_SIZE);

            //Build thumbnail URL
            String imageUrl = getResources().getString(R.string.trailer_img_url_head) + key +
                    getResources().getString(R.string.trailer_img_url_tail);

            String url = getResources().getString(R.string.trailer_url) + key;

            return new TrailerElement(name, key, site, size, imageUrl, url);
        }
    }


}

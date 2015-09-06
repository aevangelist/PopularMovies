package com.alelievangelista.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
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

import java.util.ArrayList;


public class MovieFragment extends Fragment {

    OnMovieSelectListener selectListener;

    private ArrayList<MovieElement> movieElements;
    private Toolbar mainToolbar;
    private GridView gridView;

    public MovieFragment() {
        // Required empty public constructor
    }

    /*
    Make sure activity has interface implemented within it
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            selectListener = (OnMovieSelectListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSelect");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainFrag", "Creating movie main FRAGMENT");
        setHasOptionsMenu(true);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.movie_fragment, container, false);

        mainToolbar = (Toolbar) rootView.findViewById(R.id.main_toolbar);

        gridView = (GridView) rootView.findViewById(R.id.gridview);

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
    public void onResume() {
        super.onResume();
        refreshGridView();
    }

}

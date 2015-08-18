package com.alelievangelista.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;


public class MovieFragment extends Fragment {

    OnMovieSelectListener selectListener;

    private ArrayList<MovieElement> movieElements;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.movie_fragment, container, false);

        final GridView gridview = (GridView) rootView.findViewById(R.id.gridview);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                ImageAdapter adapter = (ImageAdapter) gridview.getAdapter();

                movieElements = adapter.getItemList();
                MovieElement currMovie = movieElements.get(position);

                Toast.makeText(getActivity(), currMovie.getMovieName(),
                        Toast.LENGTH_SHORT).show();

                //Use interface to send out data and load new fragment
                selectListener.onSelect(currMovie);

            }
        });

        return rootView;

    }

}

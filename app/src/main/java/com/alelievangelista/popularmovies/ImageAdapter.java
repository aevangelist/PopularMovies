package com.alelievangelista.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by aevangelista on 15-07-14.
 */
public class ImageAdapter extends ArrayAdapter<MovieElement> {

    private Context context;
    private ArrayList<MovieElement> itemList = new ArrayList<MovieElement>();
    private int layoutResource;

    public ImageAdapter(Context c, int layoutResource, ArrayList<MovieElement> itemList) {
        super(c, layoutResource, itemList);
        this.context = c;
        this.itemList = itemList;
        this.layoutResource = layoutResource;
    }

    public ArrayList<MovieElement> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<MovieElement> result) {
        this.itemList = result;
    }

    /*private view holder class*/
    private class ViewHolder {
        CustomImageView imageView;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder.imageView = (CustomImageView) convertView.findViewById(R.id.movie_image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        MovieElement e = itemList.get(position);
        String moviePoster = e.getMoviePoster();

        //Validate the URL
        if(moviePoster != null){
            Picasso.with(context).load(e.getMoviePoster()).into(holder.imageView);
        }

        return convertView;
    }

}

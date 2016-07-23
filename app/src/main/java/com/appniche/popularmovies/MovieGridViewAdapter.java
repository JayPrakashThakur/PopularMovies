package com.appniche.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by JayPrakash on 14-01-2016.
 */
public class MovieGridViewAdapter extends BaseAdapter {

    Context context;
    ArrayList<Movie> movies = new ArrayList<>();

    public MovieGridViewAdapter(Context context) {
        this.context = context;
    }

    /*public MovieGridViewAdapter(Context context, ArrayList<Movie> movies) {
        super();
        this.context = context;
        this.movies = movies;
    }*/

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            //imageView.setLayoutParams(new GridView.LayoutParams(545, 755));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(context)
                .load(movies.get(position).getmPosterUrl())
                .into(imageView);
        imageView.setAdjustViewBounds(true);

        return imageView;
    }

    public void add(Movie movie){
        movies.add(movie);
    }

    public void clear(){
        movies.clear();
    }

}

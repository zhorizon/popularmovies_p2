package com.lazycat.android.popularmovies.app.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.lazycat.android.popularmovies.app.FlavorMovie;
import com.lazycat.android.popularmovies.app.R;
import com.lazycat.android.popularmovies.app.utils.DownloadUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Cencil on 8/9/2015.
 */
public class FlavorMovieAdapter extends ArrayAdapter<FlavorMovie> {
    private static final String LOG_TAG = FlavorMovieAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

    public FlavorMovieAdapter(Context context, int resource, List<FlavorMovie> objects) {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ImageView imageView;

        if (convertView == null) {
            // it is no recyling view, create a new one
            view = mInflater.inflate(R.layout.list_item_poster, parent, false);
        } else {
            view = convertView;
        }

        // get the image view
        imageView = (ImageView) view.findViewById(R.id.list_item_poster_img);

        FlavorMovie flavorMovie = getItem(position);

        if (flavorMovie != null) {
            String urlStr = DownloadUtils.buildPosterImageUrl(flavorMovie.getPosterPath());

            if (urlStr != null) {
                Log.v(LOG_TAG, "Poster Path: " + flavorMovie.getPosterPath() + " Poster image url: " + urlStr);

                // Using Picasso to fetch images and load them into view
                Picasso.with(getContext()).load(urlStr).into(imageView);
            } else {
                Log.d(LOG_TAG, "urlStr is null!");
            }
        } else {
            Log.d(LOG_TAG, "flavorMovie is null!?");
        }

        return imageView;
    }
}

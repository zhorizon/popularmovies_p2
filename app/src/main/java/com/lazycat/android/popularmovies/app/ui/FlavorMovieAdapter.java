package com.lazycat.android.popularmovies.app.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lazycat.android.popularmovies.app.R;
import com.lazycat.android.popularmovies.app.utils.DownloadUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by Cencil on 8/9/2015.
 */
public class FlavorMovieAdapter extends CursorAdapter {
    private static final String LOG_TAG = FlavorMovieAdapter.class.getSimpleName();

    /**
     * Cache of the children views for image list item.
     */
    public static class ViewHolder {
        public final ImageView posterView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.list_item_poster_img);
        }
    }

    public FlavorMovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_poster, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read poster path from cursor
        String posterPath = cursor.getString(MainFragment.COL_POSTER_PATH);

        String posterUrlStr = DownloadUtils.buildPosterImageUrl(posterPath);

        // Using Picasso to fetch images and load them into view
        Picasso.with(context).load(posterUrlStr).into(viewHolder.posterView);
    }
}

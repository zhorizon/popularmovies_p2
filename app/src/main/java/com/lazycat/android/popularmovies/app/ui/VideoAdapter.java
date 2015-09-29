package com.lazycat.android.popularmovies.app.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lazycat.android.popularmovies.app.R;


/**
 * Created by Cencil on 9/28/2015.
 */
public class VideoAdapter extends CursorAdapter {
    private static final String LOG_TAG = VideoAdapter.class.getSimpleName();

    public VideoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_video, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read video information from cursor
        viewHolder.textName.setText(cursor.getString(DetailFragment.COL_VIDEO_NAME));
    }

    // Cache of the children view
    static class ViewHolder {
        TextView textName;

        public ViewHolder(View view) {
            textName = (TextView) view.findViewById(R.id.textView_name);
        }
    }
}

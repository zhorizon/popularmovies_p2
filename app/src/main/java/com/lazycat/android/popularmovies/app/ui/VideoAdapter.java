package com.lazycat.android.popularmovies.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lazycat.android.popularmovies.app.MovieVideo;
import com.lazycat.android.popularmovies.app.R;

import java.util.List;

/**
 * Created by Cencil on 9/28/2015.
 */
public class VideoAdapter extends ArrayAdapter<MovieVideo> {
    private static final String LOG_TAG = VideoAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

    public VideoAdapter(Context context, int resource, List<MovieVideo> objects) {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        VideoHolder holder = null;

        if (convertView == null) {
            // it is no recyling view, create a new one
            view = mInflater.inflate(R.layout.list_item_video, parent, false);

            holder = new VideoHolder();
            holder.imageIcon = (ImageView) view.findViewById(R.id.imageView_icon);
            holder.textName = (TextView) view.findViewById(R.id.textView_name);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (VideoHolder) view.getTag();
        }

        MovieVideo video = getItem(position);

        if (video != null) {
            holder.textName.setText(video.getName());
        }

        return view;
    }

    static class VideoHolder {
        ImageView imageIcon;
        TextView textName;
    }
}

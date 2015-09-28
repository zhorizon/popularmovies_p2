package com.lazycat.android.popularmovies.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lazycat.android.popularmovies.app.MovieReview;
import com.lazycat.android.popularmovies.app.R;

import java.util.List;

/**
 * Created by Cencil on 9/28/2015.
 */
public class ReviewAdapter extends ArrayAdapter<MovieReview> {
    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

    public ReviewAdapter(Context context, int resource, List<MovieReview> objects) {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        VideoHolder holder = null;

        if (convertView == null) {
            // it is no recyling view, create a new one
            view = mInflater.inflate(R.layout.list_item_review, parent, false);

            holder = new VideoHolder();
            holder.textAuthor = (TextView) view.findViewById(R.id.textView_author);
            holder.textContent = (TextView) view.findViewById(R.id.textView_content);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (VideoHolder) view.getTag();
        }

        MovieReview review = getItem(position);

        if (review != null) {
            holder.textAuthor.setText(review.getAuthor());
            holder.textContent.setText(review.getContent());
        }

        return view;
    }

    static class VideoHolder {
        TextView textAuthor;
        TextView textContent;
    }
}

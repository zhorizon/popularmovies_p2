package com.lazycat.android.popularmovies.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.lazycat.android.popularmovies.app.R;
import com.lazycat.android.popularmovies.app.data.MovieContract;

import java.text.SimpleDateFormat;

/**
 * Created by Cencil on 9/27/2015.
 */
public class Utility {
    public static String getPreferenceSortOrder(Context context) {
        // get sort by from share preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(
                context.getString(R.string.pref_order_by_key),
                context.getString(R.string.pref_order_by_default));
    }

    public static String getContentProviderSortOrder(Context context, String prefSortOrder) {
        if (prefSortOrder.equals(context.getString(R.string.pref_order_by_popularity))) {
            return MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else if (prefSortOrder.equals(context.getString(R.string.pref_order_by_vote_average))) {
            return MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }

        return null;
    }

    public static String getPreferenceSortOrder(Context context, String actionSortOrder) {
        // resolve selected action to preference sort order key value
        if (actionSortOrder.equals(context.getString(R.string.action_order_by_popularity))) {
            return context.getString(R.string.pref_order_by_popularity);
        } else if (actionSortOrder.equals(context.getString(R.string.action_order_by_vote_average))) {
            return context.getString(R.string.pref_order_by_vote_average);
        }

        return null;
    }

    public static String getFormattedRating(Context context, float voteAverage, int voteCount) {
        return context.getString(R.string.format_rating, voteAverage, voteCount);
    }

    public static String getFormattedYear(Context context, long dateInMillis ) {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        return yearFormat.format(dateInMillis);
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}

package com.lazycat.android.popularmovies.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

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
}

package com.lazycat.android.popularmovies.app.ui;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.lazycat.android.popularmovies.app.MovieReview;
import com.lazycat.android.popularmovies.app.data.MovieContract;
import com.lazycat.android.popularmovies.app.utils.DownloadUtils;


/**
 * Created by Cencil on 9/28/2015.
 */
public class FetchReviewTask extends AsyncTask<String, Void, MovieReview[]> {
    private static final String LOG_TAG = FetchReviewTask.class.getSimpleName();

    private Context mContext;
    private ReviewAdapter mReviewAdapter;
    private long mMovieId;

    public FetchReviewTask(Context context, ReviewAdapter adapter) {
        this.mContext = context;
        this.mReviewAdapter = adapter;
    }

    @Override
    protected MovieReview[] doInBackground(String... params) {
        if (params.length == 0)
            return null;

        mMovieId = Long.parseLong(params[0]);
        String apiKey = params[1];

        if (apiKey == null) {
            Log.d(LOG_TAG, "apiKey is null");
            return null;
        }

        // download movies data from themoviedb
        String reviewJsonStr = DownloadUtils.downloadMovieReviews(apiKey, mMovieId);

        if (reviewJsonStr == null) {
            Log.d(LOG_TAG, "reviewJsonStr is null");
            return null;
        }

        // parse the return JSON string to flavor movie object array
        return DownloadUtils.getReviewDataFromJson(reviewJsonStr);
    }

    @Override
    protected void onPostExecute(MovieReview[] movieReviews) {
        super.onPostExecute(movieReviews);

            if (movieReviews != null) {
                // add or update to database
                for (MovieReview review : movieReviews) {
                    ContentValues reviewValues = new ContentValues();

                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, mMovieId);
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_ID, review.getId());
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, review.getUrl());

                    int updated = mContext.getContentResolver().update(
                            MovieContract.ReviewEntry.CONTENT_URI,
                            reviewValues,
                            MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + "=? AND " +
                                    MovieContract.ReviewEntry.COLUMN_ID + "=?",
                            new String[] {Long.toString(mMovieId), review.getId()});

                    if (updated == 0) {
                        mContext.getContentResolver().insert(
                                MovieContract.ReviewEntry.CONTENT_URI,
                                reviewValues);
                    }
                }
            } else {
                Log.d(LOG_TAG, "movieVideo is null!");
            }

            // must notify the adapter data changed!!
            mReviewAdapter.notifyDataSetChanged();
    }
}

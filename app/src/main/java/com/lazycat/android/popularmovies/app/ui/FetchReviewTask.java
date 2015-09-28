package com.lazycat.android.popularmovies.app.ui;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.lazycat.android.popularmovies.app.MovieReview;
import com.lazycat.android.popularmovies.app.utils.DownloadUtils;
import com.lazycat.android.popularmovies.app.utils.Utility;

import java.util.ArrayList;

/**
 * Created by Cencil on 9/28/2015.
 */
public class FetchReviewTask extends AsyncTask<String, Void, MovieReview[]> {
    private static final String LOG_TAG = FetchReviewTask.class.getSimpleName();
    private ReviewAdapter adapter;
    private ArrayList<MovieReview> mReviewList;
    private ListView mReviewView;

    public FetchReviewTask(ListView view, ReviewAdapter adapter, ArrayList<MovieReview> data) {
        this.adapter = adapter;
        this.mReviewList = data;
        mReviewView = view;
    }

    @Override
    protected MovieReview[] doInBackground(String... params) {
        if (params.length == 0)
            return null;

        long movieId = Long.parseLong(params[0]);
        String apiKey = params[1];

        if (apiKey == null) {
            Log.d(LOG_TAG, "apiKey is null");
            return null;
        }

        // download movies data from themoviedb
        String reviewJsonStr = DownloadUtils.downloadMovieReviews(apiKey, movieId);

        if (reviewJsonStr == null) {
            Log.d(LOG_TAG, "videoJsonStr is null");
            return null;
        }

        // parse the return JSON string to flavor movie object array
        return DownloadUtils.getReviewDataFromJson(reviewJsonStr);
    }

    @Override
    protected void onPostExecute(MovieReview[] movieReviews) {
        super.onPostExecute(movieReviews);

        mReviewList.clear();

        if (movieReviews != null) {
            for (MovieReview review : movieReviews) {
                mReviewList.add(review);
            }
        } else {
            Log.d(LOG_TAG, "movieVideos is null");
        }
        adapter.notifyDataSetChanged();

//        Utility.setListViewHeightBasedOnChildren(mReviewView);

//            if (movieVideo != null) {
//                // add or update to database
//                for (FlavorMovie flavorMovie : flavorMovies) {
//                    ContentValues movieValues = new ContentValues();
//
//                    movieValues.put(MovieContract.MovieEntry._ID, flavorMovie.getId());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, flavorMovie.getTitle());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, flavorMovie.getOriginalTitle());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, flavorMovie.getOverview());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, flavorMovie.getPosterPath());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, flavorMovie.getBackdropPath());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, flavorMovie.getReleaseDate().getTime());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, flavorMovie.getPopularity());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, flavorMovie.getVoteAverage());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, flavorMovie.getVoteCount());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_ADULT, flavorMovie.isAdult());
//                    movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, flavorMovie.isVideo());
//
//                    int updated = getActivity().getContentResolver().update(
//                            MovieContract.MovieEntry.CONTENT_URI,
//                            movieValues,
//                            MovieContract.MovieEntry._ID + "=?",
//                            new String[] {Long.toString(flavorMovie.getId())});
//
//                    if (updated == 0) {
//                        getActivity().getContentResolver().insert(
//                                MovieContract.MovieEntry.CONTENT_URI,
//                                movieValues);
//                    }
//                }
//            } else {
//                Log.d(LOG_TAG, "movieVideo is null!");
//            }
//
//            // must notify the adapter data changed!!
//            mFlavorMovieAdapter.notifyDataSetChanged();
    }
}

package com.lazycat.android.popularmovies.app.ui;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.lazycat.android.popularmovies.app.MovieVideo;
import com.lazycat.android.popularmovies.app.utils.DownloadUtils;
import com.lazycat.android.popularmovies.app.utils.Utility;

import java.util.ArrayList;

/**
 * Created by Cencil on 9/28/2015.
 */
public class FetchVideoTask extends AsyncTask<String, Void, MovieVideo[]> {
    private static final String LOG_TAG = FetchVideoTask.class.getSimpleName();
    private VideoAdapter adapter;
    private ArrayList<MovieVideo> mVideoList;
    private ListView mVideoView;

    public FetchVideoTask(ListView view, VideoAdapter adapter, ArrayList<MovieVideo> data) {
        this.adapter = adapter;
        this.mVideoList = data;
        mVideoView = view;
    }

    @Override
    protected MovieVideo[] doInBackground(String... params) {
        if (params.length == 0)
            return null;

        long movieId = Long.parseLong(params[0]);
        String apiKey = params[1];

        if (apiKey == null) {
            Log.d(LOG_TAG, "apiKey is null");
            return null;
        }

        // download movies data from themoviedb
        String videoJsonStr = DownloadUtils.downloadMovieVideos(apiKey, movieId);

        if (videoJsonStr == null) {
            Log.d(LOG_TAG, "videoJsonStr is null");
            return null;
        }

        // parse the return JSON string to flavor movie object array
        return DownloadUtils.getVideoDataFromJson(videoJsonStr);
    }

    @Override
    protected void onPostExecute(MovieVideo[] movieVideos) {
        super.onPostExecute(movieVideos);

        mVideoList.clear();

        if (movieVideos != null) {
            for (MovieVideo video : movieVideos) {
                mVideoList.add(video);
            }
        } else {
            Log.d(LOG_TAG, "movieVideos is null");
        }
        adapter.notifyDataSetChanged();

//        Utility.setListViewHeightBasedOnChildren(mVideoView);

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

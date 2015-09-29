package com.lazycat.android.popularmovies.app.ui;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.lazycat.android.popularmovies.app.MovieVideo;
import com.lazycat.android.popularmovies.app.data.MovieContract;
import com.lazycat.android.popularmovies.app.utils.DownloadUtils;


/**
 * Created by Cencil on 9/28/2015.
 */
public class FetchVideoTask extends AsyncTask<String, Void, MovieVideo[]> {
    private static final String LOG_TAG = FetchVideoTask.class.getSimpleName();

    private Context mContext;
    private VideoAdapter mVideoAdapter;
    private long mMovieId;

    public FetchVideoTask(Context context, VideoAdapter adapter) {
        this.mContext = context;
        this.mVideoAdapter = adapter;
    }

    @Override
    protected MovieVideo[] doInBackground(String... params) {
        if (params.length == 0)
            return null;

        mMovieId = Long.parseLong(params[0]);
        String apiKey = params[1];

        if (apiKey == null) {
            Log.d(LOG_TAG, "apiKey is null");
            return null;
        }

        // download movies data from themoviedb
        String videoJsonStr = DownloadUtils.downloadMovieVideos(apiKey, mMovieId);

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

        if (movieVideos != null) {
            // add or update to database
            for (MovieVideo video : movieVideos) {
                ContentValues videoValues = new ContentValues();

                videoValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_KEY, mMovieId);
                videoValues.put(MovieContract.VideoEntry.COLUMN_ID, video.getId());
                videoValues.put(MovieContract.VideoEntry.COLUMN_KEY, video.getKey());
                videoValues.put(MovieContract.VideoEntry.COLUMN_NAME, video.getName());
                videoValues.put(MovieContract.VideoEntry.COLUMN_SITE, video.getSite());
                videoValues.put(MovieContract.VideoEntry.COLUMN_SIZE, video.getSize());
                videoValues.put(MovieContract.VideoEntry.COLUMN_TYPE, video.getType());

                int updated = mContext.getContentResolver().update(
                        MovieContract.VideoEntry.CONTENT_URI,
                        videoValues,
                        MovieContract.VideoEntry.COLUMN_MOVIE_KEY + "=? AND " +
                        MovieContract.VideoEntry.COLUMN_ID + "=?",
                        new String[] {Long.toString(mMovieId), video.getId()});

                if (updated == 0) {
                    mContext.getContentResolver().insert(
                            MovieContract.VideoEntry.CONTENT_URI,
                            videoValues);
                }
            }
        } else {
            Log.d(LOG_TAG, "movieVideo is null!");
        }

        // must notify the adapter data changed!!
        mVideoAdapter.notifyDataSetChanged();
    }
}

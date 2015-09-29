package com.lazycat.android.popularmovies.app.ui;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lazycat.android.popularmovies.app.R;
import com.lazycat.android.popularmovies.app.data.MovieContract;
import com.lazycat.android.popularmovies.app.utils.DownloadUtils;
import com.lazycat.android.popularmovies.app.utils.NetworkUtils;
import com.lazycat.android.popularmovies.app.utils.Utility;
import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String DETAIL_URI = "URI";
    private Uri mUri;

    private TextView mTitleView;
    private TextView mOverviewView;
    private TextView mReleaseDateView;
    private TextView mRatingView;
    private ImageView mPosterView;
    private ImageView mBackdropView;
    private ListView mVideoView;
    private ListView mReviewView;

    private VideoAdapter mVideoAdatper;
    private ReviewAdapter mReviewAdapter;

    private static final int DETAIL_LOADER = 0;
    private static final int VIDEO_LOADER = 1;
    private static final int REVIEW_LOADER = 2;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH
    };

    // these constants correspond to the projection defined above, and must change if the
    // project changes
    private static final int COL_ID = 0;
    private static final int COL_TITLE = 1;
    private static final int COL_OVERVIEW = 2;
    private static final int COL_RELEASE_DATE = 3;
    private static final int COL_VOTE_AVERAGE = 4;
    private static final int COL_VOTE_COUNT = 5;
    private static final int COL_POSTER_PATH = 6;
    private static final int COL_BACKDROP_PATH = 7;

    private static final String[] VIDEO_COLUMNS = {
            MovieContract.VideoEntry._ID,
            MovieContract.VideoEntry.COLUMN_NAME,
            MovieContract.VideoEntry.COLUMN_KEY
    };

    // These indices are tied to VIDEO_COLUMNS. If VIDEO_COLUMNS changes, these must change.
    static final int COL_VIDEO_ID = 0;
    static final int COL_VIDEO_NAME = 1;
    static final int COL_VIDEO_KEY = 2;

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };

    // These indices are tied to REVIEW_COLUMNS. If REVIEW_COLUMNS changes, these must change.
    static final int COL_REVIEW_ID = 0;
    static final int COL_REVIEW_AUTHOR = 1;
    static final int COL_REVIEW_CONTENT = 2;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTitleView = (TextView) rootView.findViewById(R.id.textView_title);
        mOverviewView = (TextView) rootView.findViewById(R.id.textView_overview);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.textView_release_date);
        mRatingView = (TextView) rootView.findViewById(R.id.textView_rating);
        mPosterView = (ImageView) rootView.findViewById(R.id.imageView_poster);
        mBackdropView = (ImageView) rootView.findViewById(R.id.imageView_backdrop);
        mVideoView = (ListView) rootView.findViewById(R.id.listView_video);
        mReviewView = (ListView) rootView.findViewById(R.id.listView_review);

        // video adapter
        mVideoAdatper = new VideoAdapter(getActivity(), null, 0);
        mVideoAdatper.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                onVideoDataSetChanged();
            }
        });
        mVideoView.setAdapter(mVideoAdatper);

        // review adapter
        mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);
        mReviewAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                onReviewDataSetChanged();
            }
        });
        mReviewView.setAdapter(mReviewAdapter);

        mVideoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null) {
                    String key = cursor.getString(COL_VIDEO_KEY);

                    if (key != null) {
                        // open video in youtube
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                        intent.putExtra("VIDEO_ID", key);
                        startActivity(intent);
                    } else {
                        Log.d(LOG_TAG, "video key is null");
                    }
                } else {
                    Log.d(LOG_TAG, "cursor is null?");
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // loader movie detail from content provider
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        // loader movie video from content provider
        getLoaderManager().initLoader(VIDEO_LOADER, null, this);

        // loader movie review from content provider
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);

        if (null != mUri) {
            // fetch movie video data from internet
            new FetchVideoTask(getActivity(), mVideoAdatper).execute(
                    new String[]{
                            Long.toString(ContentUris.parseId(mUri)),
                            getActivity().getString(R.string.themoviedb_api_key)}
            );

            // fetch review video data from internet
            new FetchReviewTask(getActivity(), mReviewAdapter).execute(
                    new String[]{
                            Long.toString(ContentUris.parseId(mUri)),
                            getActivity().getString(R.string.themoviedb_api_key)}
            );
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        switch (id) {
            case DETAIL_LOADER:
                if (null != mUri) {
                    return new CursorLoader(
                            getActivity(),
                            MovieContract.MovieEntry.CONTENT_URI,
                            DETAIL_COLUMNS,
                            MovieContract.MovieEntry._ID + "=?",
                            new String[] {Long.toString(ContentUris.parseId(mUri))},
                            null);
                }
                break;
            case VIDEO_LOADER:
                if (null != mUri) {
                    return new CursorLoader(
                            getActivity(),
                            MovieContract.VideoEntry.buildMovieVideoUri(ContentUris.parseId(mUri)),
                            VIDEO_COLUMNS,
                            null,
                            null,
                            null);
                }
            case REVIEW_LOADER:
                if (null != mUri) {
                    return new CursorLoader(
                            getActivity(),
                            MovieContract.ReviewEntry.buildMovieReviewUri(ContentUris.parseId(mUri)),
                            REVIEW_COLUMNS,
                            null,
                            null,
                            null);
                }
            default:
                Log.d(LOG_TAG, "Unknown loader id");
                break;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");

        // return if no data
        if (!data.moveToFirst()) {
            return;
        }

        switch (loader.getId()) {
            case DETAIL_LOADER:
                // Fill others movie data
                mTitleView.setText(data.getString(COL_TITLE));
                mOverviewView.setText(data.getString(COL_OVERVIEW));
                mReleaseDateView.setText(Utility.getFormattedYear(getActivity(), data.getLong(COL_RELEASE_DATE)));
                mRatingView.setText(Utility.getFormattedRating(getActivity(), data.getFloat(COL_VOTE_AVERAGE), data.getInt(COL_VOTE_COUNT)));

                // check the network status here before access internet
                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                    // Build poster path URL string
                    String urlStr = DownloadUtils.buildPosterImageUrl(data.getString(COL_POSTER_PATH));

                    if (urlStr != null) {
                        // Using Picasso to fetch poster image and load them into view
                        Picasso.with(getActivity()).load(urlStr).into(mPosterView);
                    } else {
                        Log.d(LOG_TAG, "urlStr is null!");
                    }

                    // Build backdrop path URL
                    urlStr = DownloadUtils.buildBackdropImageUrl(data.getString(COL_BACKDROP_PATH));

                    if (urlStr != null) {
                        // Using Picasso to fetch poster image and load them into view
                        Picasso.with(getActivity()).load(urlStr).into(mBackdropView);
                    } else {
                        Log.d(LOG_TAG, "urlStr is null!");
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.msg_network_not_available), Toast.LENGTH_SHORT).show();
                }
                break;
            case VIDEO_LOADER:
                mVideoAdatper.swapCursor(data);
                break;
            case REVIEW_LOADER:
                mReviewAdapter.swapCursor(data);
                break;
            default:
                Log.d(LOG_TAG, "Unknown loader id");
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        switch (loader.getId()) {
            case VIDEO_LOADER:
                mVideoAdatper.swapCursor(null);
                break;
            case REVIEW_LOADER:
                mReviewAdapter.swapCursor(null);
                break;
            default:
                Log.d(LOG_TAG, "Unknown loader id");
                break;
        }
    }

    private void onVideoDataSetChanged() {
        getLoaderManager().restartLoader(VIDEO_LOADER, null, this);
    }

    private void onReviewDataSetChanged() {
        getLoaderManager().restartLoader(REVIEW_LOADER, null, this);
    }
}

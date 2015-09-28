package com.lazycat.android.popularmovies.app.ui;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
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

import com.lazycat.android.popularmovies.app.MovieReview;
import com.lazycat.android.popularmovies.app.MovieVideo;
import com.lazycat.android.popularmovies.app.R;
import com.lazycat.android.popularmovies.app.data.MovieContract;
import com.lazycat.android.popularmovies.app.utils.DownloadUtils;
import com.lazycat.android.popularmovies.app.utils.NetworkUtils;
import com.lazycat.android.popularmovies.app.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


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

    private ArrayList<MovieVideo> mVideoList;
    private ArrayList<MovieReview> mReviewList;

    private static final int DETAIL_LOADER = 0;

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

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVideoList = new ArrayList<MovieVideo>();
        mReviewList = new ArrayList<MovieReview>();
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

        mVideoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieVideo video = mVideoAdatper.getItem(position);

                if (video != null) {
                    // open video in youtube
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getKey()));
                    intent.putExtra("VIDEO_ID", video.getKey());
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "video is null?");
                }
            }
        });

        mVideoAdatper = new VideoAdapter(
                getActivity(),
                R.layout.list_item_video,
                mVideoList);
        mVideoView.setAdapter(mVideoAdatper);

        mReviewAdapter = new ReviewAdapter(
                getActivity(),
                R.layout.list_item_review,
                mReviewList);
        mReviewView.setAdapter(mReviewAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        if (null != mUri) {
            // fetch movie video data from internet
            new FetchVideoTask(mVideoView, mVideoAdatper, mVideoList).execute(
                    new String[]{
                            Long.toString(ContentUris.parseId(mUri)),
                            getActivity().getString(R.string.themoviedb_api_key)}
            );

            // fetch review video data from internet
            new FetchReviewTask(mReviewView, mReviewAdapter, mReviewList).execute(
                    new String[]{
                            Long.toString(ContentUris.parseId(mUri)),
                            getActivity().getString(R.string.themoviedb_api_key)}
            );
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI,
                    DETAIL_COLUMNS,
                    MovieContract.MovieEntry._ID + "=?",
                    new String[] {Long.toString(ContentUris.parseId(mUri))},
                    null
            );
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // do nothing
    }
}

package com.lazycat.android.popularmovies.app.ui;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.lazycat.android.popularmovies.app.R;
import com.lazycat.android.popularmovies.app.data.MovieContract;
import com.lazycat.android.popularmovies.app.utils.NetworkUtils;
import com.lazycat.android.popularmovies.app.utils.Utility;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";
    private static final int MOVIE_LOADER = 0;
    private int mPosition = GridView.INVALID_POSITION;
    private GridView mGridView;

    public static final String FLAVOR_MOVIE_PARCEL_KEY = "flavorMovie.parcel.key";
    public static final String SORT_BY_KEY = "movie_sort_order";
    public static final String FLAVOR_MOVIES_SAVEDINSTANCESTATE_KEY = "flavorMovie.savedInstanceState.key";
    public static final String SORT_BY_SAVEDINSTANCESTATE_KEY = "sortBy.savedInstanceState.key";

    // Adapter for movie poster grid view
    private FlavorMovieAdapter mFlavorMovieAdapter;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    // These indices are tied to MOVIE_COLUMNS. If MOVIE_COLUMNS changes, these must change.
    static final int COL_ID = 0;
    static final int COL_POSTER_PATH = 1;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // always call super onCreate
        super.onCreate(savedInstanceState);

//        if (savedInstanceState != null) {
//            mFlavorMovieList = savedInstanceState.getParcelableArrayList(FLAVOR_MOVIES_SAVEDINSTANCESTATE_KEY);
//
//            mSortBy = savedInstanceState.getString(SORT_BY_SAVEDINSTANCESTATE_KEY);
//        } else {
//            mFlavorMovieList = new ArrayList<FlavorMovie>();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // create a adapter to establish a bridge between the grid view item and movie array data
        mFlavorMovieAdapter = new FlavorMovieAdapter(getActivity(), null, 0);

        mGridView = (GridView) rootView.findViewById(R.id.gridview_poster);
        mGridView.setAdapter(mFlavorMovieAdapter);

        // Add onItemClickListener to handle what happen when an item is clicked
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    // Build the date URI
                    Uri movieUri = MovieContract.MovieEntry.buildMovieUri(
                            cursor.getLong(COL_ID));
                    // Send the date URI to main activity callback
                    ((Callback) getActivity()).onItemSelected(movieUri);
                }
                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that running their device sideways
        // does crazy lifecycle related things. It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but date or place in the app was never
        // actually "lost".
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // /the listview probably hasn't even been populated yet. Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get sort order from preference and resolve to content provider sortOrder string
        String sortOrder = Utility.getContentProviderSortOrder(
                getActivity(),
                Utility.getPreferenceSortOrder(getActivity()));
        Bundle bundle = new Bundle();
        bundle.putString(SORT_BY_KEY, sortOrder);

        // init loader to retrieve movies from content provider
        getLoaderManager().initLoader(MOVIE_LOADER, bundle, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save the list of flavor movies to eliminate internet call again
//        outState.putParcelableArrayList(FLAVOR_MOVIES_SAVEDINSTANCESTATE_KEY, mFlavorMovieList);

        // save current sorting order
//        outState.putString(SORT_BY_SAVEDINSTANCESTATE_KEY, mSortBy);

        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to ListView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public void onSortOrderChanged() {
        // get sort by from share preference
        String prefSortOrder = Utility.getPreferenceSortOrder(getActivity());

        // check the network status before call theMovieDB api!!
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            new FetchMovieTask(getActivity(), mFlavorMovieAdapter).execute(
                    prefSortOrder,
                    getActivity().getString(R.string.themoviedb_api_key));
        } else {
            Toast.makeText(getActivity(), getString(R.string.msg_network_not_available), Toast.LENGTH_SHORT).show();
        }

        // get sort order from preference and resolve to content provider sortOrder string
        String sortOrder = Utility.getContentProviderSortOrder(
                getActivity(),
                prefSortOrder);
        Bundle bundle = new Bundle();
        bundle.putString(SORT_BY_KEY, sortOrder);

        getLoaderManager().restartLoader(MOVIE_LOADER, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        String sortOrder = bundle.getString(SORT_BY_KEY);

        return new CursorLoader(getActivity(), movieUri, MOVIE_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        mFlavorMovieAdapter.swapCursor(data);

//        if (mFlavorMovieAdapter.getCount() > 0) {
//            mGridView.getItemAtPosition(0);
//        }

        if (mPosition != GridView.INVALID_POSITION) {
            // if we don't need to restart the loader, and there's a desired position on restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        mFlavorMovieAdapter.swapCursor(null);
    }
}

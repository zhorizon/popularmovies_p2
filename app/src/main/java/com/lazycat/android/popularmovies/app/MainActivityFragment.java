package com.lazycat.android.popularmovies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public static final String FLAVOR_MOVIE_PARCEL_KEY = "flavorMovie.parcel.key";
    public static final String FLAVOR_MOVIES_SAVEDINSTANCESTATE_KEY = "flavorMovie.savedInstanceState.key";
    public static final String SORT_BY_SAVEDINSTANCESTATE_KEY = "sortBy.savedInstanceState.key";

    // Adapter for movie poster grid view
    private FlavorMovieAdapter mFlavorMovieAdapter;

    // List of flavor movie will be saved in bundle
    private ArrayList<FlavorMovie> mFlavorMovieList;

    // Current sorting by value, will be saved in bundle
    private String mSortBy = null;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // always call super onCreate
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mFlavorMovieList = savedInstanceState.getParcelableArrayList(FLAVOR_MOVIES_SAVEDINSTANCESTATE_KEY);

            mSortBy = savedInstanceState.getString(SORT_BY_SAVEDINSTANCESTATE_KEY);
        } else {
            mFlavorMovieList = new ArrayList<FlavorMovie>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // create a adapter to establish a bridge between the grid view item and movie array data
        mFlavorMovieAdapter = new FlavorMovieAdapter(getActivity(),
                R.layout.list_item_poster,
                mFlavorMovieList);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_poster);
        gridView.setAdapter(mFlavorMovieAdapter);

        // Add onItemClickListener to handle what happen when an item is clicked
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                FlavorMovie flavorMovie = mFlavorMovieAdapter.getItem(position);

                if (flavorMovie != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(FLAVOR_MOVIE_PARCEL_KEY, flavorMovie);
                    intent.putExtras(bundle);

                    // start the detail activity
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "flavorMovie is null");
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // get sort by from share preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPref.getString(
                getString(R.string.pref_order_by_key),
                getString(R.string.pref_order_by_default));

        // if back from settings activity, the preference value may be changed
        if (mSortBy == null || mSortBy.compareTo(sortBy) != 0) {
            mSortBy = sortBy;
        }

        // check the network status before call theMovieDB api!!
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            new FetchMovieTask().execute(mSortBy);
        } else {
            Toast.makeText(getActivity(), getString(R.string.msg_network_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // save the list of flavor movies to eliminate internet call again
        savedInstanceState.putParcelableArrayList(FLAVOR_MOVIES_SAVEDINSTANCESTATE_KEY, mFlavorMovieList);

        // save current sorting order
        savedInstanceState.putString(SORT_BY_SAVEDINSTANCESTATE_KEY, mSortBy);

        // always call super onSaveInstanceState
        super.onSaveInstanceState(savedInstanceState);
    }

    private class FetchMovieTask extends AsyncTask<String, Void, FlavorMovie[]> {
        @Override
        protected FlavorMovie[] doInBackground(String... params) {
            if (params.length == 0)
                return null;

            String sortBy = params[0];
            String apiKey = getString(R.string.themoviedb_api_key);

            if (sortBy == null) {
                Log.d(LOG_TAG, "sortBy is null");
                return null;
            }

            if (apiKey == null) {
                Log.d(LOG_TAG, "apiKey is null");
                return null;
            }

            // download movies data from themoviedb
            String moviesJsonStr = DownloadUtils.discoverMoviesFromTheMovieDb(apiKey, sortBy);

            if (moviesJsonStr == null) {
                Log.d(LOG_TAG, "moviesJsonStr is null");
                return null;
            }

            // parse the return JSON string to flavor movie object array
            return DownloadUtils.getMovieDataFromJson(moviesJsonStr);
        }

        @Override
        protected void onPostExecute(FlavorMovie[] flavorMovies) {
            super.onPostExecute(flavorMovies);

            // manipulate the data directly instead of the adapter, as it will be saved in bundle
            // if no need to saved in bundle, or not a instance member, can consider manipulate
            // the adapter directly
            mFlavorMovieList.clear();

            if (flavorMovies != null) {
                for (FlavorMovie flavorMovie : flavorMovies) {
                    mFlavorMovieList.add(flavorMovie);
                }
            } else {
                Log.d(LOG_TAG, "flavorMovies is null!");
            }

            // must notify the adapter data changed!!
            mFlavorMovieAdapter.notifyDataSetChanged();
        }
    }
}

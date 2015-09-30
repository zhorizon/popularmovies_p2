package com.lazycat.android.popularmovies.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.lazycat.android.popularmovies.app.R;
import com.lazycat.android.popularmovies.app.utils.Utility;

public class MainActivity extends ActionBarActivity implements MainFragment.Callback, ActionBar.OnNavigationListener {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    // Current sorting by value, will be saved in bundle
    private String mSortBy = null;

    private SpinnerAdapter mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String sortBy = Utility.getPreferenceSortOrder(this);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        // add spinner to action bar
        mSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.action_order_by,
                android.R.layout.simple_spinner_dropdown_item);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        String newSortOrder = (String) mSpinnerAdapter.getItem(position);
        String prefSortOrder;

        // resolve selected action to preference sort order key value
        prefSortOrder = Utility.getPreferenceSortOrder(this, newSortOrder);

        // save to preference
        String sortOrder = Utility.getPreferenceSortOrder(this);

        if (newSortOrder != null && !newSortOrder.equals(sortOrder)) {
            // save to preference
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.pref_order_by_key), prefSortOrder);
            editor.commit();

            onSortOrderChange();
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        onSortOrderChange();
    }

    private void onSortOrderChange() {
        // get sort by from share preference
        String sortBy = Utility.getPreferenceSortOrder(this);

        // if back from settings activity, the preference value may be changed
        if (sortBy != null && !sortBy.equals(mSortBy)) {
            // get the fragment by tag
            MainFragment mf = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main);

            if (null != mf) {
                // notify for sort order changed on main fragment
                mf.onSortOrderChanged();
            }

            mSortBy = sortBy;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment df = new DetailFragment();
            df.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, df, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            // Otherwise we need to launch a new detail activity
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}

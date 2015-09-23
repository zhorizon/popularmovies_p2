package com.lazycat.android.popularmovies.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null) {
            // get flavorMovie data from intent
            FlavorMovie flavorMovie = intent.getParcelableExtra(MainActivityFragment.FLAVOR_MOVIE_PARCEL_KEY);

            if (flavorMovie != null) {
                ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);

                // Build URL string
                String urlStr = DownloadUtils.buildPosterImageUrl(flavorMovie.getPosterPath());

                // check the network status here before access internet
                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                    if (urlStr != null) {
                        // Using Picasso to fetch poster image and load them into view
                        Picasso.with(getActivity()).load(urlStr).into(imageView);
                    } else {
                        Log.d(LOG_TAG, "urlStr is null!");
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.msg_network_not_available), Toast.LENGTH_SHORT).show();
                }

                // Fill others movie data
                ((TextView) rootView.findViewById(R.id.textView_title)).setText(flavorMovie.getOriginalTitle());
                ((TextView) rootView.findViewById(R.id.textView_overview)).setText(flavorMovie.getOverview());
                ((TextView) rootView.findViewById(R.id.textView_release_date)).setText(flavorMovie.getReleaseDateString());
                ((TextView) rootView.findViewById(R.id.textView_rating)).setText(flavorMovie.getRating());
            } else {
                Log.d(LOG_TAG, "flavorMovie is null");
            }
        } else {
            Log.d(LOG_TAG, "intent is null");
        }

        return rootView;
    }
}

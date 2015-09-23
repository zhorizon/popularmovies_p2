package com.lazycat.android.popularmovies.app.utils;

import android.net.Uri;
import android.util.Log;

import com.lazycat.android.popularmovies.app.FlavorMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Cencil on 8/9/2015.
 */
public class DownloadUtils {
    private final static String LOG_TAG = DownloadUtils.class.getSimpleName();

    public static String discoverMoviesFromTheMovieDb(String apiKey, String sortBy) {
        // Construct the URL to get movies data from themoviedb.org discover/movie endpoint
        // e.g.
        // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=asdfghjkl
        final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String SORT_BY_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";

        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(SORT_BY_PARAM, sortBy)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        Log.v(LOG_TAG, "discover movies URI: " + buildUri.toString());

        String movieJsonStr = null;

        try {
            movieJsonStr = fetchMovieJson(new URL(buildUri.toString()));
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "URL Error", e);
        }

        Log.v(LOG_TAG, "movieJsonStr: " + movieJsonStr);

        return movieJsonStr;
    }

    public static FlavorMovie[] getMovieDataFromJson(String movieJsonStr) {
        FlavorMovie[] flavorMovies = null;

        // data we are interested to
        final String OWN_RESULTS = "results";
        final String OWN_ID = "id";
        final String OWN_TITLE = "title";
        final String OWN_ORIGINAL_TITLE = "original_title";
        final String OWN_OVERVIEW = "overview";
        final String OWN_POSTER_PATH = "poster_path";
        final String OWN_BACKDROP_PATH = "backdrop_path";
        final String OWN_RELEASE_DATE ="release_date";
        final String OWN_POPULARITY = "popularity";
        final String OWN_VOTE_AVERAGE = "vote_average";
        final String OWN_VOTE_COUNT = "vote_count";
        final String OWN_ADULT = "adult";
        final String OWN_VIDEO = "video";

        try {
            // get the root object from JSON string
            JSONObject jsonObject = new JSONObject(movieJsonStr);

            // get the results list
            JSONArray jsonArray = jsonObject.getJSONArray(OWN_RESULTS);

            flavorMovies = new FlavorMovie[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                // get the movie data from results list
                JSONObject jsonMovieObject = jsonArray.getJSONObject(i);

                // extract movie data and keep it in flavor movie object
                FlavorMovie movie = new FlavorMovie();
                movie.setId(jsonMovieObject.getInt((OWN_ID)));
                movie.setTitle(jsonMovieObject.getString(OWN_TITLE));
                movie.setOriginalTitle(jsonMovieObject.getString(OWN_ORIGINAL_TITLE));
                movie.setOverview(jsonMovieObject.getString(OWN_OVERVIEW));
                movie.setPosterPath(jsonMovieObject.getString(OWN_POSTER_PATH));
                movie.setBackdropPath(jsonMovieObject.getString(OWN_BACKDROP_PATH));
                movie.setPopularity((float) jsonMovieObject.getDouble(OWN_POPULARITY));
                movie.setVoteAverage(jsonMovieObject.getInt(OWN_VOTE_AVERAGE));
                movie.setVoteCount(jsonMovieObject.getInt(OWN_VOTE_COUNT));
                movie.setAdult(jsonMovieObject.getBoolean(OWN_ADULT));
                movie.setVideo(jsonMovieObject.getBoolean(OWN_VIDEO));

                // special handling for date datatype
                String releaseDateStr = jsonMovieObject.getString(OWN_RELEASE_DATE);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                movie.setReleaseDate(formatter.parse(releaseDateStr));

                flavorMovies[i] = movie;
            }

        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSON Error", e);

            return null;
        } catch (ParseException e) {
            Log.d(LOG_TAG, "Parse date format error", e);
        }

        return flavorMovies;
    }

    public static String fetchMovieJson(URL url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;

        try {
            // open Http connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream is = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (is == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }

            movieJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.d(LOG_TAG, "Error", e);

            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null)
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
        }

        return movieJsonStr;
    }

    public static String buildPosterImageUrl(String posterPath) {
        // build URL to fetch the poster image
        // e.g.
        // http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg

        String BASE_URL = "http://image.tmdb.org/t/p/";
        String SIZE = "w185";

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(SIZE)
                .appendEncodedPath(posterPath).build();

        return uri.toString();
    }
}

package com.lazycat.android.popularmovies.app.utils;

import android.net.Uri;
import android.util.Log;

import com.lazycat.android.popularmovies.app.FlavorMovie;
import com.lazycat.android.popularmovies.app.MovieReview;
import com.lazycat.android.popularmovies.app.MovieVideo;

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
            movieJsonStr = downloadJson(new URL(buildUri.toString()));
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "URL Error", e);
        }

        Log.v(LOG_TAG, "movieJsonStr: " + movieJsonStr);

        return movieJsonStr;
    }

    public static String downloadMovieVideos(String apiKey, long movieId) {
        // Construct the URL to get movie videos from themoviedb.org
        // e.g.
        // http://api.themoviedb.org/3/movie/id/videos

        final String BASE_URL = "http://api.themoviedb.org/3/movie";
        final String VIDEOS_PATH = "videos";
        final String API_KEY_PARM = "api_key";

        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(Long.toString(movieId))
                .appendPath(VIDEOS_PATH)
                .appendQueryParameter(API_KEY_PARM, apiKey)
                .build();

        Log.v(LOG_TAG, "movie videos URI: " + buildUri.toString());

        String videoJsonStr = null;

        try {
            videoJsonStr = downloadJson(new URL(buildUri.toString()));
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "URL Error", e);
        }

        Log.v(LOG_TAG, "videoJsonStr: " + videoJsonStr);

        return videoJsonStr;
    }

    public static String downloadMovieReviews(String apiKey, long movieId) {
        // Construct the URL to get movie reviews from themoviedb.org
        // e.g.
        // http://api.themoviedb.org/3/movie/id/previews

        final String BASE_URL = "http://api.themoviedb.org/3/movie";
        final String REVIEWS_PATH = "reviews";
        final String API_KEY_PARM = "api_key";

        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(Long.toString(movieId))
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(API_KEY_PARM, apiKey)
                .build();

        Log.v(LOG_TAG, "reviews URI: " + buildUri.toString());

        String reviewJsonStr = null;

        try {
            reviewJsonStr = downloadJson(new URL(buildUri.toString()));
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "URL Error", e);
        }

        Log.v(LOG_TAG, "reviewJsonStr: " + reviewJsonStr);

        return reviewJsonStr;
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
            JSONArray movieArray = jsonObject.getJSONArray(OWN_RESULTS);

            flavorMovies = new FlavorMovie[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {
                // get the movie data from results list
                JSONObject jsonMovieObject = movieArray.getJSONObject(i);

                // extract movie data and keep it in flavor movie object
                FlavorMovie movie = new FlavorMovie();
                movie.setId(jsonMovieObject.getInt((OWN_ID)));
                movie.setTitle(isNull(jsonMovieObject.getString(OWN_TITLE)));
                movie.setOriginalTitle(isNull(jsonMovieObject.getString(OWN_ORIGINAL_TITLE)));
                movie.setOverview(isNull(jsonMovieObject.getString(OWN_OVERVIEW)));
                movie.setPosterPath(isNull(jsonMovieObject.getString(OWN_POSTER_PATH)));
                movie.setBackdropPath(isNull(jsonMovieObject.getString(OWN_BACKDROP_PATH)));
                movie.setPopularity((float) jsonMovieObject.getDouble(OWN_POPULARITY));
                movie.setVoteAverage(jsonMovieObject.getInt(OWN_VOTE_AVERAGE));
                movie.setVoteCount(jsonMovieObject.getInt(OWN_VOTE_COUNT));
                movie.setAdult(jsonMovieObject.getBoolean(OWN_ADULT));
                movie.setVideo(jsonMovieObject.getBoolean(OWN_VIDEO));

                // special handling for date datatype
                String releaseDateStr = isNull(jsonMovieObject.getString(OWN_RELEASE_DATE));
                if (releaseDateStr != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    movie.setReleaseDate(formatter.parse(releaseDateStr));
                }

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

    public static MovieVideo[] getVideoDataFromJson(String jsonStr) {
        MovieVideo[] videos = null;

        // data we are interested to
        final String OWN_RESULTS = "results";
        final String OWN_ID = "id";
        final String OWN_KEY = "key";
        final String OWN_NAME = "name";
        final String OWN_SITE = "site";
        final String OWN_SIZE = "size";
        final String OWN_TYPE = "type";

        try {
            // get the root object from JSON string
            JSONObject jsonObject = new JSONObject(jsonStr);

            // get the results list
            JSONArray videoArray = jsonObject.getJSONArray(OWN_RESULTS);

            videos = new MovieVideo[videoArray.length()];

            for (int i = 0; i < videoArray.length(); i++) {
                // get the video data from results list
                JSONObject jsonVideoObject = videoArray.getJSONObject(i);

                // extract movie data and keep it in flavor movie object
                MovieVideo video = new MovieVideo();
                video.setId(jsonVideoObject.getString((OWN_ID)));
                video.setKey(jsonVideoObject.getString(OWN_KEY));
                video.setName(jsonVideoObject.getString(OWN_NAME));
                video.setSite(jsonVideoObject.getString(OWN_SITE));
                video.setSize(jsonVideoObject.getInt(OWN_SIZE));
                video.setType(jsonVideoObject.getString(OWN_TYPE));

                videos[i] = video;
            }

        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSON Error", e);

            return null;
        }

        return videos;
    }

    public static MovieReview[] getReviewDataFromJson(String jsonStr) {
        MovieReview[] previews = null;

        // data we are interested to
        final String OWN_RESULTS = "results";
        final String OWN_ID = "id";
        final String OWN_AUTHOR = "author";
        final String OWN_CONTENT = "content";
        final String OWN_URL = "url";

        try {
            // get the root object from JSON string
            JSONObject jsonObject = new JSONObject(jsonStr);

            // get the results list
            JSONArray previewArray = jsonObject.getJSONArray(OWN_RESULTS);

            previews = new MovieReview[previewArray.length()];

            for (int i = 0; i < previewArray.length(); i++) {
                // get the video data from results list
                JSONObject jsonPreviewObject = previewArray.getJSONObject(i);

                // extract movie data and keep it in flavor movie object
                MovieReview preview = new MovieReview();
                preview.setId(jsonPreviewObject.getString((OWN_ID)));
                preview.setAuthor(jsonPreviewObject.getString(OWN_AUTHOR));
                preview.setContent(jsonPreviewObject.getString(OWN_CONTENT));
                preview.setUrl(jsonPreviewObject.getString(OWN_URL));

                previews[i] = preview;
            }

        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSON Error", e);

            return null;
        }

        return previews;
    }

    public static String downloadJson(URL url) {
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

    public static String buildBackdropImageUrl(String path) {
        // build URL to fetch the poster image
        // e.g.
        // http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg

        String BASE_URL = "http://image.tmdb.org/t/p/";
        String SIZE = "w500";

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(SIZE)
                .appendEncodedPath(path).build();

        return uri.toString();
    }

    public static String isNull(String str) {
        return ("null".equals(str)) ? null : str;
    }
}

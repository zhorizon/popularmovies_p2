package com.lazycat.android.popularmovies.app.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Cencil on 9/25/2015.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
//        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR), WeatherProvider.WEATHER_WITH_LOCATION);
//        assertEquals("Error: The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR), WeatherProvider.WEATHER_WITH_LOCATION_AND_DATE);
//        assertEquals("Error: The LOCATION URI was matched incorrectly.",
//                testMatcher.match(TEST_LOCATION_DIR), WeatherProvider.LOCATION);
    }
}

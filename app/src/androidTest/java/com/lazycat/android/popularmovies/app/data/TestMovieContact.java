package com.lazycat.android.popularmovies.app.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Cencil on 9/25/2015.
 */
public class TestMovieContact extends AndroidTestCase {
    private static final long TEST_MOVIE_ID = 999L;

    public void testBuildMovie() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);

        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovie in " +
                        "MovieContract.",
                movieUri);
//        assertEquals("Error: Movie not properly appended to the end of the Uri",
//                TEST_WEATHER_LOCATION, locationUri.getLastPathSegment());
        assertEquals("Error: Movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.lazycat.android.popularmovies.app/movie/999");
    }
}

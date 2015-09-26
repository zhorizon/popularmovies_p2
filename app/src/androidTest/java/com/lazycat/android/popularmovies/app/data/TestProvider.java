package com.lazycat.android.popularmovies.app.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.lazycat.android.popularmovies.app.data.MovieContract.MovieEntry;

/**
 * Created by Cencil on 9/25/2015.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(MovieEntry.CONTENT_URI,
                null,
                null);

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegister() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://com.lazycat.android.popularmovies.app/movie/
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.lazycat.android.popluarmovies.app/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

//        String testLocation = "94074";
//        // content://com.example.android.sunshine.app/weather/94074
//        type = mContext.getContentResolver().getType(
//                WeatherEntry.buildWeatherLocation(testLocation));
//        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
//        assertEquals("Error: the WeatherEntry CONTENT_URI with location should return WeatherEntry.CONTENT_TYPE",
//                WeatherEntry.CONTENT_TYPE, type);
//
//        long testDate = 1419120000L; // December 21st, 2014
//        // content://com.example.android.sunshine.app/weather/94074/20140612
//        type = mContext.getContentResolver().getType(
//                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
//        // vnd.android.cursor.item/com.example.android.sunshine.app/weather/1419120000
//        assertEquals("Error: the WeatherEntry CONTENT_URI with location and date should return WeatherEntry.CONTENT_ITEM_TYPE",
//                WeatherEntry.CONTENT_ITEM_TYPE, type);
//
//        // content://com.example.android.sunshine.app/location/
//        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
//        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
//        assertEquals("Error: the LocationEntry CONTENT_URI should return LocationEntry.CONTENT_TYPE",
//                LocationEntry.CONTENT_TYPE, type);
    }

    public void testBasicMovieQuery() {
        // insert our test records in to the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();
        long movieId = db.insert(MovieEntry.TABLE_NAME, null, testValues);
        assertTrue("Unable to insert MovieEntry into the Database", movieId != -1);

        db.close();

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        // Make sure we get the conrrect cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", cursor, testValues);
    }

    public void testUpdateMovie() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieValues();

        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        long movieId = ContentUris.parseId(movieUri);

        // Verify we got a row back;
        assertTrue(movieId != -1);
        Log.d(LOG_TAG, "New row id: " + movieId);

        ContentValues updateValues = new ContentValues(values);
        updateValues.put(MovieEntry._ID, movieId);
        updateValues.put(MovieEntry.COLUMN_TITLE, "Avengers2");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updateValues, MovieEntry._ID + "=?",
                new String[] { Long.toString(movieId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called. If not, we throw an assertion.
        // If our code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                MovieEntry._ID + " = " + movieId,
                null,
                null);

        TestUtilities.validateCursor("testUpdateMovie, Error validating movie entry update.",
                cursor, updateValues);

        cursor.close();
    }

    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our movie delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        deleteAllRecordsFromProvider();

        // If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        movieObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertMovieValues() {
        long currentTestDate = TestUtilities.TEST_DATE;
        long millisecondsInADay = 1000*60*60*24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentTestDate+= millisecondsInADay ) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry._ID, i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Superman " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Superman " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "This is Superman " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "abcde");
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, "abcedfe");
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, currentTestDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 1.1 + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 1 + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, 5 + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ADULT, "false");
            movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, "true");
            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
    }

    public void testBulkInsert() {
        // Now we can bulkInsert some movie.  In fact, we only implement BulkInsert for movie
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, bulkInsertContentValues);

        // If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                MovieEntry._ID + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}

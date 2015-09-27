package com.lazycat.android.popularmovies.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.lazycat.android.popularmovies.app.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Cencil on 9/25/2015.
 */
public class TestUtilities extends AndroidTestCase {
    static final long TEST_DATE = 1419033600L;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry._ID, 1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Superman");
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Superman");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "This is Superman");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "abcde");
        movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, "abcedfe");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, TEST_DATE);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 1.1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 1.2);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, 2);
        movieValues.put(MovieContract.MovieEntry.COLUMN_ADULT, "false");
        movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, "true");

        return movieValues;
    }

    static long insertMovieValues(Context context) {
        // insert our test records in the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues();

        long movieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back
        assertTrue("Error: Failure to insert movie values", movieId != -1);

        return movieId;
    }

    /*
        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}

package com.lazycat.android.popularmovies.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Cencil on 9/25/2015.
 */
public class TestDb extends AndroidTestCase {

    public static final String TAG_LOG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the movie entry
        // and movie entry tables
        assertTrue("Error: Your database was created without the movie entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ADULT);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VIDEO);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required movie
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                movieColumnHashSet.isEmpty());
        db.close();
    }

    public void testMovieTable() {
        // First step: Get reference to writable database
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createMovieValues TestUtilities function if you wish)
        ContentValues movieValues = TestUtilities.createMovieValues();

        // Insert ContentValues into database and get a row ID back
        Long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);

        assertTrue(movieRowId != -1);

        // Query the database and receive a Cursor back
        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null, null,
                null);

        // Move the cursor to a valid database row
        assertTrue("Error: NO records returned from movie query", c.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed", c, movieValues);

        assertFalse("Error: More than one record returned from movie query", c.moveToNext());

        // Finally, close the cursor and database
        c.close();
        db.close();
    }
}

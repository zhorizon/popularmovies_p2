package com.lazycat.android.popularmovies.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lazycat.android.popularmovies.app.data.MovieContract.MovieEntry;
import com.lazycat.android.popularmovies.app.data.MovieContract.VideoEntry;
import com.lazycat.android.popularmovies.app.data.MovieContract.ReviewEntry;

/**
 * Manages a local database for movie data.
 * Created by Cencil on 9/24/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "(" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_TITLE + " TEXT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NULL, " +
                MovieEntry.COLUMN_ADULT + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VIDEO + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NULL, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL);";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + "(" +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VideoEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                VideoEntry.COLUMN_ID + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_SITE + " TEXT NULL, " +
                VideoEntry.COLUMN_SIZE + " INTEGER NULL, " +
                VideoEntry.COLUMN_TYPE + " TEXT NULL, " +
                " FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + "(" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_ID + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NULL, " +
                ReviewEntry.COLUMN_URL + " TEXT NULL, " +
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

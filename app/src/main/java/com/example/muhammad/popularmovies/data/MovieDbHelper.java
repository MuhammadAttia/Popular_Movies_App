package com.example.muhammad.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.muhammad.popularmovies.data.MovieContract.MovieEntry;
import com.example.muhammad.popularmovies.data.MovieContract.ReviewEntry;
import com.example.muhammad.popularmovies.data.MovieContract.TrailerEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create movie table
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MARKED_FAVORITE + " INTEGER, " +

                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";

        //create trailer table
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY, " +
                TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +

                " UNIQUE (" + TrailerEntry.COLUMN_YOUTUBE_KEY + ") ON CONFLICT REPLACE);";

        //create review table
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + ")," +

                " UNIQUE (" + ReviewEntry.COLUMN_CONTENT + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);
    }

}

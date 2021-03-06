package com.example.muhammad.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muhammad.popularmovies.R;
import com.example.muhammad.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private static final int TRAILER_LOADER = 2;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS
    };

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY
    };

    // indices for columns
    static final int COL_DETAIL_ID = 0;
    static final int COL_DETAIL_POSTER_URL = 1;
    static final int COL_DETAIL_TITLE = 2;
    static final int COL_DETAIL_VOTE_AVERAGE = 3;
    static final int COL_DETAIL_RELEASE_DATE = 4;
    static final int COL_DETAIL_SYNOPSIS = 5;

    static final int COL_REVIEW_ID = 0;
    static final int COL_REVIEW_AUTHOR = 1;
    static final int COL_REVIEW_CONTENT = 2;

    static final int COL_TRAILER_ID = 0;
    static final int COL_TRAILER_KEY = 1;

    private int mMovieId;

    public ImageView mPosterView;
    public TextView mTitleView;
    public TextView mVoteAvgView;
    public TextView mReleaseDateView;
    public TextView mSynopsisView;

    public CheckBox mFavoritesButton;

    public LinearLayout mReviewLayout;
    public LinearLayout mTrailerLayout;

    public boolean mReviewDataAlreadyLoaded;
    public boolean mTrailerDataAlreadyLoaded;

    private String mFirstTrailerUrl;
    private ShareActionProvider mShareActionProvider;

    private LayoutInflater mLayoutInflater;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //retrieve movie id from bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieId = arguments.getInt(getString(R.string.detail_args));
        }

        mLayoutInflater = inflater;

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //movie detail views
        mPosterView = (ImageView) rootView.findViewById(R.id.poster_image);
        mTitleView = (TextView) rootView.findViewById(R.id.title_text);
        mVoteAvgView = (TextView) rootView.findViewById(R.id.vote_avg_text);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.release_date_text);
        mSynopsisView = (TextView) rootView.findViewById(R.id.synopsis_text);

        mFavoritesButton = (CheckBox) rootView.findViewById(R.id.favorites_button);
        mFavoritesButton.setChecked(getFavoritesPreference());
        mFavoritesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setFavoritesPreference(isChecked);
            }
        });

        //review and trailer layouts
        mReviewLayout = (LinearLayout) rootView.findViewById(R.id.review_layout);
        mTrailerLayout = (LinearLayout) rootView.findViewById(R.id.trailer_layout);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //keep track if dynamic data has already been loaded
        mReviewDataAlreadyLoaded = false;
        mTrailerDataAlreadyLoaded = false;

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        //initialize loaders
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);

        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        Uri detailUri = MovieContract.MovieEntry.buildDetailWithMovie(mMovieId);
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewWithMovie(mMovieId);
        Uri trailerUri = MovieContract.TrailerEntry.buildTrailerWithMovie(mMovieId);

        switch (loaderId) {
            case DETAIL_LOADER:
                return new CursorLoader(
                        getActivity(),
                        detailUri,
                        DETAIL_COLUMNS,
                        null,
                        null,
                        null
                );
            case REVIEW_LOADER:
                return new CursorLoader(
                        getActivity(),
                        reviewUri,
                        REVIEW_COLUMNS,
                        null,
                        null,
                        null);
            case TRAILER_LOADER:
                return new CursorLoader(
                        getActivity(),
                        trailerUri,
                        TRAILER_COLUMNS,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            //inflate detail view
            case DETAIL_LOADER:
                if (cursor != null && cursor.moveToFirst()) {

                    Picasso.with(getActivity()).load(cursor.getString(DetailActivityFragment.COL_DETAIL_POSTER_URL)).into(mPosterView);

                    String title = cursor.getString(DetailActivityFragment.COL_DETAIL_TITLE);
                    mTitleView.setText(title);

                    double voteAvg = cursor.getDouble(DetailActivityFragment.COL_DETAIL_VOTE_AVERAGE);
                    mVoteAvgView.setText(voteAvg + "/10");

                    String releaseDate = cursor.getString(DetailActivityFragment.COL_DETAIL_RELEASE_DATE);
                    mReleaseDateView.setText(releaseDate.substring(0, 4));

                    String synopsis = cursor.getString(DetailActivityFragment.COL_DETAIL_SYNOPSIS);
                    mSynopsisView.setText(synopsis);
                }

                break;

            //inflate review views dynamically
            case REVIEW_LOADER:
                if (!mReviewDataAlreadyLoaded) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        View reviewView = mLayoutInflater.inflate(R.layout.list_item_review, null);

                        if (cursor.isFirst()) {
                            TextView reviewSectionView = (TextView) reviewView.findViewById(R.id.list_item_review_title);
                            reviewSectionView.setText(getText(R.string.review_title));
                        }

                        TextView authorView = (TextView) reviewView.findViewById(R.id.list_item_review_author);
                        String author = cursor.getString(DetailActivityFragment.COL_REVIEW_AUTHOR);
                        authorView.setText("Author: " + author);

                        TextView contentView = (TextView) reviewView.findViewById(R.id.list_item_review_content);
                        String content = cursor.getString(DetailActivityFragment.COL_REVIEW_CONTENT);
                        contentView.setText("" + content);

                        mReviewLayout.addView(reviewView);
                    }
                    mReviewDataAlreadyLoaded = true;
                }
                break;

            //inflate trailer views dynamically
            case TRAILER_LOADER:
                if (!mTrailerDataAlreadyLoaded) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        View trailerView = mLayoutInflater.inflate(R.layout.list_item_trailer, null);

                        //store first trailer url for share intent
                        if (cursor.isFirst()) {
                            mFirstTrailerUrl = cursor.getString(COL_DETAIL_POSTER_URL);
                        }

                        Button button = (Button) trailerView.findViewById(R.id.list_item_trailer_button);
                        button.setId(cursor.getPosition());
                        button.setText("Watch Trailer " + (cursor.getPosition() + 1));

                        final String trailerUrl = cursor.getString(COL_DETAIL_POSTER_URL);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerUrl));
                                startActivity(intent);
                            }
                        });

                        mTrailerLayout.addView(trailerView);
                    }
                    mTrailerDataAlreadyLoaded = true;
                }
                break;
            default:
                break;
        }

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrailerUrlIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mFirstTrailerUrl != null) {
            mShareActionProvider.setShareIntent(createShareTrailerUrlIntent());
        }
    }

    private Intent createShareTrailerUrlIntent() {
        //share youtubelink
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");

        if (mFirstTrailerUrl == null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, "No trailer available to share!");
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + mFirstTrailerUrl);
        }

        return shareIntent;
    }

    public void setFavoritesPreference(boolean isFavorite) {
        //keep track of favorites setting in database
        ContentValues favoritesValues = new ContentValues();
        favoritesValues.put(MovieContract.MovieEntry.COLUMN_MARKED_FAVORITE, isFavorite);

        getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                favoritesValues,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{"" + mMovieId});
    }

    public boolean getFavoritesPreference() {

        //retrieve favorites preference
        Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MARKED_FAVORITE},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{"" + mMovieId},
                null);

        boolean isFavorite = false;

        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getInt(1) == 1) isFavorite = true;
        }

        return isFavorite;
    }
}

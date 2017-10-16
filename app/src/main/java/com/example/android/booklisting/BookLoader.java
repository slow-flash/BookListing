package com.example.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import static com.example.android.booklisting.MainActivity.LOG_TAG;

/**
 * Created by Shahzaib on 6/15/2017.
 */

public class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {

    private String url;

    public BookLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public ArrayList<Book> loadInBackground() {

        Log.v(LOG_TAG, "Loading in Background");

        ArrayList<Book> books = QueryUtils.getBookDataFromServer(this.url);
        return books;
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "Starting to load");
        forceLoad();
    }

}

package com.example.android.booklisting;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Book>>{

    public static final String LOG_TAG = MainActivity.class.getName();

    /** URL for book data from the Google Books API */
    private String bookRequestUrl = "https://www.googleapis.com/books/v1/volumes?";

    // sample url
    // "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10";

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    private TextView emptyView;

    private ListView bookListView;

    private ProgressBar progressBar;

    private BookAdapter adapter;

    private Uri baseUri;

    private Uri.Builder uriBuilder;

    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button searchButton = (Button) findViewById(R.id.search_button);


        // Find a reference to the {@link ListView} in the layout
        bookListView = (ListView) findViewById(R.id.list);

        emptyView = (TextView) findViewById(R.id.empty_view);

        progressBar = (ProgressBar) findViewById(R.id.progress_indicator);

        bookListView.setEmptyView(emptyView);

        loaderManager = getLoaderManager();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(loaderManager.getLoader(BOOK_LOADER_ID) != null)
                {
                    loaderManager.destroyLoader(BOOK_LOADER_ID);
                }

                doButtonClick();
            }
        });
    }

    public void updateUi()
    {
        adapter = new BookAdapter(this, new ArrayList<Book>());

        // Create a new {@link ArrayAdapter} of books
      /*  ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, earthquakes);*/

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(adapter);
    }

    @Override
    public AsyncTaskLoader<ArrayList<Book>> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "Creating the loader");
        return new BookLoader(MainActivity.this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> earthquakes) {

        Log.v(LOG_TAG, "Finishing the load");

        // Clear the adapter of previous book data
        adapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            adapter.addAll(earthquakes);
        }

        progressBar.setVisibility(View.GONE);
        emptyView.setText(R.string.no_books);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {
        Log.v(LOG_TAG, "Reseting the loader");
        adapter.clear();
    }

    public void doButtonClick()
    {
        emptyView.setText("");

        progressBar.setVisibility(View.VISIBLE);



        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());

        updateUi();

        EditText searchQuery = (EditText) findViewById(R.id.search_query);


        if(isConnected)
        {
            baseUri = Uri.parse(bookRequestUrl);
            uriBuilder = baseUri.buildUpon();

            uriBuilder.appendQueryParameter("q", "" + searchQuery.getText());
            uriBuilder.appendQueryParameter("maxResults", "10");

            //loaderManager = getLoaderManager();

            loaderManager.initLoader(BOOK_LOADER_ID, null, this);

            Log.v(LOG_TAG, "Initializing the loader");
        }
        else
        {
            emptyView.setText(R.string.no_internet);
        }
    }

    public class BookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>>
    {

        @Override
        protected ArrayList<Book> doInBackground(String... urls) {

            if(urls.length <= 0 || urls[0] == null)
                return null;

            ArrayList<Book> books = QueryUtils.getBookDataFromServer(bookRequestUrl);
            return books;

        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {

            if(books == null)
                return;;

            updateUi();
        }
    }
}

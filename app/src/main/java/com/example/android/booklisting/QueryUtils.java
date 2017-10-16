package com.example.android.booklisting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.android.booklisting.MainActivity.LOG_TAG;

/**
 * Created by Shahzaib on 6/15/2017.
 */

public final class QueryUtils {

    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            int statusCode = urlConnection.getResponseCode();
            if(statusCode == 200)
            {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else
            {
                Log.e(LOG_TAG, "Error response code: " + statusCode);
            }
        } catch (IOException e) {
            // TODO: Handle the exception
            Log.e(LOG_TAG, "Can't obtain the json response");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    private ArrayList<Book> extractBooks(String jsonResponse) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Book> books = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Book objects with the corresponding data.

            JSONObject data = new JSONObject(jsonResponse);
            JSONArray items = data.getJSONArray("items");

            int length = items.length();

            for(int i = 0; i < length; i++)
            {
                JSONObject book = items.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                JSONArray authors = volumeInfo.getJSONArray("authors");
                int authorCount = authors.length();

                ArrayList<String> bookAuthors = new ArrayList<>();

                for(int j = 0; j < authorCount; j++)
                {
                    bookAuthors.add(authors.getString(j));
                }

                String infoUrl = volumeInfo.getString("infoLink");

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String thumbnailUrl = imageLinks.getString("smallThumbnail");

                Bitmap bitmap = BitmapFactory.decodeStream(createUrl(thumbnailUrl).openConnection().getInputStream());

                Book currentBook = new Book(title, bookAuthors, bitmap, infoUrl);
                books.add(currentBook);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the list of books
        return books;
    }



    public static ArrayList<Book> getBookDataFromServer(String requestUrl)
    {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.v(LOG_TAG, "Obtaining data from the server");

        QueryUtils queryUtils = new QueryUtils();

        // Create URL object
        URL url = queryUtils.createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = queryUtils.makeHttpRequest(url);
        } catch (IOException e) {
            // TODO Handle the IOException
        }

        ArrayList<Book> books = queryUtils.extractBooks(jsonResponse);

        return books;


    }


}

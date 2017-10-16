package com.example.android.booklisting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.android.booklisting.MainActivity.LOG_TAG;

/**
 * Created by Shahzaib on 6/15/2017.
 */

public class BookAdapter extends ArrayAdapter<Book>{

    public View listItemView;

    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context,0, books);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list, parent, false);
        }

        final Book currentBook = getItem(position);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);

        titleTextView.setText(currentBook.getTitle());

        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_text_view);

        if(currentBook.getAuthors().size() == 1)
        {
            authorTextView.setText(currentBook.getAuthors().get(0));
        }
        else
        {
            authorTextView.setText(currentBook.getAuthors().get(0) + " et al.");
        }


        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image_view);

        imageView.setImageBitmap(currentBook.getThumbnail());

        imageView.setVisibility(View.VISIBLE);

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage(currentBook.getInfoUrl());
            }
        });


        return listItemView;
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(intent);
        }
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

    public class BookAsyncTask extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {

            Bitmap bitmap = null;

            if(urls.length <= 0 || urls[0] == null)
                return null;

            try {
                bitmap = BitmapFactory.decodeStream(createUrl(urls[0]).openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if(bitmap == null)
                return;;

            ImageView imageView = (ImageView) listItemView.findViewById(R.id.image_view);

            imageView.setImageBitmap(bitmap);

            imageView.setVisibility(View.VISIBLE);
        }
    }

}

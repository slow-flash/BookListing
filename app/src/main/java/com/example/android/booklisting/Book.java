package com.example.android.booklisting;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Shahzaib on 6/15/2017.
 */

public class Book {

    private String title;
    private ArrayList<String> authors;
    private Bitmap thumbnail;
    private String infoUrl;

    public Book(String title, ArrayList<String> authors, Bitmap thumbnail, String infoUrl) {
        this.title = title;
        this.authors = authors;
        this.thumbnail = thumbnail;
        this.infoUrl = infoUrl;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getInfoUrl() {
        return infoUrl;
    }
}

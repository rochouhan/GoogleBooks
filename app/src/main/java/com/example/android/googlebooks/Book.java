package com.example.android.googlebooks;

import java.util.List;

public class Book {
    private String mTitle;
    private String mAuthors;
    private String mUrl;
    private String mImageUrl;

    public Book(String title, String authors, String url, String imageUrl){
        mTitle = title;
        mAuthors = authors;
        mUrl = url;
        mImageUrl = imageUrl;
    }

    public String getmAuthors() {
        return mAuthors;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    @Override
    public String toString() {
        return mTitle + mAuthors;
    }
}

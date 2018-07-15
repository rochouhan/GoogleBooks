package com.example.android.googlebooks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class BookListAdapter extends ArrayAdapter {
    private String LOG_TAG = BookListAdapter.class.getName();
    public BookListAdapter(Context context, int resource, ArrayList<Book> books){
        super(context, resource, books);
        Log.v(LOG_TAG, "Books list " + books);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Book book = (Book) getItem(position);
        Log.v(LOG_TAG, "Book " + book);
        TextView authorsView = (TextView) listItemView.findViewById(R.id.author);
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        ImageView imageView = listItemView.findViewById(R.id.book_cover);
        Log.v(LOG_TAG, "IMAGE URL: " + book.getmImageUrl());

        String smallThumbnailLink = book.getmImageUrl();
        Picasso.get().load(smallThumbnailLink).into(imageView);
//        Bitmap d = null;
//        try {
//            d = getBitmap(book.getmImageUrl());
//        }
//        catch (IOException e){
//            Log.v(LOG_TAG, "Unable to download image", e);
//        }

        String title = book.getmTitle();
        String authors = book.getmAuthors();
        Log.v(LOG_TAG, "Authors list " + book.getmAuthors());


        authorsView.setText(authors);
        titleView.setText(title);

        return listItemView;
    }

    public Bitmap getBitmap(String stringUrl) throws IOException {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(stringUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "Unable to download image", e);
        } catch (IOException e) {
            Log.v(LOG_TAG, "Unable to download image", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            return bitmap;
        }
    }
}

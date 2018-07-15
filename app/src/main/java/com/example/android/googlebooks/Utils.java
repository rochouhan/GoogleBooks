package com.example.android.googlebooks;

import android.app.LoaderManager;
import android.util.Log;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;

public final class Utils {
    private static String LOG_TAG = Utils.class.getName();


    private Utils(){}

    public static ArrayList<Book> extractBooks(String url)  {

        JSONObject booksData;
        List<String> authors;
        Book book;
        ArrayList<Book> books = new ArrayList<Book>();
        String jsonQuery = "";
        try {
            jsonQuery = getBookData(url);
        } catch (IOException e) {
            Log.v(LOG_TAG, "Unable to download data", e);
            return null;
        }
        try {

            booksData = new JSONObject(jsonQuery);
            Log.v(Utils.class.getName(), booksData.toString());
            JSONArray items = booksData.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject bookObject = items.getJSONObject(i);
                JSONObject volumeInfo = bookObject.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String imageUrl = imageLinks.getString("thumbnail");
                JSONArray authorsJson = new JSONArray();
                try {
                    authorsJson = volumeInfo.getJSONArray("authors");
                }
                catch (JSONException e){
                    Log.v(Utils.class.getName(), "No authors for" + title);
                }

                String infoLink = volumeInfo.getString("infoLink");
//                Log.v(Utils.class.getName(), "TITLE: " + title);
//                Log.v(Utils.class.getName(), "AUTHORS: " + authorsJson.toString());
                Log.v(Utils.class.getName(), "IMAGELINK: " + imageUrl);
                authors = new ArrayList<String>();
                for (int j = 0; j < authorsJson.length(); j++) {
                    String author = authorsJson.getString(j);
                    authors.add(author);
                }
                book = new Book(title, createAuthorsString(authors), infoLink, imageUrl);
                books.add(book);
            }
//                Log.v(Utils.class.getName(), book.toString());

        } catch (JSONException e) {
            Log.v(Utils.class.getName(), "Unable to create json object", e);
        }
        Log.v(Utils.class.getName(), books.toString());
        return books;
    }
    private static String getBookData(String urlString) throws IOException {
        //create url
        URL url = createUrl(urlString);

        return makeHttpRequest(url);

    }

    private static URL createUrl(String urlString){
        URL url= null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";
        if (url == null || url.toString().isEmpty()){
            return null;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200){
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG, "Error in response code: " + httpURLConnection.getResponseCode());
            }

        }
        catch (IOException e){
            Log.e(LOG_TAG, "Unable to receive results", e);
        }

        finally {
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }

        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder response = new StringBuilder();

        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null){
                response.append(line);
                line = bufferedReader.readLine();
            }

        }
        return response.toString();
    }

    public static String createAuthorsString(List<String> authors){
        if (authors == null || authors.isEmpty()){
            return "No authors";
        }
        String authorString = authors.get(0);
        authors.remove(0);
        for (String author: authors
                ) {
            authorString += ", " + author;
        }
        return authorString;
    }
}

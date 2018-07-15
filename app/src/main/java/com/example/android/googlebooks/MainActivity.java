package com.example.android.googlebooks;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>>{
    //initialize public variables
    private String mSearchText = "";
    private String SEARCH_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private final String DEFAULT_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static String LOG_TAG = MainActivity.class.getName();
    private BookListAdapter mBookListAdapter;
    protected void onCreate(Bundle savedInstanceState) {

        //set content view and mainactivity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookListAdapter = new BookListAdapter(getApplicationContext(), 0,new ArrayList<Book>());
//        Log.v(LOG_TAG, book.toString());
        final ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(mBookListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book earthquake = (Book) listView.getAdapter().getItem(position);
                String url = earthquake.getmUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        //get all the views
        final TextView textView = (TextView) findViewById(R.id.title);
        final SearchView searchView = (SearchView) findViewById(R.id.search_bar);
        final Button searchButton = (Button) findViewById(R.id.search_close_btn);

        //attach method to when the search button is clicked
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.v(LOG_TAG, "On Close");
                return true;
            }
        });
    }

    private void onSearch(){
        if (getLoaderManager().getLoader(1) != null){
            getLoaderManager().destroyLoader(1);
            Log.v(LOG_TAG, "Loader destroyed ");
        }
        mBookListAdapter.clear();
        createSearchURL();
        if (SEARCH_URL == DEFAULT_URL){
            Toast.makeText(getApplicationContext(), "Type in a search parameter", Toast.LENGTH_SHORT);
            return;
        }


        getLoaderManager().initLoader(1, null, this).startLoading();

        SearchView searchView =  findViewById(R.id.search_bar);
        // Check if no view has focus:
        searchView.clearFocus();
    }


    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        return new BookLoader(MainActivity.this, SEARCH_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
//        Log.v(LOG_TAG, "On Load Finished" + data);
        mBookListAdapter.clear();

//        Log.v(LOG_TAG, "On Load Finished 2" + data);
        if (data != null && !data.isEmpty()) {
            Log.v(LOG_TAG, "New Book List Adapter 1" + mBookListAdapter);
            mBookListAdapter.addAll(data);
            Log.v(LOG_TAG, "New Book List Adapter 2" + mBookListAdapter);
        }
        else{
            Toast.makeText(getApplicationContext(), "Type in a search parameter", Toast.LENGTH_SHORT);
        }

//        SearchView searchView = (SearchView) findViewById(R.id.search_bar);
//        searchView.setIconified(true);

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.v(LOG_TAG, "On Loader Reset");
        mBookListAdapter.clear();
    }

    public void createSearchURL(){
        SEARCH_URL = new String(DEFAULT_URL);
        SearchView searchView = findViewById(R.id.search_bar);
        String search =  searchView.getQuery().toString();
        search = search.replace(" ", "%20");
        if(search.isEmpty()){
            return;
        }
        SEARCH_URL += search;
        Log.v(LOG_TAG, "Search Url modified " + SEARCH_URL);


    }

    public static class BookLoader extends AsyncTaskLoader<List<Book>>{
        private String mUrl;
        public BookLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        public List<Book> loadInBackground() {

            if (mUrl == null){
                return null;
            }


            return Utils.extractBooks(mUrl);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }




}


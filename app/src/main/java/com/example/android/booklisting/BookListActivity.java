package com.example.android.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<GoogleBook>>{
    /** URL for earthquake data from the USGS dataset */
    private static final int BOOK_LOADER_ID = 1;
    private static final String GOOGLE_API_URL = "https://www.googleapis.com/books/v1/volumes?q=title:";
    private GoogleBookAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressBar;
    private String searchKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booklist_activity);

        ListView bookListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new GoogleBookAdapter(this, new ArrayList<GoogleBook>());

        // Set the adapter on the {@link ListView} so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        mProgressBar.setVisibility(View.GONE);
        final LoaderManager loaderManager = getLoaderManager();

        Button searchButton = (Button) findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                mAdapter.clear();
                mEmptyStateTextView.setText("");

                EditText keywordView = (EditText) findViewById(R.id.keywordText);
                searchKeyword = keywordView.getText().toString();

                if( TextUtils.isEmpty(searchKeyword) || searchKeyword == null ){
                    Toast.makeText(BookListActivity.this, getString(R.string.keyword_no_entered), Toast.LENGTH_SHORT).show();
                }else{
                    ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();

                    if (isConnected ){
                        mProgressBar.setIndeterminate(true);
                        mProgressBar.setVisibility(View.VISIBLE);
                        if(loaderManager.getLoader(BOOK_LOADER_ID) == null){
                            loaderManager.initLoader(BOOK_LOADER_ID, null,BookListActivity.this).forceLoad();
                        }else{
                            loaderManager.restartLoader(BOOK_LOADER_ID, null,BookListActivity.this).forceLoad();
                        }
                    }else {
                        mEmptyStateTextView.setText(R.string.no_network_conn);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

        if(loaderManager.getLoader(BOOK_LOADER_ID) != null) {
            loaderManager.initLoader(BOOK_LOADER_ID, null, BookListActivity.this).forceLoad();
        }else{
            mEmptyStateTextView.setText(R.string.keyword_no_entered);
        }
    }

    @Override
    public Loader<List<GoogleBook>> onCreateLoader(int id, Bundle args) {
        if ( searchKeyword == null || searchKeyword.isEmpty()){
            return null;
        }

        String formattedSearchKey = searchKeyword.toLowerCase().trim().replace(" ","+");
        String apiQuery = GOOGLE_API_URL + formattedSearchKey;
        Log.i("Test",apiQuery);
        return new BookLoader(this, apiQuery.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<GoogleBook>> loader, List<GoogleBook> earthquakes) {
        mAdapter.clear();
        mEmptyStateTextView.setText(R.string.no_books);

        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<GoogleBook>> loader) {
        mAdapter.clear();
    }
}

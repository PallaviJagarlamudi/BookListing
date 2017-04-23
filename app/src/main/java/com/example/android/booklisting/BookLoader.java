package com.example.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Pallavi J on 22-04-2017.
 */

public class BookLoader  extends AsyncTaskLoader<List<GoogleBook>> {
    String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<GoogleBook> loadInBackground() {
        if( mUrl == null){
            return null;
        }else{
            List<GoogleBook> earthquakeList = QueryUtils.extractGoogleBooksData(mUrl);
            return earthquakeList;
        }
    }
}

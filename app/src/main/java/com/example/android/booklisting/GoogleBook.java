package com.example.android.booklisting;

import java.util.ArrayList;

/**
 * Created by Pallavi J on 22-04-2017.
 */

public class GoogleBook {
    private String mTitle;
    private ArrayList<String> mAuthor;
    private String mImageUrl;

    public GoogleBook(String title, ArrayList<String> authors, String imageUrl) {
        this.mTitle = title;
        this.mAuthor = authors;
        this.mImageUrl = imageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public ArrayList<String> getAuthor() {
        return mAuthor;
    }

    public String getImageUrl() {
        return mImageUrl;
    }


    public boolean hasImage(){
        if ( mImageUrl == null || mImageUrl.isEmpty()){
            return false;
        }else{
            return true;
        }
    }
}

package com.example.android.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pallavi J on 23-04-2017.
 */

public class GoogleBookAdapter extends ArrayAdapter<GoogleBook> {
    Context mContext;

    public GoogleBookAdapter(Context context, List<GoogleBook> googleBooks) {
        super(context, 0, googleBooks);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        //If ItemView is already created reuse it, else create
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        //Set data to the layout fields
        GoogleBook currentBook = getItem(position);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        titleTextView.setText(currentBook.getTitle());

        ArrayList<String> authors = currentBook.getAuthor();
        String authorList = "";
        if (!authors.isEmpty() && authors != null) {
            for (String s : currentBook.getAuthor()) {
                authorList += s + "\n";
            }
        } else {
            authorList = getContext().getString(R.string.no_author);
        }
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        authorTextView.setText(authorList.trim());

        ImageView coverImageView = (ImageView) listItemView.findViewById(R.id.coverImageView);
        if (currentBook.hasImage()) {
            // loading album cover using Glide library
            Glide.with(mContext).load(currentBook.getImageUrl()).centerCrop().into(coverImageView);
        } else {
            Glide.with(mContext).load(R.drawable.cover_not_available).centerCrop().into(coverImageView);
        }
        return listItemView;
    }
}

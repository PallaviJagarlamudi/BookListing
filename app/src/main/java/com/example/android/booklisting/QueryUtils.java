package com.example.android.booklisting;

import android.text.TextUtils;
import android.util.Log;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pallavi J on 22-04-2017.
 */

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link GoogleBook} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<GoogleBook> extractGoogleBooksData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back from Google Book API
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link GoogleBook} List
        List<GoogleBook> GoogleBooks = extractVolumeInfoFromJson(jsonResponse);

        return GoogleBooks;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(15000 /* milliseconds */);
            urlConnection.setConnectTimeout(30000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                //Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the GoogleBook JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link GoogleBook} object by parsing out information
     * about the first GoogleBook from the input GoogleBookJSON string.
     */
    private static List<GoogleBook> extractVolumeInfoFromJson(String GoogleBookJSON) {

        ArrayList<GoogleBook> googleBooks = new ArrayList<>();
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(GoogleBookJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(GoogleBookJSON);
            JSONArray itemArray = baseJsonResponse.getJSONArray("items");

            // If there are results in the items array
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject volumeInfo = itemArray.getJSONObject(i).optJSONObject("volumeInfo");

                //extract the title
                String title = volumeInfo.getString("title");

                //extract the author
                ArrayList<String> authors = new ArrayList<String>();
                if (!volumeInfo.isNull("authors")){
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        for (int j = 0; j < authorsArray.length(); j++) {
                            authors.add( authorsArray.getString(j));
                        }
                }

                String imageUrl = "";
                if (!volumeInfo.isNull("imageLinks")){
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    if ( imageLinks != null ){
                        imageUrl = imageLinks.getString("smallThumbnail");
                    }
                }

                googleBooks.add(new GoogleBook(title, authors,  imageUrl));
            }
            return googleBooks;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the GoogleBook JSON results", e);
        }
        return null;
    }


}

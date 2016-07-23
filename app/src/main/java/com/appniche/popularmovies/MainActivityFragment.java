package com.appniche.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivityFragment extends Fragment {

    MovieGridViewAdapter movieGridViewAdapter;

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView)rootView.findViewById(R.id.gridView);
        movieGridViewAdapter = new MovieGridViewAdapter(getActivity());
        gridView.setAdapter(movieGridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie movie = (Movie)movieGridViewAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movie);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        new FetchMovieTask().execute();
        super.onStart();
    }

    //fetching movie data in background using AsyncTask

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        //parsing movie JSON Data
        private Movie[] getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            Movie movie[] = new Movie[movieArray.length()];
            final String BASE_URL = "http://image.tmdb.org/t/p/w185/" ;

            //my movie const...Movie(String mTitle, String mPosterUrl, String mOverview, String mReleaseDate, String mRating)

            for(int i=0;i<movieArray.length();i++){
                movie[i] = new Movie(
                        movieArray.getJSONObject(i).getString("original_title"),
                        BASE_URL + movieArray.getJSONObject(i).getString("poster_path"),
                        movieArray.getJSONObject(i).getString("overview"),
                        movieArray.getJSONObject(i).getString("release_date"),
                        movieArray.getJSONObject(i).getString("vote_average")
                );
            }
            return movie;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // A String variable @movieJsoStr will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the Movie Database query
                String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";

                //Both Sorting urls
                String POPULARITY_URL = "sort_by=popularity.desc";
                String VOTE_URL = "sort_by=vote_average.desc&vote_count.gte=20";

                //A String variable @SORTING_URL will contain sorting url based on preferences
                String SORTING_URL;

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String preferenceValue = preferences.getString("sort","popularity");

                Log.v(LOG_TAG, "Preference Value " +preferenceValue);

                if (preferenceValue.equals("vote"))
                    SORTING_URL = VOTE_URL;
                else
                    SORTING_URL = POPULARITY_URL;

                String API_KEY = "&api_key=" + BuildConfig.MOVIE_DB_API_KEY;
                //URL url = new URL(BASE_URL + POPULARITY_URL + API_KEY);

                URL url = new URL(BASE_URL + SORTING_URL + API_KEY);

                //URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=your_api_key");

                Log.d(LOG_TAG,"URL" +url.toString());
                // Create the request to The Movie Db, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = buffer.toString();

                //Logging the Json movie data
                Log.v(LOG_TAG, "Movie data string: " + movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error! No data from the API. Is the API key for TMDb missing? ", e);
                // If the code didn't successfully get the  data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                // End connection and close the reader.
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MovieGridFragment", "Error closing stream", e);
                    }
                }
            }

            // Parsing the JSON string
            try {
                return getMoviesDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            //return new Movie[0];
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] strings) {
            if (strings != null) {
                movieGridViewAdapter.clear();
                for (Movie moviesStr : strings) {
                    movieGridViewAdapter.add(moviesStr);
                }
            }
            movieGridViewAdapter.notifyDataSetChanged();
            super.onPostExecute(strings);
        }
    }
}

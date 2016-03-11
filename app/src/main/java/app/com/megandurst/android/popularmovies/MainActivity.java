package app.com.megandurst.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class MainActivity extends ActionBarActivity {

    private ArrayList<String> popularPosterUrlsArrayList;
    private ArrayList<Movie> popularMoviesArrayList;

    private ArrayList<String> highestRatedPosterUrlsArrayList;
    private ArrayList<Movie> highestRatedMoviesArrayList;

    private GridView gridView;
    private MoviePosterAdapter posterAdapter;

    private SharedPreferences sharedPreferences;

    private String popularSortUrl = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=REVIEWER_API_KEY_GOES_HERE";
    private String ratingsSortUrl = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=REVIEWER_API_KEY_GOES_HERE";
    private String defaultSort = popularSortUrl;
    private String sortingUrl = defaultSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            popularPosterUrlsArrayList = new ArrayList<>();
            popularMoviesArrayList = new ArrayList<>();
            highestRatedPosterUrlsArrayList = new ArrayList<>();
            highestRatedMoviesArrayList = new ArrayList<>();
        } else if (savedInstanceState.containsKey("popular_movies") || !savedInstanceState.containsKey("highest_rated_movies")) {
            popularPosterUrlsArrayList = savedInstanceState.getStringArrayList("popular_posters");
            popularMoviesArrayList = savedInstanceState.getParcelableArrayList("popular_movies");
            highestRatedPosterUrlsArrayList = new ArrayList<>();
            highestRatedMoviesArrayList = new ArrayList<>();
            sortingUrl = savedInstanceState.getString("sort");
            updateGridView();
        } else if (!savedInstanceState.containsKey("popular_movies") || savedInstanceState.containsKey("highest_rated_movies")) {
            highestRatedPosterUrlsArrayList = savedInstanceState.getStringArrayList("highest_rated_posters");
            highestRatedMoviesArrayList = savedInstanceState.getParcelableArrayList("highest_rated_movies");
            popularPosterUrlsArrayList = new ArrayList<>();
            popularMoviesArrayList = new ArrayList<>();
            sortingUrl = savedInstanceState.getString("sort");
            updateGridView();
        } else {
            popularPosterUrlsArrayList = savedInstanceState.getStringArrayList("popular_posters");
            popularMoviesArrayList = savedInstanceState.getParcelableArrayList("popular_movies");
            highestRatedPosterUrlsArrayList = savedInstanceState.getStringArrayList("highest_rated_posters");
            highestRatedMoviesArrayList = savedInstanceState.getParcelableArrayList("highest_rated_movies");
            sortingUrl = savedInstanceState.getString("sort");
            updateGridView();
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (popularMoviesArrayList != null && !popularMoviesArrayList.isEmpty()) {
            outState.putStringArrayList("popular_posters", popularPosterUrlsArrayList);
            outState.putParcelableArrayList("popular_movies", popularMoviesArrayList);
        }

        if (highestRatedMoviesArrayList != null && !highestRatedMoviesArrayList.isEmpty()) {
            outState.putStringArrayList("highest_rated_posters", highestRatedPosterUrlsArrayList);
            outState.putParcelableArrayList("highest_rated_movies", highestRatedMoviesArrayList);
        }

        outState.putString("sort", sortingUrl);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        FetchMovieData movieData = new FetchMovieData();

        String sortCheck = sharedPreferences.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_most_popular));

        if (sortCheck.equals(getString(R.string.pref_sort_highest_rated))) {
            sortingUrl = ratingsSortUrl;
        } else if (sortCheck.equals(getString(R.string.pref_sort_most_popular))) {
            sortingUrl = popularSortUrl;
        }

        if (sortingUrl.equals(popularSortUrl) && popularMoviesArrayList.size() == 0) {
            movieData.execute(sortingUrl);

        } else if (sortingUrl.equals(ratingsSortUrl)  && highestRatedMoviesArrayList.size() == 0) {
            movieData.execute(sortingUrl);
        }
    }

    private void updateGridView() {

        ArrayList posters;

        if (sortingUrl.equals(popularSortUrl)) {
            posters = popularPosterUrlsArrayList;
        } else {
            posters = highestRatedPosterUrlsArrayList;
        }


        gridView = (GridView) findViewById(R.id.posters_gridview);
        posterAdapter = new MoviePosterAdapter(MainActivity.this, posters);

        gridView.setAdapter(posterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie currentMovie;
                if (sortingUrl.equals(popularSortUrl)) {
                    currentMovie = popularMoviesArrayList.get(position);
                } else {
                    currentMovie = highestRatedMoviesArrayList.get(position);
                }

                Intent intent = new Intent("com.megandurst.android.popularmovies.MovieDetailActivity");
                intent.putExtra("movie", currentMovie);
                startActivity(intent);
            }
        });
    }

    public class FetchMovieData extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        private String[] getMovieDataFromJson(String movieJsonStr, int numMovies)
                throws JSONException {

            //JSON objects to be extracted.
            final String MDB_RESULTS = "results";
            final String MDB_TITLE = "original_title";
            final String MDB_POSTER = "poster_path";
            final String MDB_PLOT = "overview";
            final String MDB_RATING = "vote_average";
            final String MDB_RELEASE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);

            String[] resultStrs = new String[numMovies];

            for (int i = 0; i < movieArray.length(); i++) {
                String title;
                String posterPath;
                String plotSynopsis;
                String userRating;
                String releaseDate;
                String posterPathUrl;
                String movieReleaseYear = "Release Year Unknown";

                //Get the JSON object representing the movie
                JSONObject movieData = movieArray.getJSONObject(i);

                title = movieData.getString(MDB_TITLE);
                posterPath = movieData.getString(MDB_POSTER);
                plotSynopsis = movieData.getString(MDB_PLOT);
                userRating = movieData.getString(MDB_RATING);
                releaseDate = movieData.getString(MDB_RELEASE);

                if (releaseDate.length() >= 4) {
                    movieReleaseYear = releaseDate.substring(0, 4);
                }

                posterPathUrl = "http://image.tmdb.org/t/p/" + "w500" + posterPath;

                resultStrs[i] = title + "\n" + posterPathUrl + "\n" + plotSynopsis + "\n"
                        + userRating + "\n" + movieReleaseYear;

                Movie currentMovie = new Movie(title, posterPathUrl, movieReleaseYear,
                        userRating, plotSynopsis);

                if (sortingUrl.equals(popularSortUrl)) {
                    popularPosterUrlsArrayList.add(posterPathUrl);
                    popularMoviesArrayList.add(currentMovie);
                }

                if (sortingUrl.equals(ratingsSortUrl)) {
                    highestRatedPosterUrlsArrayList.add(posterPathUrl);
                    highestRatedMoviesArrayList.add(currentMovie);
                }
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {

            String urlString = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the MovieDB query
                URL url = new URL(urlString);

                // Create the request to MovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr, 40);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);

            if (sortingUrl.equals(popularSortUrl) && !popularMoviesArrayList.isEmpty()) {
                updateGridView();
            }

            if (sortingUrl.equals(ratingsSortUrl) && !highestRatedMoviesArrayList.isEmpty()) {
                updateGridView();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
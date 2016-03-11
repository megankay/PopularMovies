package app.com.megandurst.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends ActionBarActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Bundle data = getIntent().getExtras();
        Movie movie = data.getParcelable("movie");

        String title = movie.getTitle();
        String poster = movie.getPosterUrl();
        String year = movie.getYear();
        String rating = movie.getRating();
        String plot = movie.getPlot();

        TextView movieTitle = (TextView) findViewById(R.id.movie_title);
        ImageView moviePoster = (ImageView) findViewById(R.id.poster_image);
        TextView movieYear = (TextView) findViewById(R.id.movie_year);
        TextView movieRating = (TextView) findViewById(R.id.movie_rating);
        TextView moviePlot = (TextView) findViewById(R.id.movie_plot);


        movieTitle.setText(title);
        Picasso.with(context)
                .load(poster)
                .into(moviePoster);
        movieYear.setText(year);
        movieRating.setText(rating);
        moviePlot.setText(plot);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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

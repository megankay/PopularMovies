package app.com.megandurst.android.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable {

    String title;
    String posterUrl;
    String year;
    String rating;
    String plot;

    public Movie(String mTitle, String mPosterUrl, String mYear, String mRating, String mPlot) {

        this.title = mTitle;
        this.posterUrl = mPosterUrl;
        this.year = mYear;
        this.rating = mRating;
        this.plot = mPlot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public Movie(Parcel in) {
        String[] movieData = new String[5];

        in.readStringArray(movieData);
        this.title = movieData[0];
        this.posterUrl = movieData[1];
        this.year = movieData[2];
        this.rating = movieData[3];
        this.plot = movieData[4];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.title,
                this.posterUrl,
                this.year,
                this.rating,
                this.plot});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

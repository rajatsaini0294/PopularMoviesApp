package com.rajatsaini.android.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rajat on 4/2/2016.
 */
public class MovieDataPOJO implements Parcelable{
    public String movie_title;
    public double movie_rating;
    public double movie_popularity;
    public String movie_release_date;
    public String movie_overview;
    public String moview_poster_url;
    public int movie_id;
    public String movie_backdrop_url;

    public MovieDataPOJO() {

    }

    public void setMovie_backdrop_url(String movie_backdrop_url) {
        this.movie_backdrop_url = movie_backdrop_url;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public void setMovie_rating(double movie_rating) {
        this.movie_rating = movie_rating;
    }

    public void setMovie_popularity(double movie_popularity) {
        this.movie_popularity = movie_popularity;
    }

    public void setMovie_release_date(String movie_release_date) {
        this.movie_release_date = movie_release_date;
    }

    public void setMovie_overview(String movie_overview) {
        this.movie_overview = movie_overview;
    }

    public void setMoview_poster_url(String moview_poster_url) {
        this.moview_poster_url = moview_poster_url;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    private MovieDataPOJO (Parcel in) {
        movie_title = in.readString();
        movie_rating = in.readDouble();
        movie_popularity = in.readDouble();
        movie_overview= in.readString();
        movie_release_date = in.readString();
        moview_poster_url = in.readString();
        movie_id = in.readInt();
        movie_backdrop_url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movie_title);
        parcel.writeDouble(movie_rating);
        parcel.writeDouble(movie_popularity);
        parcel.writeString(movie_overview);
        parcel.writeString(movie_release_date);
        parcel.writeString(moview_poster_url);
        parcel.writeInt(movie_id);
        parcel.writeString(movie_backdrop_url);
    }

    public static final Parcelable.Creator<MovieDataPOJO> CREATOR = new Parcelable.Creator<MovieDataPOJO>() {
        @Override
        public MovieDataPOJO createFromParcel(Parcel in) {
            return new MovieDataPOJO(in);
        }

        @Override
        public MovieDataPOJO[] newArray(int size) {
            return new MovieDataPOJO[size];
        }
    };
}

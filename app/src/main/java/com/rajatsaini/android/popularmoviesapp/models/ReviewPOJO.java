package com.rajatsaini.android.popularmoviesapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rajat on 5/8/2016.
 */
public class ReviewPOJO implements Parcelable{
    public String author;
    public String content;
    public String url;

    public ReviewPOJO() {
    }

    @Override
    public int describeContents() {
        return 0;
    }
    private ReviewPOJO (Parcel in) {
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }
    public static final Parcelable.Creator<ReviewPOJO> CREATOR = new Parcelable.Creator<ReviewPOJO>() {
        @Override
        public ReviewPOJO createFromParcel(Parcel in) {
            return new ReviewPOJO(in);
        }

        @Override
        public ReviewPOJO[] newArray(int size) {
            return new ReviewPOJO[size];
        }
    };
}

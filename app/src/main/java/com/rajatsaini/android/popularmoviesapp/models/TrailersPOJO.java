package com.rajatsaini.android.popularmoviesapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rajat on 5/8/2016.
 */
public class TrailersPOJO implements Parcelable{
    public String url;
    public String name;
    public String id;

    public TrailersPOJO() {
    }

    @Override
    public int describeContents() {
        return 0;
    }
    private TrailersPOJO (Parcel in) {
        url = in.readString();
        name = in.readString();
        id = in.readString();
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeString(name);
        parcel.writeString(id);
    }
    public static final Parcelable.Creator<TrailersPOJO> CREATOR = new Parcelable.Creator<TrailersPOJO>() {
        @Override
        public TrailersPOJO createFromParcel(Parcel in) {
            return new TrailersPOJO(in);
        }

        @Override
        public TrailersPOJO[] newArray(int size) {
            return new TrailersPOJO[size];
        }
    };
}

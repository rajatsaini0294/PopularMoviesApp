package com.rajatsaini.android.popularmoviesapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rajat on 5/8/2016.
 */
public class TrailerAdapter extends BaseAdapter {
    public ArrayList<TrailersPOJO> trailers = new ArrayList<TrailersPOJO>();
    Context mContext;

    public TrailerAdapter(Context con) {
        mContext = con;
    }

    @Override
    public int getCount() {
        return trailers.size();
    }

    @Override
    public Object getItem(int i) {
        return trailers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View trailerItem;
        if (convertView == null) {
            trailerItem = View.inflate(mContext, R.layout.trailer_list_item, null);
        } else {
            trailerItem = convertView;
        }
       // TextView label = (TextView) trailerItem.findViewById(R.id.trailerName);
        //label.setText(trailers.get(i).name);

        SquareImageView image = (SquareImageView) trailerItem.findViewById(R.id.trailerImage);
        Picasso.with(mContext).load("http://img.youtube.com/vi/" + trailers.get(i).url + "/default.jpg").placeholder(R.mipmap.ic_launcher).into(image);

        final String url = trailers.get(i).url;
        trailerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDetailsFragment.instance.watchTrailer(url);
            }
        });
        return trailerItem;
    }


    public void addItem(TrailersPOJO trailer) {
        trailers.add(trailer);
    }
}

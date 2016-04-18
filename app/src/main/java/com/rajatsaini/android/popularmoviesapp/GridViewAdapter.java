package com.rajatsaini.android.popularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rajat on 3/29/2016.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    ArrayList<String> mylist;

    public GridViewAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        mylist = new ArrayList<String>();
        mylist = list;
    }

    public GridViewAdapter() {
    }

    public GridViewAdapter(ArrayList<String> list) {
        mylist = new ArrayList<String>();
        mylist = list;
    }

    @Override
    public int getCount() {
        return mylist.size();
    }

    @Override
    public Object getItem(int i) {
        return mylist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.image.setPadding(0, 0, 0, 0);
            viewHolder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewHolder.image.setAdjustViewBounds(true);
            convertView.setTag(viewHolder);
            //}
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(context).load("http://image.tmdb.org/t/p/w342/" + mylist.get(i)).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(viewHolder.image, new Callback() {
            @Override
            public void onSuccess() {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade);
                viewHolder.image.setAnimation(animation);
            }

            @Override
            public void onError() {

            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView image;
    }
}
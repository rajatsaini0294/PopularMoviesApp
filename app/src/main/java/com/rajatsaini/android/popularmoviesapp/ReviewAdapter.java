package com.rajatsaini.android.popularmoviesapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rajat on 5/8/2016.
 */
public class ReviewAdapter extends BaseAdapter{
    public ArrayList<ReviewPOJO> reviews = new ArrayList<ReviewPOJO>();
    Context mContext;

    public ReviewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int i) {
        return reviews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View reviewItem;
        if (convertView == null) {
            reviewItem = View.inflate(mContext, R.layout.review_list_item, null);
        } else {
            reviewItem = convertView;
        }

        TextView author = (TextView) reviewItem.findViewById(R.id.author);
        author.setText(reviews.get(i).author);

        TextView review = (TextView) reviewItem.findViewById(R.id.author_review);
        review.setText(reviews.get(i).content);

        return reviewItem;
    }

    public void addReview(ReviewPOJO review) {
        reviews.add(review);
    }
}

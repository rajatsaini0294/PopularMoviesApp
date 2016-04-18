package com.rajatsaini.android.popularmoviesapp;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment {
    private static final String PARAM1 = "POJO_OBJECT";
    private MovieDataPOJO movieObject;
    private String mParam2;
    public static Context context;

    public MovieDetailsFragment() {

    }

    public static MovieDetailsFragment newInstance(MovieDataPOJO pojoObject) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM1, pojoObject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieObject = getArguments().getParcelable(PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(movieObject.movie_title);

        final ImageView image = (ImageView) view.findViewById(R.id.fragment_image);
        image.setAdjustViewBounds(true);
        Picasso.with(context).load("http://image.tmdb.org/t/p/w342/" + movieObject.moview_poster_url).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(image, new Callback() {
            @Override
            public void onSuccess() {
                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade);
                image.setAnimation(animation);
            }
            @Override
            public void onError() {

            }
        });

        TextView releaseDate = (TextView) view.findViewById(R.id.releaseDate);
        releaseDate.setText(dateFormat(movieObject.movie_release_date));

        TextView rating = (TextView) view.findViewById(R.id.rating);
        rating.setText("Rating: "+ movieObject.movie_rating+"/10");

        TextView synopsis = (TextView) view.findViewById(R.id.overview);
        synopsis.setText(movieObject.movie_overview);
        return view;
    }

    public String dateFormat(String input){
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        try
        {
            date = form.parse(input);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        SimpleDateFormat postFormater = new SimpleDateFormat("MMMMM dd, yyyy");
        String newDateStr = postFormater.format(date);
        return newDateStr;
    }
}

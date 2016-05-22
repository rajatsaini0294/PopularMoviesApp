package com.rajatsaini.android.popularmoviesapp;


import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class MovieDetailsFragment extends Fragment {
    private static final String PARAM1 = "POJO_OBJECT";
    private MovieDataPOJO movieObject;
    public static Context context;
    DatabaseHelper mydbhelper;
    SQLiteDatabase sqLiteDatabase;
    RequestQueue mRequestQueue;
    public  static  MovieDetailsFragment instance;
    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    LinearLayout trailerList, reviewList;

    public MovieDetailsFragment() {
        instance = this;
    }

    public static MovieDetailsFragment newInstance(MovieDataPOJO pojoObject) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM1, pojoObject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        mRequestQueue = Volley.newRequestQueue(getActivity());
        trailerAdapter = new TrailerAdapter(getActivity());
        reviewAdapter = new ReviewAdapter(getActivity());

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
        rating.setText("Rating: " + movieObject.movie_rating + "/10");

        TextView synopsis = (TextView) view.findViewById(R.id.overview);
        synopsis.setText(movieObject.movie_overview);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        if (isFavouriteMovie(movieObject)) {
            checkBox.setChecked(true);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mydbhelper = new DatabaseHelper(getActivity().getApplicationContext());
                sqLiteDatabase = mydbhelper.getWritableDatabase();
                if (b) {
                    ContentValues values = new ContentValues();
                    values.put("id", movieObject.movie_id + "");
                    values.put("name", movieObject.movie_title);
                    values.put("releaseDate", movieObject.movie_release_date);
                    values.put("rating", movieObject.movie_rating + "");
                    values.put("isFavourite", true);
                    values.put("popularity", movieObject.movie_popularity + "");
                    values.put("synopsis", movieObject.movie_overview);
                    values.put("imagePath", movieObject.moview_poster_url);
                    values.put("backdrop", movieObject.movie_backdrop_url);
                    sqLiteDatabase.insert(Constants.TABLE_NAME, null, values);
                    Toast.makeText(getActivity().getApplicationContext(), "Added as Favourite", Toast.LENGTH_SHORT).show();
                } else {
                    String query = "DELETE FROM " + Constants.TABLE_NAME + " where id = " + movieObject.movie_id;
                    sqLiteDatabase.execSQL(query);
                    Toast.makeText(getActivity().getApplicationContext(), "Removed as Favourite", Toast.LENGTH_SHORT).show();


                }
            }
        });

        trailerList = (LinearLayout) view.findViewById(R.id.trailersList);
        getTrailers(movieObject.movie_id);

        reviewList = (LinearLayout) view.findViewById(R.id.reviewsList);
        getReviews(movieObject.movie_id);
        return view;
    }

    private void getReviews(int id) {
        String url = "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" + Constants.API_KEY;
        JsonObjectRequest req1 = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray items = response.getJSONArray("results");
                            JSONObject reviewObj;
                            for (int i=0; i<items.length(); i++){
                                reviewObj = items.getJSONObject(i);
                                ReviewPOJO review = new ReviewPOJO();
                                review.author = reviewObj.getString("author");
                                review.url = reviewObj.getString("url");
                                review.content = reviewObj.getString("content");
                                reviewAdapter.addReview(review);
                            }
                            // scroll saved
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        for (int i = 0; i < reviewAdapter.getCount(); i++){
                            reviewList.addView(reviewAdapter.getView(i, null, null));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mRequestQueue.add(req1);
    }

    private void getTrailers(int id) {
        String url = "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + Constants.API_KEY;
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray items = response.getJSONArray("results");
                            JSONObject trailerObj;
                            for (int i=0; i<items.length(); i++){
                                trailerObj = items.getJSONObject(i);
                                TrailersPOJO trailer = new TrailersPOJO();
                                trailer.id = trailerObj.getString("id");
                                trailer.url = trailerObj.getString("key");
                                trailer.name = trailerObj.getString("name");
                                trailerAdapter.addItem(trailer);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        for (int i = 0; i < trailerAdapter.getCount(); i++){
                            trailerList.addView(trailerAdapter.getView(i, null, null));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mRequestQueue.add(req);
    }


    public String dateFormat(String input) {
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        try {
            date = form.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat postFormater = new SimpleDateFormat("MMMMM dd, yyyy");
        String newDateStr = postFormater.format(date);
        return newDateStr;
    }

    public String tableQuery() {
        String query = "CREATE TABLE " + movieObject.movie_title + " ( ";
        return query;
    }

    /*
    public void onBackPressed(){
        String countQuery = "SELECT  * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = mydbhelper.getReadableDatabase();
        Cursor cursor1 = db.rawQuery(countQuery, null);
        if(cursor1.getCount()<=0){
            GridFragment.pojoList.clear();
            GridFragment.mylist.clear();

        }
        cursor1.close();
    }
    */
    public boolean isFavouriteMovie(MovieDataPOJO object) {
        String query = "SELECT id FROM " + Constants.TABLE_NAME;
        mydbhelper = new DatabaseHelper(getActivity().getApplicationContext());
        sqLiteDatabase = mydbhelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    if(cursor.getInt(0)==object.movie_id){
                        return true;
                    }
                } while (cursor.moveToNext());
            }
        }
        return false;
    }

    public void watchTrailer(String url) {
        try {
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + url));
            //startActivity(intent);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + url));

            String title = "Select a browser";

            Intent chooser = Intent.createChooser(intent, title);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(chooser);
            }
        } catch (ActivityNotFoundException ex) {

        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }
}

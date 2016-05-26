package com.rajatsaini.android.popularmoviesapp.fragments;


import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rajatsaini.android.popularmoviesapp.Constants;
import com.rajatsaini.android.popularmoviesapp.R;
import com.rajatsaini.android.popularmoviesapp.adapters.ReviewAdapter;
import com.rajatsaini.android.popularmoviesapp.adapters.TrailerAdapter;
import com.rajatsaini.android.popularmoviesapp.models.MovieDataPOJO;
import com.rajatsaini.android.popularmoviesapp.models.ReviewPOJO;
import com.rajatsaini.android.popularmoviesapp.models.TrailersPOJO;
import com.rajatsaini.android.popularmoviesapp.provider.DatabaseHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MovieDetailsFragment extends Fragment {
    View view;
    private static final String PARAM1 = "POJO_OBJECT";
    private MovieDataPOJO movieObject;
    public static Context context;
    DatabaseHelper mydbhelper;
    SQLiteDatabase sqLiteDatabase;
    RequestQueue mRequestQueue;
    public static MovieDetailsFragment instance;
    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    LinearLayout trailerList, reviewList;
    ScrollView mScrollView;
    private static final String STATE_SCROLL_VIEW = "state_scroll_view";
    int scrollId = 0, scrollOverheadId = 0;
    private ArrayList<ReviewPOJO> reviewsdata;
    private ArrayList<TrailersPOJO> trailersdata;

    private static final String EXTRA_REVIEWS_LIST = "REVIEWS_LIST";
    private static final String EXTRA_TRAILERS_LIST = "TRAILERS_LIST";
    private static final String EXTRA_CHECKBOX_STATE = "checkbox_state";
    private static final String EXTRA_RELEASE_DATE = "release_date";
    private static final String EXTRA_RATING = "rating";
    private static final String EXTRA_SYNOPSIS = "synopsis";
    private static final String EXTRA_MOVIE_TITLE = "movie_title";
    private static final String POSITION = "position";

    private int pos;

    public MovieDetailsFragment() {
        instance = this;
    }


    public static MovieDetailsFragment newInstance(MovieDataPOJO pojoObject, int position) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM1, pojoObject);
        args.putInt(POSITION, position);
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
            pos = getArguments().getInt(POSITION);
        }
        if (savedInstanceState != null) {
            // mScrollView.onRestoreInstanceState(savedInstanceState.getParcelable(STATE_SCROLL_VIEW));
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // mScrollView.onRestoreInstanceState(savedInstanceState.getParcelable(STATE_SCROLL_VIEW));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        mydbhelper = new DatabaseHelper(getActivity().getApplicationContext());
        sqLiteDatabase = mydbhelper.getWritableDatabase();
        reviewsdata = new ArrayList<ReviewPOJO>();
        trailersdata = new ArrayList<TrailersPOJO>();

        TextView title = (TextView) view.findViewById(R.id.title);
        mScrollView = (ScrollView) view.findViewById(R.id.mScrollView);

        mRequestQueue = Volley.newRequestQueue(getActivity());
        trailerAdapter = new TrailerAdapter(getActivity());
        reviewAdapter = new ReviewAdapter(getActivity());

        final ImageView image = (ImageView) view.findViewById(R.id.fragment_image);
        image.setAdjustViewBounds(true);
        Picasso.with(context).load("http://image.tmdb.org/t/p/w342/" + movieObject.moview_poster_url).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(image, new Callback() {
            @Override
            public void onSuccess() {
                if (isAdded()) {
                    Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade);
                    image.setAnimation(animation);
                }
            }

            @Override
            public void onError() {

            }
        });

        TextView releaseDate = (TextView) view.findViewById(R.id.releaseDate);

        TextView rating = (TextView) view.findViewById(R.id.rating);

        TextView synopsis = (TextView) view.findViewById(R.id.overview);

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (checkBox.isShown()) { // because checkbox is set checked true later than associating the changelistener.
                    // this causes the change listener to trigger always if checkbox state is checked, isShown() prevent this
                    new scheduleRoutines().execute(b);
                }

            }
        });

        trailerList = (LinearLayout) view.findViewById(R.id.trailersList);
        reviewList = (LinearLayout) view.findViewById(R.id.reviewsList);

        if (savedInstanceState != null) {

            ArrayList<ReviewPOJO> list = new ArrayList<ReviewPOJO>();
            reviewsdata = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS_LIST);
            reviewAdapter.addAllReview(reviewsdata);

            ArrayList<TrailersPOJO> listr = new ArrayList<TrailersPOJO>();
            trailersdata = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS_LIST);
            trailerAdapter.addAllTrailer(trailersdata);

            //Toast.makeText(getActivity().getApplicationContext(), reviewAdapter.getCount()+"..Review adapter count", Toast.LENGTH_SHORT).show();

            for (int i = 0; i < reviewAdapter.getCount(); i++) {
                reviewList.addView(reviewAdapter.getView(i, null, null));
            }

            for (int i = 0; i < trailerAdapter.getCount(); i++) {
                trailerList.addView(trailerAdapter.getView(i, null, null));
            }
            //Toast.makeText(getActivity(), trailerList.getChildCount()+"...count of trailers", Toast.LENGTH_SHORT).show();
            //Toast.makeText(getActivity().getApplicationContext(), trailerAdapter.getCount()+"..trailer adapter count", Toast.LENGTH_SHORT).show();

            title.setText(savedInstanceState.getString(EXTRA_MOVIE_TITLE));
            releaseDate.setText(savedInstanceState.getString(EXTRA_RELEASE_DATE));
            rating.setText(savedInstanceState.getString(EXTRA_RATING));
            synopsis.setText(savedInstanceState.getString(EXTRA_SYNOPSIS));
            checkBox.setChecked(savedInstanceState.getBoolean(EXTRA_CHECKBOX_STATE));

        } else if (savedInstanceState == null) {

            title.setText(movieObject.movie_title);
            releaseDate.setText(dateFormat(movieObject.movie_release_date));
            rating.setText("Rating: " + movieObject.movie_rating + "/10");
            synopsis.setText(movieObject.movie_overview);

            if (isFavouriteMovie(movieObject)) {
                checkBox.setChecked(true);
            }
            getTrailers(movieObject.movie_id);
            getReviews(movieObject.movie_id);
        }

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
                            for (int i = 0; i < items.length(); i++) {
                                reviewObj = items.getJSONObject(i);
                                ReviewPOJO review = new ReviewPOJO();
                                review.author = reviewObj.getString("author");
                                review.url = reviewObj.getString("url");
                                review.content = reviewObj.getString("content");
                                reviewsdata.add(review);
                                reviewAdapter.addReview(review);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < reviewAdapter.getCount(); i++) {
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
                            for (int i = 0; i < items.length(); i++) {
                                trailerObj = items.getJSONObject(i);
                                TrailersPOJO trailer = new TrailersPOJO();
                                trailer.id = trailerObj.getString("id");
                                trailer.url = trailerObj.getString("key");
                                trailer.name = trailerObj.getString("name");
                                trailersdata.add(trailer);
                                trailerAdapter.addItem(trailer);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < trailerAdapter.getCount(); i++) {
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
                    if (cursor.getInt(0) == object.movie_id) {
                        return true;
                    }
                } while (cursor.moveToNext());
            }
        }
        return false;
    }

    public void watchTrailer(String url) {
        try {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // outState.putParcelable(STATE_SCROLL_VIEW, mScrollView.onSaveInstanceState());
        outState.putParcelableArrayList(EXTRA_REVIEWS_LIST, reviewsdata);
        outState.putParcelableArrayList(EXTRA_TRAILERS_LIST, trailersdata);
        outState.putBoolean(EXTRA_CHECKBOX_STATE, ((CheckBox) view.findViewById(R.id.checkbox)).isChecked());
        outState.putString(EXTRA_MOVIE_TITLE, movieObject.movie_title);
        outState.putString(EXTRA_RELEASE_DATE, dateFormat(movieObject.movie_release_date));
        outState.putString(EXTRA_RATING, "Rating: " + movieObject.movie_rating + "/10");
        outState.putString(EXTRA_SYNOPSIS, movieObject.movie_overview);
    }

    private class scheduleRoutines extends AsyncTask<Boolean, Void, Void> {
        Boolean checkBoxstate;

        @Override
        protected Void doInBackground(Boolean... booleans) {
            checkBoxstate = booleans[0];
            if (checkBoxstate) {
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

            } else {
                String query = "DELETE FROM " + Constants.TABLE_NAME + " where id = " + movieObject.movie_id;
                sqLiteDatabase.execSQL(query);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (checkBoxstate) {
                Toast.makeText(getActivity().getApplicationContext(), "Added as Favourite", Toast.LENGTH_SHORT).show();
            } else {
                boolean dualPane = getResources().getBoolean(R.bool.isTablet);
                if (GridFragment.sortOrder.equals("fav") && dualPane) {
                    GridFragment.updateUIOnTablets();
                    Toast.makeText(getActivity().getApplicationContext(), "Removed as Favourite", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }
}

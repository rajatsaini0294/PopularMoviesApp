package com.rajatsaini.android.popularmoviesapp;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public static Context context;
    DatabaseHelper mydbhelper;
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
        rating.setText("Rating: " + movieObject.movie_rating + "/10");

        TextView synopsis = (TextView) view.findViewById(R.id.overview);
        synopsis.setText(movieObject.movie_overview);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        if(GridFragment.sortOrder.equals("fav")){
            checkBox.setChecked(true);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mydbhelper = new DatabaseHelper(getActivity().getApplicationContext());
                SQLiteDatabase sqLiteDatabase = mydbhelper.getWritableDatabase();
                if (b){
                    ContentValues values = new ContentValues();
                    values.put("id", movieObject.movie_id+"");
                    values.put("name", movieObject.movie_title);
                    values.put("releaseDate", movieObject.movie_release_date);
                    values.put("rating", movieObject.movie_rating+"");
                    values.put("isFavourite", true);
                    values.put("popularity", movieObject.movie_popularity+"");
                    values.put("synopsis", movieObject.movie_overview);
                    values.put("imagePath", movieObject.moview_poster_url);
                    values.put("backdrop", movieObject.movie_backdrop_url);
                    sqLiteDatabase.insert(Constants.TABLE_NAME, null, values);
                    Toast.makeText(getActivity().getApplicationContext(), "Added as Favourite",Toast.LENGTH_SHORT).show();
                }else{
                    String query  = "DELETE FROM "+Constants.TABLE_NAME+" where id = "+movieObject.movie_id;
                    sqLiteDatabase.execSQL(query);
                    Toast.makeText(getActivity().getApplicationContext(), "Removed as Favourite",Toast.LENGTH_SHORT).show();


                }
            }
        });

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

    public String tableQuery(){
        String query = "CREATE TABLE "+ movieObject.movie_title+" ( ";
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
}

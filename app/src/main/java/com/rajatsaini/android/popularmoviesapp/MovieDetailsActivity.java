package com.rajatsaini.android.popularmoviesapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {
    public static MovieDataPOJO pojo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_nav);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        pojo = getIntent().getParcelableExtra("POJO_OBJECT");
        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle(pojo.movie_title);
        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setCollapsedTitleTextColor(Color.WHITE);
        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setExpandedTitleColor(Color.WHITE);

        final ImageView backdrop = (ImageView) findViewById(R.id.backdrop);

        Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w500/" + pojo.movie_backdrop_url).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(backdrop, new Callback() {
            @Override
            public void onSuccess() {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                backdrop.setAnimation(animation);
            }

            @Override
            public void onError() {

            }
        });

        Fragment frag = MovieDetailsFragment.newInstance(pojo);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction t = manager.beginTransaction();
        t.replace(R.id.detail_container, frag);
        t.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(GridFragment.sortOrder.equals("fav")) {
            GridFragment.invalidateData();
            fetchFavouriteData();
            GridFragment.adapter.notifyDataSetChanged();
        }
    }

    private void fetchFavouriteData() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM "+ Constants.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    MovieDataPOJO movieDataPOJO = new MovieDataPOJO();
                    movieDataPOJO.setMovie_id(cursor.getInt(0));
                    movieDataPOJO.setMovie_title(cursor.getString(1));
                    movieDataPOJO.setMovie_release_date(cursor.getString(2));
                    movieDataPOJO.setMovie_rating(cursor.getDouble(3));
                    movieDataPOJO.setMovie_popularity(cursor.getDouble(4));
                    movieDataPOJO.setMovie_overview(cursor.getString(6));
                    movieDataPOJO.setMoview_poster_url(cursor.getString(7));
                    movieDataPOJO.setMovie_backdrop_url(cursor.getString(8));

                    GridFragment.pojoList.add(movieDataPOJO);
                    GridFragment.mylist.add("http://image.tmdb.org/t/p/w342/" + cursor.getString(7));
                }
                while (cursor.moveToNext());
            }
        }
        cursor.close();
    }
}

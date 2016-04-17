package com.rajatsaini.android.popularmoviesapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MovieDetailsActivity extends AppCompatActivity {
    public static MovieDataPOJO pojo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pojo = getIntent().getParcelableExtra("POJO_OBJECT");
        getSupportActionBar().setTitle(pojo.movie_title);

        Fragment frag = MovieDetailsFragment.newInstance(pojo);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction t = manager.beginTransaction();
        t.replace(R.id.detail_container, frag);
        t.commit();
    }

}

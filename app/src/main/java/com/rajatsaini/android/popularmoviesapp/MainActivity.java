package com.rajatsaini.android.popularmoviesapp;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem rating = menu.findItem(R.id.action_sort_by_rating);

        if ((GridFragment.sortOrder).contentEquals("popularity.desc")) {
            if (!popularity.isChecked()) {
                popularity.setChecked(true);
            }
        } else if ((GridFragment.sortOrder).contentEquals("vote_average.desc")) {
            if (!rating.isChecked()) {
                rating.setChecked(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        GridFragment frag = GridFragment.context;

        if (id == R.id.action_sort_by_popularity) {
            item.setChecked(true);
            GridFragment.sortOrder = "popularity.desc";
            frag.GridViewInterface();
        } else if (id == R.id.action_sort_by_rating) {
            item.setChecked(true);
            GridFragment.sortOrder = "vote_average.desc";
            GridFragment.params = "vote_count.gte=50&include_video=false";
            frag.GridViewInterface();
        } else if (id == R.id.action_my_favourites) {
            item.setChecked(true);
            GridFragment.sortOrder = "fav";
            GridFragment.params = "";
            frag.GridViewInterface();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkDatabase() {
        DatabaseHelper myDbHelper = new DatabaseHelper(getApplicationContext());
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        myDbHelper.close();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}

package com.rajatsaini.android.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        }
        return super.onOptionsItemSelected(item);
    }

}

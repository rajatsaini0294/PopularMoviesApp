package com.rajatsaini.android.popularmoviesapp;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rajat on 3/28/2016.
 */
public class GridFragment extends Fragment {
    public static ArrayList<String> mylist = new ArrayList<String>();
    public ArrayList<MovieDataPOJO> pojoList;// = new ArrayList<MovieDataPOJO>();
    private RequestQueue requestQueue;
    View view;
    GridViewAdapter adapter;
    GridView gridView;
    public static String sortOrder = "popularity.desc";
    public static String params = "";
    public static GridFragment context;

    public GridFragment() {
        context = this;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        gridView = (GridView) inflater.inflate(R.layout.fragment_main, container, false);
        pojoList = new ArrayList<MovieDataPOJO>();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        adapter = new GridViewAdapter(getActivity().getApplicationContext(), mylist);
        gridView.setAdapter(adapter);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setNumColumns(3);
            invalidateData();
        } else {
            gridView.setNumColumns(2);
            invalidateData();

        }
        fetchData(sortOrder, params);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("POJO_OBJECT", pojoList.get(i));
                startActivity(intent);
            }
        });
        return gridView;
    }

    private void invalidateData() {
        pojoList.clear();
        mylist.clear();
    }

    public void fetchData(String sortOrder, String params) {
        String url = "http://api.themoviedb.org/3/discover/movie?sort_by=" + sortOrder + "&" + params + "&api_key=" + Constants.API_KEY;
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray dataItems = response.getJSONArray("results");
                    JSONObject jsonObject;
                    for (int i = 0; i < dataItems.length(); i++) {
                        jsonObject = dataItems.getJSONObject(i);
                        MovieDataPOJO movieData = new MovieDataPOJO();

                        movieData.setMovie_id(jsonObject.getInt("id"));
                        movieData.setMovie_title(jsonObject.getString("original_title"));
                        movieData.setMovie_overview(jsonObject.getString("overview"));
                        movieData.setMoview_poster_url(jsonObject.getString("poster_path"));
                        movieData.setMovie_rating(jsonObject.getDouble("vote_average"));
                        movieData.setMovie_release_date(jsonObject.getString("release_date"));
                        movieData.setMovie_popularity(jsonObject.getDouble("popularity"));
                        pojoList.add(movieData);

                        mylist.add("http://image.tmdb.org/t/p/w342/" + jsonObject.getString("poster_path"));
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                    Toast.makeText(getActivity(), "JSON ERROR", Toast.LENGTH_SHORT).show();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gridView.setAdapter(adapter);
                    }
                });
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", "JSON ERROR");
            }
        };
        JsonObjectRequest req = new JsonObjectRequest(url, null, listener, errorListener);
        requestQueue.add(req);
    }

    public static Fragment newInstance() {
        GridFragment myFragment = new GridFragment();
        return myFragment;
    }

    public void GridViewInterface() {
        pojoList.clear();
        mylist.clear();
        fetchData(sortOrder, params);
        adapter.notifyDataSetChanged();
    }
}

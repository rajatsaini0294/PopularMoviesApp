package com.rajatsaini.android.popularmoviesapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
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
    public static ArrayList<MovieDataPOJO> pojoList;
    private RequestQueue requestQueue;
    public static GridViewAdapter adapter;
    GridView gridView;
    public static String sortOrder = "popularity.desc";
    public static String params = "";
    public static GridFragment context;
    public boolean isDualPane = false;
    public int gridPos = -1;

    public GridFragment() {
        context = this;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isDualPane = getPaneLayout();
    }

    private boolean getPaneLayout() {
        boolean isdualPAne = getActivity().findViewById(R.id.detailContainer) != null;
        return isdualPAne;
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
        scheduleRoutine1();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isDualPane) {
                    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                    MovieDetailsFragment frag = MovieDetailsFragment.newInstance(pojoList.get(i));
                    ft.replace(R.id.detailContainer, frag);
                    ft.commit();
                } else {
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
                    String countQuery = "SELECT  * FROM " + Constants.TABLE_NAME;
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor cursor = db.rawQuery(countQuery, null);
                    int cnt = cursor.getCount();
                    cursor.close();
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("POJO_OBJECT", pojoList.get(i));
                    startActivity(intent);
                }
            }
        });
        return gridView;
    }

    public void scheduleRoutine1() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDualPane) {
                    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                    MovieDetailsFragment frag = MovieDetailsFragment.newInstance(pojoList.get(0));
                    ft.replace(R.id.detailContainer, frag);
                    ft.commit();
                }
            }
        }, 500);
    }
    public static void invalidateData() {
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
                        movieData.setMovie_backdrop_url(jsonObject.getString("backdrop_path"));
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
                        if (gridPos > -1)
                            gridView.setSelection(gridPos);
                        gridPos = -1;
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
        if (!sortOrder.equals("fav")) {
            fetchData(sortOrder, params);
        } else {
            fetchFavouriteData();
        }
        adapter.notifyDataSetChanged();
    }

    private void fetchFavouriteData() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Constants.TABLE_NAME;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
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

                    pojoList.add(movieDataPOJO);
                    mylist.add("http://image.tmdb.org/t/p/w342/" + cursor.getString(7));
                }
                while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("GRIDVIEW_POSITION", gridView.getFirstVisiblePosition());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            gridPos = savedInstanceState.getInt("GRIDVIEW_POSITION");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }
}

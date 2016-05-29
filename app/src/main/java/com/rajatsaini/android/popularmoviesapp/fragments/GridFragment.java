package com.rajatsaini.android.popularmoviesapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
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
import com.rajatsaini.android.popularmoviesapp.Constants;
import com.rajatsaini.android.popularmoviesapp.R;
import com.rajatsaini.android.popularmoviesapp.Utils.NetworkUtil;
import com.rajatsaini.android.popularmoviesapp.activities.MovieDetailsActivity;
import com.rajatsaini.android.popularmoviesapp.adapters.GridViewAdapter;
import com.rajatsaini.android.popularmoviesapp.models.MovieDataPOJO;
import com.rajatsaini.android.popularmoviesapp.provider.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rajat on 3/28/2016.
 */
public class GridFragment extends Fragment {
    public static ArrayList<String> mylist;
    public static ArrayList<MovieDataPOJO> pojoList;
    private RequestQueue requestQueue;
    public static GridViewAdapter adapter;
    GridView gridView;
    public static String sortOrder = "popular";
    public static String params = "";
    public static GridFragment context;
    public boolean isDualPane = false;
    public int gridPos = -1;

    public static final String EXTRA_MOVIE_POSTERS = "MOVIE_POSTER_URL";
    public static final String EXTRA_MOVIE_OBJECTS = "MOVIE_OBJECTS";
    public static final String EXTRA_GRIDVIEW_POSITION = "GRID_MOVIE_POSITION";

    public GridFragment() {
        context = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            gridPos = savedInstanceState.getInt("GRIDVIEW_POSITION");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isDualPane = getPaneLayout();
    }

    private boolean getPaneLayout() {
        boolean isdualPAne = getActivity().findViewById(R.id.detail_Container_tablet) != null;
        return isdualPAne;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        gridView = (GridView) inflater.inflate(R.layout.fragment_main, container, false);
        pojoList = new ArrayList<MovieDataPOJO>();
        mylist = new ArrayList<String>();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setNumColumns(3);
            invalidateData();
        } else {
            gridView.setNumColumns(2);
            invalidateData();

        }
        if (savedInstanceState != null) {
            mylist.clear();
            pojoList.clear();
            mylist = savedInstanceState.getStringArrayList(EXTRA_MOVIE_POSTERS);
            pojoList = savedInstanceState.getParcelableArrayList(EXTRA_MOVIE_OBJECTS);
        } else {
            if(NetworkUtil.getConnectivityStatus(getActivity())!=NetworkUtil.TYPE_NOT_CONNECTED) {
                fetchData(sortOrder, params);
            }else {
                Toast.makeText(getActivity(), "Not Connected to Internet", Toast.LENGTH_SHORT).show();
            }
        }

        adapter = new GridViewAdapter(getActivity().getApplicationContext(), mylist);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isDualPane) {
                    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                    MovieDetailsFragment frag = MovieDetailsFragment.newInstance(pojoList.get(i), i);
                    ft.replace(R.id.detail_Container_tablet, frag);
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
                    intent.putExtra("POJO_POSITION", i);
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
                    MovieDetailsFragment frag = MovieDetailsFragment.newInstance(pojoList.get(0), 0);
                    ft.replace(R.id.detail_Container_tablet, frag);
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
        String url = "http://api.themoviedb.org/3/movie/" + sortOrder + "?api_key=" + Constants.API_KEY;
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
                        if (pojoList.size() > 0) {
                            scheduleRoutine1();
                        }
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gridView.setAdapter(adapter);
                    if (gridPos > -1)
                        gridView.setSelection(gridPos);
                    gridPos = -1;
                    if (pojoList.size() > 0) {
                        scheduleRoutine1();
                    }
                    if(pojoList.size() <=0 && getResources().getBoolean(R.bool.isTablet)){
                        clearDetailsFragment();
                    }
                }
            });
        }

        adapter.notifyDataSetChanged();
    }

    private void clearDetailsFragment() {
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        EmptyFragment frag = EmptyFragment.newInstance();
        ft.replace(R.id.detail_Container_tablet, frag);
        ft.commit();
    }

    public static void updateUIOnTablets(){
        context.GridViewInterface();
    }
    public void fetchFavouriteData() {
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
        outState.putInt(EXTRA_GRIDVIEW_POSITION, gridView.getFirstVisiblePosition());
        outState.putStringArrayList(EXTRA_MOVIE_POSTERS, mylist);
        outState.putParcelableArrayList(EXTRA_MOVIE_OBJECTS, pojoList);
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
    public void refreshData(){
        if(!sortOrder.equals("fav")) {
            gridView.setAdapter(null);
            invalidateData();
            fetchData(sortOrder, params);
        }else{
            GridViewInterface();
        }
    }
}

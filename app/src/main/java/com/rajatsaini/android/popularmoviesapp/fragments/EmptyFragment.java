package com.rajatsaini.android.popularmoviesapp.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rajatsaini.android.popularmoviesapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyFragment extends Fragment {


    public EmptyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

    public static EmptyFragment newInstance() {
        EmptyFragment frag = new EmptyFragment();
        return frag;
    }
}

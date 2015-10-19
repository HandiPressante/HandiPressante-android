package com.handipressante.handipressante;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Nico on 19/10/2015.
 */
public class MapFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                getString(R.string.demo_map));
        return rootView;
    }
}

package com.handipressante.handipressante;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;


/**
 * Created by Nico on 19/10/2015.
 */
public class ListFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        System.out.println("A");
        final ListView listview = (ListView) rootView.findViewById(R.id.mobile_list);

        String[] toilets = {"WC1", "WC2", "WC3", "WC4", "WC5", "WC6", "WC6", "WC7", "WC8", "WC9", "WC10"};
        System.out.println("B");
        final ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < toilets.length; ++i) {
            System.out.println(i);
            list.add(toilets[i]);
        }
        System.out.println("C");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1 , list);
        System.out.println("D");
        listview.setAdapter(adapter);
        System.out.println("D");


        return rootView;
    }
}

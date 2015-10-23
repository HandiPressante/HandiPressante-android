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
 * Created by Tom on 21/10/2015.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class ListToiletsFragment extends ListFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        // List of toilets to be displayed on the list :
        Toilet[] listOfToilets = new Toilet[5];
        listOfToilets[0] = new Toilet(0, true, "1 rue de la Gare", new GPSCoordinates(5, 4), 3);
        listOfToilets[1] = new Toilet(1, false, "14 rue de l'Eglise", new GPSCoordinates(5, 4), 1);
        listOfToilets[2] = new Toilet(2, true, "7 rue de la Maire", new GPSCoordinates(5, 4), 5);
        listOfToilets[3] = new Toilet(3, false, "2 rue du Parc", new GPSCoordinates(5, 4), 2);
        listOfToilets[4] = new Toilet(4, false, "34 rue de la République", new GPSCoordinates(5, 4),4);

        // GPSCoordinate of user
        GPSCoordinates myPlace = new GPSCoordinates(10, 15);


        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<listOfToilets.length;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", listOfToilets[i].getAddress());
            hm.put("rank",Integer.toString(listOfToilets[i].getRankIcon()));
            hm.put("icon_pmr", Integer.toString(listOfToilets[i].getIcon()));
            hm.put("dist", "10Km");
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = { "icon_pmr","txt", "dist" ,"rank"};

        // Ids of views in listview_layout
        int[] to = { R.id.flag,R.id.txt,R.id.dist,R.id.rank};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.fragment_list, from, to);

        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}


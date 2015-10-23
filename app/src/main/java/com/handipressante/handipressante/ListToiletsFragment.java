package com.handipressante.handipressante;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
        Toilet[] listOfToilets = new Toilet[10];
        listOfToilets[0] = new Toilet(0, false, "Mairie Opéra", new GPSCoordinates(5, 4), 3);
        listOfToilets[1] = new Toilet(1, false, "Halles Centrales", new GPSCoordinates(5, 4), 1);
        listOfToilets[2] = new Toilet(2, true, "Champs Libres", new GPSCoordinates(5, 4), 5);
        listOfToilets[3] = new Toilet(3, true, "Fréville", new GPSCoordinates(5, 4), 2);
        listOfToilets[4] = new Toilet(4, false, "Kléber 2", new GPSCoordinates(5, 4),4);
        listOfToilets[5] = new Toilet(5, true, "Gare", new GPSCoordinates(5, 4), 3);
        listOfToilets[6] = new Toilet(6, false, "Kléber Parking", new GPSCoordinates(5, 4), 1);
        listOfToilets[7] = new Toilet(7, false, "Guy HOUIST", new GPSCoordinates(5, 4), 5);
        listOfToilets[8] = new Toilet(8, true, "St Cyr", new GPSCoordinates(5, 4), 2);
        listOfToilets[9] = new Toilet(9, false, "Cimetière Nord", new GPSCoordinates(5, 4),4);

        // GPSCoordinate of user
        GPSCoordinates myPlace = new GPSCoordinates(10, 15);


        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<listOfToilets.length;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", listOfToilets[i].getAddress());
            hm.put("rank",Integer.toString(listOfToilets[i].getRankIcon()));
            hm.put("icon_pmr", Integer.toString(listOfToilets[i].getIcon()));
            hm.put("dist", listOfToilets[i].getDistanceToString(new GPSCoordinates()));
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

        new DownloadToiletsTask().execute(new GPSCoordinates(351861.03, 6789173.05));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadToiletsTask extends AsyncTask<GPSCoordinates, Void, List<Toilet>> {
        @Override
        protected List<Toilet> doInBackground(GPSCoordinates... params) {
            IDataModel model = new OnlineDataModel(getContext());
            return model.getToilets(params[0], 400, 400);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Toilet> result) {

            for (Toilet t : result) {
                Log.d("Debug", "########################");
                Log.d("Debug", "Toilette " + t.getId());
                Log.d("Debug", "Adresse : " + t.getAddress());
                Log.d("Debug", "PMR : " + t.isAdapted());
                Log.d("Debug", "X 93 : " + t.getCoordinates().getL93X());
                Log.d("Debug", "Y 93 : " + t.getCoordinates().getL93Y());
                Log.d("Debug", "########################");
            }
            //textView.setText(result);
        }


    }
}


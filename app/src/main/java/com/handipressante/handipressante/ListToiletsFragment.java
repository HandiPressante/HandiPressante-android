package com.handipressante.handipressante;


/**
 * Created by Tom on 21/10/2015.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
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

    private List<Toilet> listOfToilets = new ArrayList<Toilet>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        // GPSCoordinate of user ###### NEEDS TO BE DYNAMIC ########
        GPSCoordinates myPlace = new GPSCoordinates(351861.03, 6789173.05);

        // Download toilets
        new DownloadToiletsTask().execute(myPlace);

        // Default toilet in case of error
        listOfToilets.add(0, new Toilet(123456, false, "Pas de toilette", new GPSCoordinates(1,1),2345));

        // Generation of view
        generateView();

        // Add ClickListener
        final ListView toiletList = this.getListView();
        toiletList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Show new activity when clicked
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.e("Position touched", String.valueOf(position));
                Log.e("id touched", String.valueOf(id));
                Toilet t = (Toilet) listOfToilets.get(position);
                Log.e("Toilet touched", t.getAddress());
                Integer idSheet = t.getId();
                if (idSheet != -1) // If it's not the default toilet
                {
                    Intent intent = new Intent(getActivity(), ToiletSheetActivity.class);
                    Bundle b = new Bundle();
                    Log.e("Id send to the sheet", String.valueOf(idSheet));
                    b.putInt("idSheet", idSheet);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }



    public void generateView(){
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
        for(int i=0;i<this.listOfToilets.size();i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", this.listOfToilets.get(i).getAddress());
            hm.put("rank",Integer.toString(this.listOfToilets.get(i).getRankIcon()));
            hm.put("icon_pmr", Integer.toString(this.listOfToilets.get(i).getIcon()));
            hm.put("dist", this.listOfToilets.get(i).getDistanceToString(new GPSCoordinates()));
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

            listOfToilets = result;
            generateView();
            //textView.setText(result);
        }


    }
}


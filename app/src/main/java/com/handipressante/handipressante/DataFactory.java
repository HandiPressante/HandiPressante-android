package com.handipressante.handipressante;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 23/10/2015.
 */
public class DataFactory {

    public List<Toilet> createToilets(InputStream in) throws IOException {
        List<Toilet> res = new ArrayList<Toilet>();

        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();

            int t_id = 9999;
            String t_name = "";
            String t_address = "";
            boolean t_adapted = false;
            double t_x93 = 0;
            double t_y93 = 0;

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id_toilettes")) {
                    t_id = reader.nextInt();
                } else if (name.equals("nom")) {
                    t_name = reader.nextString();
                } else if (name.equals("lieu")) {
                    t_address = reader.nextString();
                } else if (name.equals("pmr")) {
                    t_adapted = reader.nextString().equals("1");
                } else if (name.equals("x93")) {
                    t_x93 = reader.nextDouble();
                    Log.d("Debug", "t_x93 = " + t_x93);
                } else if (name.equals("y93")) {
                    t_y93 = reader.nextDouble();
                    Log.d("Debug", "t_y93 = " + t_y93);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

            Toilet t = new Toilet(t_id, t_adapted, t_address, new GPSCoordinates(t_x93, t_y93), 0);
            res.add(t);
        }
        reader.endArray();


        return res;
    }


    public Sheet createSheet(InputStream in) throws IOException {
        Sheet res = new Sheet();

        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                res._id = reader.nextInt();
            } else if (name.equals("nom")) {
                res._name = reader.nextString();
            } else if (name.equals("description")) {
                res._description = reader.nextString();
            } else if (name.equals("lieu")) {
                reader.skipValue();
            } else if (name.equals("horaire")) {
                reader.skipValue();
            } else if (name.equals("pmr")) {
                res._isAdapted = reader.nextBoolean();
            } else if (name.equals("moyenne_proprete")) {
                res._rankCleanliness = reader.nextInt();
            } else if (name.equals("moyenne_equipement")) {
                res._rankFacilities = reader.nextInt();
            } else if (name.equals("moyenne_accessibilite")) {
                res._rankAccessibility = reader.nextInt();
            }else {
                reader.skipValue();
            }

            // Generating general rank
            res._rankGeneral = ((res.get_rankAccessibility() + res.get_rankCleanliness() + res.get_rankFacilities())/3);
        }

        return res;
    }
}

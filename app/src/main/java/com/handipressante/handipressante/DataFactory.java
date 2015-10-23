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
    List<Toilet> createToilets(InputStream in) throws IOException {
        List<Toilet> res = new ArrayList<Toilet>();

        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();

            int t_id = 0;
            String t_name = "";
            String t_address = "";
            boolean t_adapted = false;
            double t_x93 = 0;
            double t_y93 = 0;

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id")) {
                    t_id = reader.nextInt();
                } else if (name.equals("nom")) {
                    t_name = reader.nextString();
                } else if (name.equals("lieu")) {
                    t_address = reader.nextString();
                } else if (name.equals("pmr")) {
                    t_adapted = reader.nextString().equals("1");
                } else if (name.equals("x93")) {
                    t_x93 = reader.nextDouble();
                } else if (name.equals("y93")) {
                    t_y93 = reader.nextDouble();
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
}

package com.handipressante.handipressante;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

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
            double t_lat = 0;
            double t_long = 0;

            double t_rankCleanliness = 0;
            double t_rankFacilities = 0;
            double t_rankAccessibility = 0;
            double t_rankAverage = 0;

            double t_distance = 0;

            while (reader.hasNext()) {
                String name = reader.nextName();

                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                } else if (name.equals("ids")) {
                    t_id = reader.nextInt();
                } else if (name.equals("nom")) {
                    t_name = reader.nextString();
                } else if (name.equals("lieu")) {
                    t_address = reader.nextString();
                } else if (name.equals("pmr")) {
                    t_adapted = reader.nextString().equals("1");
                } else if (name.equals("lat64")) {
                    t_lat = reader.nextDouble();
                } else if (name.equals("long64")) {
                    t_long = reader.nextDouble();
                } else if (name.equals("distance")) {
                    t_distance = reader.nextDouble();
                } else if (name.equals("moyenne_proprete")) {
                    t_rankCleanliness = reader.nextDouble();
                } else if (name.equals("moyenne_equipement")) {
                    t_rankFacilities = reader.nextDouble();
                } else if (name.equals("moyenne_accessibilite")) {
                    t_rankAccessibility = reader.nextDouble();
                } else if (name.equals("moyenne")) {
                    t_rankAverage = reader.nextDouble();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

            Toilet t = new Toilet(t_id, t_adapted, t_address, new GeoPoint(t_lat, t_long), t_distance);
            t.setRankCleanliness((int) Math.round(t_rankCleanliness));
            t.setRankFacilities((int) Math.round(t_rankFacilities));
            t.setRankAccessibility((int) Math.round(t_rankAccessibility));
            t.setRankAverage((int) Math.round(t_rankAverage));

            res.add(t);
        }
        reader.endArray();


        return res;
    }


    public Sheet createSheet(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        reader.beginObject();
        int i = 0;
        int t_id = 9999;
        String t_name = "";
        String t_description = "";
        boolean t_adapted = false;
        double t_rankCleanliness = 0;
        double t_rankFacilities = 0;
        double t_rankAccessibility = 0;
        double t_rankAverage = 0;

        while (reader.hasNext()) {
            System.out.println("loop " + i++);
            String name = reader.nextName();
            System.out.println("Key : " + name);


            if (reader.peek() == JsonToken.NULL) {
                reader.skipValue();
            } else if (name.equals("id")) {
                t_id = reader.nextInt();
            } else if (name.equals("nom")) {
                t_name = reader.nextString();
            } else if (name.equals("description")) {
                t_description = reader.nextString();
            } else if (name.equals("lieu")) {
                reader.skipValue();
            } else if (name.equals("horaire")) {
                reader.skipValue();
            } else if (name.equals("pmr")) {
                t_adapted = reader.nextBoolean();
            } else if (name.equals("moyenne_proprete")) {
                t_rankCleanliness = reader.nextDouble();
            } else if (name.equals("moyenne_equipement")) {
                t_rankFacilities = reader.nextDouble();
            } else if (name.equals("moyenne_accessibilite")) {
                t_rankAccessibility = reader.nextDouble();
            }else {
                reader.skipValue();
            }
        }
        reader.endObject();

        // Generating general rank
        t_rankAverage = ((t_rankAccessibility + t_rankCleanliness + t_rankFacilities)/3);
        Sheet res = new Sheet(t_id, t_name, t_description, (int)Math.round(t_rankAverage), (int)Math.round(t_rankCleanliness), (int)Math.round(t_rankFacilities), (int)Math.round(t_rankAccessibility), t_adapted);

        return res;
    }
}

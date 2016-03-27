package fr.handipressante.app;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import fr.handipressante.app.Data.Memo;

/**
 * Created by Nico on 23/10/2015.
 */
public class DataFactory {
    public Toilet createToilet(JSONObject jsonObject) throws JSONException {
        int t_id = jsonObject.getInt("id");
        String t_address = jsonObject.getString("lieu");
        boolean t_adapted = jsonObject.optInt("pmr", 0) == 1;
        double t_lat = jsonObject.getDouble("lat84");
        double t_long = jsonObject.getDouble("long84");

        String t_description = "";
        if (!jsonObject.isNull("description")) {
            t_description = jsonObject.getString("description");
        }

        double t_rankCleanliness = jsonObject.optDouble("moyenne_proprete", -1.0);
        double t_rankFacilities = jsonObject.optDouble("moyenne_equipement", -1.0);
        double t_rankAccessibility = jsonObject.optDouble("moyenne_accessibilite", -1.0);
        double t_rankAverage = jsonObject.optDouble("moyenne", -1.0);

        double t_distance = 0.0;
        if (jsonObject.has("distance")) t_distance = jsonObject.getDouble("distance");

        Toilet t = new Toilet(t_id, t_adapted, t_address, new GeoPoint(t_lat, t_long), t_distance);
        t.setDescription(t_description);
        t.setRankCleanliness((int) Math.round(t_rankCleanliness));
        t.setRankFacilities((int) Math.round(t_rankFacilities));
        t.setRankAccessibility((int) Math.round(t_rankAccessibility));
        t.setRankAverage((int) Math.round(t_rankAverage));

        return t;
    }

    public Memo createMemo(JSONObject jsonObject) throws JSONException {
        int id = jsonObject.getInt("id");
        String title = jsonObject.getString("title");
        String filename = jsonObject.getString("filename");
        String salt = jsonObject.getString("salt");

        Memo m = new Memo(id, title, filename, salt);
        return m;
    }
}

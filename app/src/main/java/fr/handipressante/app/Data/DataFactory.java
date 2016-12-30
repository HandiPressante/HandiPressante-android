package fr.handipressante.app.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

/**
 * Created by Nico on 23/10/2015.
 */
public class DataFactory {
    public Toilet createToilet(JSONObject jsonObject) throws JSONException {
        int t_id = jsonObject.getInt("id");
        String t_name = jsonObject.getString("name");
        boolean t_adapted = jsonObject.optInt("adapted", 0) == 1;
        boolean t_charged = jsonObject.optInt("charged", 0) == 1;
        double t_lat = jsonObject.getDouble("lat84");
        double t_long = jsonObject.getDouble("long84");

        String t_description = "";
        if (!jsonObject.isNull("description")) {
            t_description = jsonObject.getString("description");
        }

        float t_rankCleanliness = (float) jsonObject.optDouble("cleanliness_avg", 0.0);
        float t_rankFacilities = (float) jsonObject.optDouble("facilities_avg", 0.0);
        float t_rankAccessibility = (float) jsonObject.optDouble("accessibility_avg", 0.0);
        int t_rateWeight = jsonObject.optInt("rate_weight", 0);

        Toilet t = new Toilet(t_id, t_adapted, t_charged, t_name, new GeoPoint(t_lat, t_long));
        t.setDescription(t_description);
        t.setRankCleanliness(t_rankCleanliness);
        t.setRankFacilities(t_rankFacilities);
        t.setRankAccessibility(t_rankAccessibility);
        t.setRateWeight(t_rateWeight);

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

    public Photo createPhoto(JSONObject jsonObject) throws JSONException {
        int id = jsonObject.getInt("id");
        int toiletId = jsonObject.getInt("toilet_id");
        String filename = jsonObject.getString("filename");
        String postdate = jsonObject.getString("postdate");

        return new Photo(id, toiletId, filename, postdate);
    }

    public Comment createComment(JSONObject jsonObject) throws JSONException {
        Integer id = jsonObject.getInt("id");
        String username = jsonObject.getString("username");
        String content = jsonObject.getString("content");
        String postdate = jsonObject.getString("postdate");

        return new Comment(id, username, content, postdate);
    }
}

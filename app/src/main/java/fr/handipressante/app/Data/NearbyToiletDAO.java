package fr.handipressante.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 28/03/2016.
 */
public class NearbyToiletDAO extends AbstractDAO {
    public static final String TABLE_NAME = "nearby_toilets";
    public static final String FIELD_KEY = "_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ADAPTED = "adapted";
    public static final String FIELD_CHARGED = "charged";
    public static final String FIELD_LATITUDE = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";
    public static final String FIELD_CLEANLINESS = "cleanliness";
    public static final String FIELD_FACILITIES = "facilities";
    public static final String FIELD_ACCESSIBILITY = "accessibility";
    public static final String FIELD_RATE_WEIGHT = "rate_weight";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            FIELD_KEY + " INTEGER PRIMARY KEY, " +
            FIELD_NAME + " TEXT, " +
            FIELD_DESCRIPTION + " TEXT, " +
            FIELD_ADAPTED + " INTEGER, " +
            FIELD_CHARGED + " INTEGER, " +
            FIELD_LATITUDE + " REAL, " +
            FIELD_LONGITUDE + " REAL, " +
            FIELD_CLEANLINESS + " REAL, " +
            FIELD_FACILITIES + " REAL, " +
            FIELD_ACCESSIBILITY + " REAL, " +
            FIELD_RATE_WEIGHT + " INTEGER);";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public NearbyToiletDAO(Context context) {
        super(context);
    }

    public void add(Toilet t) {
        ContentValues value = new ContentValues();
        value.put(FIELD_KEY, t.getId());
        value.put(FIELD_NAME, t.getName());
        value.put(FIELD_DESCRIPTION, t.getDescription());
        value.put(FIELD_ADAPTED, t.isAdapted() ? 1 : 0);
        value.put(FIELD_CHARGED, t.isCharged() ? 1 : 0);
        value.put(FIELD_LATITUDE, t.getPosition().latitude);
        value.put(FIELD_LONGITUDE, t.getPosition().longitude);
        value.put(FIELD_CLEANLINESS, t.getRankCleanliness());
        value.put(FIELD_FACILITIES, t.getRankFacilities());
        value.put(FIELD_ACCESSIBILITY, t.getRankAccessibility());
        value.put(FIELD_RATE_WEIGHT, t.getRateWeight());

        try {
            mDatabase.insertOrThrow(TABLE_NAME, null, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAll(List<Toilet> toiletList) {
        for (Toilet t :
                toiletList) {
            add(t);
        }
    }

    public List<Toilet> selectAll() {
        List<Toilet> results = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT " + FIELD_KEY + ", " +
                FIELD_NAME + ", " + FIELD_DESCRIPTION + ", " +
                FIELD_ADAPTED + ", " + FIELD_CHARGED + ", " +
                FIELD_LATITUDE + ", " + FIELD_LONGITUDE + ", " +
                FIELD_CLEANLINESS + ", " + FIELD_FACILITIES + ", " +
                FIELD_ACCESSIBILITY  + ", " + FIELD_RATE_WEIGHT +
                " FROM " + TABLE_NAME + ";", new String[] {});

        while (c.moveToNext()) {
            Toilet t = new Toilet();
            t.setId(c.getInt(0));
            t.setName(c.getString(1));
            t.setDescription(c.getString(2));
            t.setAdapted(c.getInt(3) == 1);
            t.setCharged(c.getInt(4) == 1);
            t.setPosition(new LatLng(c.getDouble(5), c.getDouble(6)));
            t.setRankCleanliness(c.getFloat(7));
            t.setRankFacilities(c.getFloat(8));
            t.setRankAccessibility(c.getFloat(9));
            t.setRateWeight(c.getInt(10));
            results.add(t);
        }
        c.close();

        return results;
    }

    public void truncate() {
        mDatabase.execSQL(DROP_TABLE);
        mDatabase.execSQL(CREATE_TABLE);
    }
}

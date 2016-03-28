package fr.handipressante.app.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 28/03/2016.
 */
public class NearbyToiletDAO extends AbstractDAO {
    public static final String TABLE_NAME = "nearby_toilets";
    public static final String FIELD_KEY = "_id";
    public static final String FIELD_ADAPTED = "adapted";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_LAT84 = "lat84";
    public static final String FIELD_LON84 = "lon84";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_CLEANLINESS = "cleanliness";
    public static final String FIELD_FACILITIES = "facilities";
    public static final String FIELD_ACCESSIBILITY = "accessibility";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            FIELD_KEY + " INTEGER PRIMARY KEY, " +
            FIELD_ADAPTED + " INTEGER, " +
            FIELD_ADDRESS + " TEXT, " +
            FIELD_LAT84 + " REAL, " +
            FIELD_LON84 + " REAL, " +
            FIELD_DESCRIPTION + " TEXT, " +
            FIELD_CLEANLINESS + " INTEGER, " +
            FIELD_FACILITIES + " INTEGER, " +
            FIELD_ACCESSIBILITY + " INTEGER);";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public NearbyToiletDAO(Context context) {
        super(context);
    }

    public void add(Toilet t) {
        ContentValues value = new ContentValues();
        value.put(FIELD_KEY, t.getId());
        value.put(FIELD_ADAPTED, t.isAdapted() ? 1 : 0);
        value.put(FIELD_ADDRESS, t.getAddress());
        value.put(FIELD_LAT84, t.getCoordinates().getLatitude());
        value.put(FIELD_LON84, t.getCoordinates().getLongitude());
        value.put(FIELD_DESCRIPTION, t.getDescription());
        value.put(FIELD_CLEANLINESS, t.getRankCleanliness());
        value.put(FIELD_FACILITIES, t.getRankFacilities());
        value.put(FIELD_ACCESSIBILITY, t.getRankAccessibility());

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

        Cursor c = mDatabase.rawQuery("SELECT " + FIELD_KEY + ", " + FIELD_ADAPTED + ", " +
                FIELD_ADDRESS + ", " + FIELD_LAT84 + ", " + FIELD_LON84 + ", " +
                FIELD_DESCRIPTION + ", " + FIELD_CLEANLINESS + ", " + FIELD_FACILITIES + ", " +
                FIELD_ACCESSIBILITY  + " FROM " + TABLE_NAME + ";", new String[] {});

        while (c.moveToNext()) {
            Toilet t = new Toilet();
            t.setId(c.getInt(0));
            t.setAdapted(c.getInt(1) == 1);
            t.setAddress(c.getString(2));
            t.setCoordinates(new GeoPoint(c.getDouble(3), c.getDouble(4)));
            t.setDescription(c.getString(5));
            t.setRankCleanliness(c.getInt(6));
            t.setRankFacilities(c.getInt(7));
            t.setRankAccessibility(c.getInt(8));
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

package fr.handipressante.app.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 25/05/2016.
 */
public class PhotoDAO extends AbstractDAO {
    public static final String TABLE_NAME = "photos";
    public static final String FIELD_KEY = "_id";
    public static final String FIELD_TOILET_ID = "toilet_id";
    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_FILENAME = "filename";
    public static final String FIELD_POSTDATE = "postdate";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            FIELD_KEY + " INTEGER PRIMARY KEY, " +
            FIELD_TOILET_ID + " INTEGER, " +
            FIELD_USER_ID + " TEXT, " +
            FIELD_FILENAME + " TEXT, " +
            FIELD_POSTDATE + " TEXT);";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public PhotoDAO(Context context) {
        super(context);
    }

    public void add(Photo p) {
        ContentValues value = new ContentValues();
        value.put(FIELD_KEY, p.getId());
        value.put(FIELD_TOILET_ID, p.getToiletId());
        value.put(FIELD_USER_ID, p.getUserId());
        value.put(FIELD_FILENAME, p.getFilename());
        value.put(FIELD_POSTDATE, p.getPostdate());

        try {
            mDatabase.insertOrThrow(TABLE_NAME, null, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(int id) {
        mDatabase.delete(TABLE_NAME, FIELD_KEY + " = ?", new String[]{String.valueOf(id)});
    }

    public void update(Photo p) {
        ContentValues value = new ContentValues();
        value.put(FIELD_TOILET_ID, p.getToiletId());
        value.put(FIELD_USER_ID, p.getUserId());
        value.put(FIELD_FILENAME, p.getFilename());
        value.put(FIELD_POSTDATE, p.getPostdate());

        mDatabase.update(TABLE_NAME, value, FIELD_KEY + " = ?", new String[]{String.valueOf(p.getId())});
    }

    public List<Photo> selectAll() {
        List<Photo> results = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT " + FIELD_KEY + ", " + FIELD_TOILET_ID+ ", " + FIELD_USER_ID + ", " +
                FIELD_FILENAME + ", " + FIELD_POSTDATE + " FROM " + TABLE_NAME + " ORDER BY " +
                FIELD_POSTDATE + ";", new String[] {});

        while (c.moveToNext()) {
            Photo p = new Photo();
            p.setId(c.getInt(0));
            p.setToiletId(c.getInt(1));
            p.setUserId(c.getString(2));
            p.setFilename(c.getString(3));
            p.setPostdate(c.getString(4));
            results.add(p);
        }
        c.close();

        return results;
    }

    public List<Photo> selectByToilet(Integer toiletId) {
        List<Photo> results = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT " + FIELD_KEY + ", " + FIELD_TOILET_ID+ ", " + FIELD_USER_ID + ", " +
                FIELD_FILENAME + ", " + FIELD_POSTDATE + " FROM " + TABLE_NAME +
                " WHERE " + FIELD_TOILET_ID + " = " + toiletId + " ORDER BY " +
                FIELD_POSTDATE + ";", new String[] {});

        while (c.moveToNext()) {
            Photo p = new Photo();
            p.setId(c.getInt(0));
            p.setToiletId(c.getInt(1));
            p.setUserId(c.getString(2));
            p.setFilename(c.getString(3));
            p.setPostdate(c.getString(4));
            results.add(p);
        }
        c.close();

        return results;
    }

    public void save(List<Photo> photos) {
        List<Integer> existingPhotos = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT " + FIELD_KEY + " FROM " + TABLE_NAME +  ";", new String[]{});
        while (c.moveToNext()) {
            existingPhotos.add(c.getInt(0));
        }
        c.close();

        for (Photo p : photos) {
            if (existingPhotos.contains(p.getId())) {
                update(p);
            } else {
                add(p);
            }
        }
    }
}

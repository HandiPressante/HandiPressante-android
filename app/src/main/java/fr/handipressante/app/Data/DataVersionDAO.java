package fr.handipressante.app.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by Nico on 14/03/2016.
 */
public class DataVersionDAO extends AbstractDAO {
    public static final String TABLE_NAME = "data_version";
    public static final String KEY = "id";
    public static final String DATA = "data";
    public static final String LAST_UPDATE = "last_update";

    public static final String DEFAULT_LAST_UPDATE = "0000-00-00 00:00:00";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
            KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DATA + " TEXT UNIQUE, " +
            LAST_UPDATE + " TEXT);";

    public static final String INSERT_MEMOS =
            "INSERT INTO " + TABLE_NAME + "(" + DATA + ", " + LAST_UPDATE + ") " +
                    "VALUES ('memos', '" + DEFAULT_LAST_UPDATE + "');";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public DataVersionDAO(Context context) {
        super(context);
    }

    public void update(String data, String date) {
        ContentValues value = new ContentValues();
        value.put(LAST_UPDATE, date);

        mDatabase.update(TABLE_NAME, value, DATA + " = ?", new String[] {data});
    }

    public String getLastUpdate(String data) {

        String lastUpdate = DEFAULT_LAST_UPDATE;
        Cursor c = mDatabase.rawQuery("SELECT " + LAST_UPDATE + " FROM " + TABLE_NAME +
                " WHERE " + DATA + " = ?;", new String[] {data});
        if (c.moveToNext()) {
            lastUpdate = c.getString(0);
        }
        c.close();

        return lastUpdate;
    }
}

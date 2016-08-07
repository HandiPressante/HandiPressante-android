package fr.handipressante.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 14/03/2016.
 */
public class MemoDAO extends AbstractDAO {
    public static final String TABLE_NAME = "memos";
    public static final String FIELD_KEY = "_id";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_FILENAME = "filename";
    public static final String FIELD_SALT = "salt";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            FIELD_KEY + " INTEGER PRIMARY KEY, " +
            FIELD_TITLE + " TEXT, " +
            FIELD_FILENAME + " TEXT, " +
            FIELD_SALT + " TEXT);";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public MemoDAO(Context context) {
        super(context);
    }

    public void add(Memo m) {
        ContentValues value = new ContentValues();
        value.put(FIELD_KEY, m.getId());
        value.put(FIELD_TITLE, m.getTitle());
        value.put(FIELD_FILENAME, m.getFilename());
        value.put(FIELD_SALT, m.getSalt());

        try {
            mDatabase.insertOrThrow(TABLE_NAME, null, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(int id) {
        mDatabase.delete(TABLE_NAME, FIELD_KEY + " = ?", new String[]{String.valueOf(id)});
    }

    public void update(Memo m) {
        ContentValues value = new ContentValues();
        value.put(FIELD_TITLE, m.getTitle());
        value.put(FIELD_FILENAME, m.getFilename());
        value.put(FIELD_SALT, m.getSalt());

        mDatabase.update(TABLE_NAME, value, FIELD_KEY + " = ?", new String[]{String.valueOf(m.getId())});
    }

    public List<Memo> selectAll() {
        List<Memo> results = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT " + FIELD_KEY + ", " + FIELD_TITLE + ", " +
                FIELD_FILENAME + ", " + FIELD_SALT + " FROM " + TABLE_NAME + " ORDER BY " +
                FIELD_TITLE + ";", new String[] {});

        while (c.moveToNext()) {
            Memo m = new Memo();
            m.setId(c.getInt(0));
            m.setTitle(c.getString(1));
            m.setFilename(c.getString(2));
            m.setSalt(c.getString(3));
            results.add(m);
        }
        c.close();

        return results;
    }

    public void save(List<Memo> memos) {
        List<Integer> existingMemos = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT " + FIELD_KEY + " FROM " + TABLE_NAME +  ";", new String[]{});
        while (c.moveToNext()) {
            existingMemos.add(c.getInt(0));
        }
        c.close();

        for (Memo m : memos) {
            if (existingMemos.contains(m.getId())) {
                update(m);
            } else {
                add(m);
            }
        }
    }
}

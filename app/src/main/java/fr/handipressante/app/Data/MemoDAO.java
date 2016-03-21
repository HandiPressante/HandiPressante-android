package fr.handipressante.app.Data;

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
    public static final String KEY = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_FILENAME = "filename";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            KEY + " INTEGER PRIMARY KEY, " +
            COL_TITLE + " TEXT, " +
            COL_FILENAME + " TEXT);";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public static final String DATA1 = "INSERT INTO " + TABLE_NAME +
            " (" + KEY + ", " + COL_TITLE + ", " + COL_FILENAME + ")" +
            " VALUES (1, 'Spécifications Fonctionnelles', 'SpecificationsFonctionnelles.pdf');";
    public static final String DATA2 = "INSERT INTO " + TABLE_NAME +
            " (" + KEY + ", " + COL_TITLE + ", " + COL_FILENAME + ")" +
            " VALUES (2, 'Plaque Urinaire (Aidant)', 'Plaque_urinaire_AIDANT.pdf');";

    public MemoDAO(Context context) {
        super(context);
    }

    public void add(Memo m) {
        ContentValues value = new ContentValues();
        value.put(KEY, m.getId());
        value.put(COL_TITLE, m.getTitle());
        value.put(COL_FILENAME, m.getFilename());

        try {
            mDatabase.insertOrThrow(TABLE_NAME, null, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(int id) {
        mDatabase.delete(TABLE_NAME, KEY + " = ?", new String[]{String.valueOf(id)});
    }

    public void update(Memo m) {
        ContentValues value = new ContentValues();
        value.put(COL_TITLE, m.getTitle());
        value.put(COL_FILENAME, m.getFilename());

        mDatabase.update(TABLE_NAME, value, KEY + " = ?", new String[]{String.valueOf(m.getId())});
    }

    public List<Memo> selectAll() {
        List<Memo> results = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT " + KEY + ", " + COL_TITLE + ", " + COL_FILENAME +
                " FROM " + TABLE_NAME + " ORDER BY " + COL_TITLE + ";", new String[] {});
        while (c.moveToNext()) {
            Memo m = new Memo();
            m.setId(c.getInt(0));
            m.setTitle(c.getString(1));
            m.setFilename(c.getString(2));
            results.add(m);
        }
        c.close();

        return results;
    }

    public void save(List<Memo> memos) {
        List<Integer> existingMemos = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT " + KEY + " FROM " + TABLE_NAME +  ";", new String[]{});
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

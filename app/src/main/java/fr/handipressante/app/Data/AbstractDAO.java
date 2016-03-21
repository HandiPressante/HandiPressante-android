package fr.handipressante.app.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nico on 14/03/2016.
 */
public class AbstractDAO {
    protected SQLiteDatabase mDatabase = null;
    protected DatabaseHandler mHandler = null;

    public AbstractDAO(Context context) {
        mHandler = new DatabaseHandler(context);
    }

    public SQLiteDatabase open() {
        mDatabase = mHandler.getWritableDatabase();
        return mDatabase;
    }

    public void close() {
        mDatabase.close();
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }
}

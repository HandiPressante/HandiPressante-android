package fr.handipressante.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nico on 14/03/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "handipressante";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NearbyToiletDAO.CREATE_TABLE);
        db.execSQL(PhotoDAO.CREATE_TABLE);
        db.execSQL(MemoDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(NearbyToiletDAO.DROP_TABLE);
        db.execSQL(PhotoDAO.DROP_TABLE);
        db.execSQL(MemoDAO.DROP_TABLE);
        onCreate(db);
    }
}

package fr.handipressante.app.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nico on 14/03/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "handipressante.db";
    private static final int DB_VERSION = 1;

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MemoDAO.CREATE_TABLE);
        db.execSQL(MemoDAO.DATA1);
        db.execSQL(MemoDAO.DATA2);

        db.execSQL(DataVersionDAO.CREATE_TABLE);
        db.execSQL(DataVersionDAO.INSERT_MEMOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MemoDAO.DROP_TABLE);
        db.execSQL(DataVersionDAO.DROP_TABLE);
        onCreate(db);
    }
}

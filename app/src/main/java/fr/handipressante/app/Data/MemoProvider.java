package fr.handipressante.app.Data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MemoProvider extends ContentProvider {
    private DatabaseHandler mDatabase;

    private static final String AUTHORITY = "fr.handipressante.app.Data.MemoProvider";
    public static final int MEMOS = 100;
    public static final int MEMO_ID = 110;

    private static final String MEMOS_BASE_PATH = "memos";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + MEMOS_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/memo";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/memo";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, MEMOS_BASE_PATH, MEMOS);
        sUriMatcher.addURI(AUTHORITY, MEMOS_BASE_PATH + "/#", MEMO_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase database = mDatabase.getWritableDatabase();
        int rowsAffected = 0;

        switch (uriType) {
            case MEMO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = database.delete(MemoDAO.TABLE_NAME,
                            MemoDAO.KEY + "=" + id, null);
                } else {
                    rowsAffected = database.delete(MemoDAO.TABLE_NAME,
                            MemoDAO.KEY + "=" + id, selectionArgs);
                }
                break;
            case MEMOS:
                rowsAffected = database.delete(MemoDAO.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown or invalid URI : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sUriMatcher.match(uri);

        switch (uriType) {
            case MEMOS:
                return CONTENT_TYPE;
            case MEMO_ID:
                return CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sUriMatcher.match(uri);
        if (uriType != MEMOS) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }

        SQLiteDatabase database = mDatabase.getWritableDatabase();
        Long newId = database.insert(MemoDAO.TABLE_NAME, null, values);
        if (newId > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, newId);
            getContext().getContentResolver().notifyChange(uri, null);
            return newUri;
        } else {
            Log.e("MemoProvider", "Failed to insert into " + uri);
            Log.i("MemoProvider", "Failed to insert into " + uri);
            return null;
        }
    }

    @Override
    public boolean onCreate() {
        mDatabase = new DatabaseHandler(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i("MemoProvider", "query");

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MemoDAO.TABLE_NAME);

        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case MEMO_ID:
                queryBuilder.appendWhere(MemoDAO.KEY + "=" + uri.getLastPathSegment());
                break;
            case MEMOS:
                break;
            default:
                throw new IllegalArgumentException("Unknown or invalid URI : " + uri);
        }

        Cursor cursor = queryBuilder.query(mDatabase.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase database = mDatabase.getWritableDatabase();
        int rowsAffected = 0;

        switch (uriType) {
            case MEMO_ID:
                String id = uri.getLastPathSegment();
                StringBuilder modSelection = new StringBuilder(MemoDAO.KEY + "=" + id);

                if (!TextUtils.isEmpty(selection)) {
                    modSelection.append(" AND " + selection);
                }

                rowsAffected = database.update(MemoDAO.TABLE_NAME,
                        values, modSelection.toString(), null);
                break;
            case MEMOS:
                rowsAffected = database.update(MemoDAO.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown or invalid URI : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }
}

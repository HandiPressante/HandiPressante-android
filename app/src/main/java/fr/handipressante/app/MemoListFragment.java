package fr.handipressante.app;


/**
 * Created by Nico on 06/03/2016.
 */

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.util.Log;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;

import java.io.File;

import fr.handipressante.app.Data.MemoDAO;
import fr.handipressante.app.Data.MemoProvider;


public class MemoListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MEMO_LIST_LOADER = 0x01;
    private CursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MemoListFragment", "onCreate");

        getLoaderManager().initLoader(MEMO_LIST_LOADER, null, this);
        adapter = new MemoListAdapter(getActivity().getApplicationContext(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        setListAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("MemoListFragment", "onActivityCreated");
    }

    /* Request updates at startup */
    @Override
    public void onResume() {
        super.onResume();
        Log.i("MemoListFragment", "onResume");

        for (String file : getActivity().getApplicationContext().fileList()) {
            Log.i("MemoListFragment", "File : " + file);
        }
/*
        if (MemoManager.instance(getActivity().getApplicationContext()).getMemoList().size() == 0) {
            MemoManager.instance(getActivity().getApplicationContext()).update();

        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("MemoListFragment", "onPause");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String[] projection = {MemoDAO.COL_FILENAME};
        Cursor memoCursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(MemoProvider.CONTENT_URI, String.valueOf(id)),
                projection, null, null, null);

        if (memoCursor.moveToFirst()) {
            String filename = memoCursor.getString(0);

            File file = new File(getContext().getFilesDir(), "memos/" + filename);
            Log.i("MemoListFragment", file.getAbsolutePath());
            Uri contentUri = FileProvider.getUriForFile(getContext(), "fr.handipressante.app", file);
            Log.i("MemoListFragment", "Content uri : " + contentUri.toString());

            Intent intent = new Intent(Intent.ACTION_VIEW, contentUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.i("MemoListFragment", "PDF Activity Not Found Exception");
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {MemoDAO.KEY, MemoDAO.COL_TITLE};
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                MemoProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}


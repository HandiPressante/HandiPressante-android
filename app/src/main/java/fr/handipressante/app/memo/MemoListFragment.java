package fr.handipressante.app.memo;


/**
 * Created by Nico on 06/03/2016.
 */

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ListFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.handipressante.app.data.Memo;
import fr.handipressante.app.data.MemoDAO;
import fr.handipressante.app.help.HelpSlideMemo;
import fr.handipressante.app.R;
import fr.handipressante.app.server.Downloader;
import fr.handipressante.app.server.MemoDownloader;

public class MemoListFragment extends ListFragment {
    private MemoListAdapter mAdapter;
    private ProgressDialog mMemoDownloadDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_memolist, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i("MemoListFragment", "onCreate");

        mAdapter = new MemoListAdapter(getContext().getApplicationContext(), new ArrayList<Memo>());
        // We don't set listAdapter here, because we want the loading animation

        new LoadDatabaseTask().execute();
        // onPostExecute : data are loaded from the server
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("MemoListFragment", "onActivityCreated");

        Toolbar toolbarBottom = (Toolbar) getActivity().findViewById(R.id.toolbar_scroll);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean scroll_help = sharedPrefs.getBoolean("scroll_help", false);

        if(!scroll_help) {
            toolbarBottom.setVisibility(View.GONE);
        }

        getActivity().findViewById(R.id.upList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int offset = computeVisibleItemCount();
                getListView().smoothScrollByOffset(-offset);
            }
        });
        getActivity().findViewById(R.id.downList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int offset = computeVisibleItemCount();
                getListView().smoothScrollByOffset(offset);
            }
        });

        getActivity().setTitle(R.string.menu_memos);
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("MemoListFragment", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("MemoListFragment", "onPause");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Memo m = mAdapter.getItem(position);

        if (m != null) {
            File file = new File(getContext().getFilesDir(), m.getLocalPath());

            if (file.exists()) {
                openFile(file);
            } else {
                mMemoDownloadDialog = new ProgressDialog(getContext());
                mMemoDownloadDialog.setTitle("Veuillez patienter");
                mMemoDownloadDialog.setMessage("Téléchargement en cours ...");
                mMemoDownloadDialog.setIndeterminate(false);
                mMemoDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mMemoDownloadDialog.setMax(100);
                mMemoDownloadDialog.show();

                new DownloadTask().execute(m);
            }
        }
    }

    private int computeVisibleItemCount() {
        if (mAdapter.getCount() == 0)
            return 0;

        int listViewHeight = getListView().getHeight();
        View listItem = mAdapter.getView(0, null, getListView());
        listItem.measure(0, 0);
        int childItemHeight = listItem.getMeasuredHeight() + getListView().getDividerHeight();

        return listViewHeight / childItemHeight;
    }

    private void openFile(File file) {
        Uri contentUri = FileProvider.getUriForFile(getContext(), "fr.handipressante.app", file);

        Intent intent = new Intent(Intent.ACTION_VIEW, contentUri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e("MemoListFragment", "PDF Activity Not Found Exception");
        }
    }

    private class LoadDatabaseTask extends AsyncTask<Void, Void, List<Memo>> {
        @Override
        protected List<Memo> doInBackground(Void... params) {
            MemoDAO dao = new MemoDAO(getContext());
            dao.open();
            List<Memo> result = dao.selectAll();
            dao.close();

            return result;
        }

        @Override
        protected void onPostExecute(List<Memo> memoList) {
            if (!memoList.isEmpty()) {
                mAdapter.swapItems(memoList);
                setListAdapter(mAdapter);
            }

            syncWithServer();
        }
    }

    private void syncWithServer() {
        new MemoDownloader(getContext()).downloadMemoList(new Downloader.Listener<List<Memo>>() {
            @Override
            public void onResponse(List<Memo> response) {
                new UpdateDatabaseTask().execute(response);

                mAdapter.swapItems(response);
                setListAdapter(mAdapter);
            }
        });
    }

    private class UpdateDatabaseTask extends AsyncTask<List<Memo>, Void, Void> {
        @Override
        protected Void doInBackground(List<Memo>... params) {
            if (params.length != 1) return null;

            List<Memo> memoList = params[0];
            if (memoList == null) return null;

            MemoDAO dao = new MemoDAO(getContext());
            dao.open();
            List<Memo> oldMemoList = dao.selectAll();

            // Removed memos
            for (Memo oldMemo : oldMemoList) {
                boolean removed = true;
                for (Memo memo : memoList) {
                    if (memo.getId().equals(oldMemo.getId())) {
                        removed = false;
                        break;
                    }
                }

                if (removed) {
                    File file = new File(getContext().getFilesDir(), oldMemo.getLocalPath());
                    file.delete();
                    dao.remove(oldMemo.getId());
                }
            }

            // Updated and added memos
            for (Memo memo : memoList) {
                boolean updated = false;
                for (Memo oldMemo : oldMemoList) {
                    if (memo.getId().equals(oldMemo.getId())) {

                        if (!oldMemo.getSalt().equals(memo.getSalt())) {
                            File file = new File(getContext().getFilesDir(), oldMemo.getLocalPath());
                            file.delete();
                        }
                        updated = true;

                        break;
                    }
                }

                if (updated) {
                    dao.update(memo);
                } else {
                    dao.add(memo);
                }
            }

            dao.close();
            return null;
        }
    }


    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<Memo, Integer, String> {
        private PowerManager.WakeLock mWakeLock;
        private Memo mMemo;

        @Override
        protected String doInBackground(Memo... m) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                mMemo = m[0];
                URL url = new URL(mMemo.getRemoteUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                File memoDir = new File(getContext().getFilesDir(), mMemo.getFolder());
                if (!memoDir.exists()) memoDir.mkdir();

                File file = new File(getContext().getFilesDir(), mMemo.getLocalPath());
                file.setExecutable(false);
                file.setReadable(true, false);
                file.setWritable(true);

                //output = context.openFileOutput(mMemo.getLocalPath(), Context.MODE_PRIVATE);
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values.length != 1) return;
            mMemoDownloadDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Log.e("MemoManager", "Download error: " + result);
                //Toast.makeText(getContext(), "Download error: " + result, Toast.LENGTH_LONG).show();
                mMemoDownloadDialog.dismiss();
            }
            else {
                mMemoDownloadDialog.dismiss();

                File file = new File(getContext().getFilesDir(), mMemo.getLocalPath());
                if (file.exists()) {
                    openFile(file);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Intent intentHelp = new Intent(getContext(), HelpSlideMemo.class);
                startActivity(intentHelp);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}


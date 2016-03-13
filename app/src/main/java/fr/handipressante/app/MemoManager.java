package fr.handipressante.app;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Nico on 06/03/2016.
 */
public class MemoManager {
    private static MemoManager INSTANCE;

    private Context mCtx;
    private List<Memo> mMemoList;
    private Queue<Memo> mToDownload = new LinkedList<>();

    private MemoManager(Context context) {
        mCtx = context;
        mMemoList = new ArrayList<>();

        /*mMemoList.add(new Memo(1, "Spécifications Fonctionnelles", "SpecificationsFonctionnelles.pdf"));
        mMemoList.add(new Memo(2, "Conception", "http://handipressante.carbonkiwi.net/memo/RapportConception.pdf"));*/
    }
    public static synchronized MemoManager instance(Context ctx) {
        if (INSTANCE == null) INSTANCE = new MemoManager(ctx);
        return INSTANCE;
    }

    public interface MemoListListener {
        void onDataChanged();
    }

    private List<MemoListListener> mMemoListListeners = new ArrayList<>();

    public void addMemoListListener(MemoListListener listener) {
        mMemoListListeners.add(listener);
    }

    // TODO : Really needed ?
    public void removeMemoListListener(MemoListListener listener) {
        mMemoListListeners.remove(listener);
    }

    private void notifyMemoListListeners() {
        for (MemoListListener listener : mMemoListListeners) {
            listener.onDataChanged();
        }
    }

    public List<Memo> getMemoList() {
        return mMemoList;
    }

    public void update() {
        Memo m1 = new Memo(1, "Spécifications Fonctionnelles", "SpecificationsFonctionnelles.pdf", "http://handipressante.carbonkiwi.net/memo/SpecificationsFonctionnelles.pdf");
        Memo m2 = new Memo(2, "Plaque Urinaire (Aidant)", "PlaqueUrinaire_AIDANT.pdf", "http://handipressante.carbonkiwi.net/memo/Plaqu_urinaire_AIDANT.pdf");
        Memo m3 = new Memo(2, "Plaque Urinaire (Usager)", "PlaqueUrinaire_USAGER.pdf", "http://handipressante.carbonkiwi.net/memo/Plaqu_urinaire_USAGER.pdf");

        mToDownload.add(m1);
        mToDownload.add(m2);
        mToDownload.add(m3);

        nextDownload();
    }

    public void nextDownload() {
        Memo m = mToDownload.poll();
        if (m != null) {
            final DownloadTask downloadTask = new DownloadTask(mCtx);
            downloadTask.execute(m);
        }
    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<Memo, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private Memo mMemo = null;

        public DownloadTask(Context context) {
            this.context = context;
        }

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

                File memoDir = new File(context.getFilesDir(), mMemo.getFolder());
                if (!memoDir.exists()) memoDir.mkdir();

                File file = new File(context.getFilesDir(), mMemo.getLocalPath());
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
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Log.i("MemoManager", "Download error: " + result);
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                mMemoList.add(mMemo);
                notifyMemoListListeners();
            }

            nextDownload();
        }
    }
}

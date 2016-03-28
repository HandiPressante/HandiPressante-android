package fr.handipressante.app.Server;

import android.content.Context;

/**
 * Created by Nico on 27/03/2016.
 */
public class Downloader {
    public interface Listener<T> {
        void onResponse(T response);
    }
    protected Context mContext;

    public Downloader(Context context) {
        mContext = context;
    }
}

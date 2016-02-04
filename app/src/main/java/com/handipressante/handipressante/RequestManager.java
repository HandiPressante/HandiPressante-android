package com.handipressante.handipressante;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Nico on 02/02/2016.
 */
public class RequestManager {
    private static RequestManager INSTANCE;
    public static synchronized RequestManager instance(Context ctx) {
        if (INSTANCE == null) INSTANCE = new RequestManager(ctx);
        return INSTANCE;
    }

    private RequestQueue mRequestQueue;
    private Context mCtx;

    private RequestManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

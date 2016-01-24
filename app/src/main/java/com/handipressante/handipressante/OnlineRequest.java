package com.handipressante.handipressante;

import android.util.Log;

import java.io.InputStream;

/**
 * Created by nico on 21/01/2016.
 */
public abstract class OnlineRequest {
    private String _url;
    protected String _data;

    public OnlineRequest() {
        _url = "";
    }

    public OnlineRequest(String url) {
        _url = url;
    }

    public String getUrl() {
        return _url;
    }

    public void setData(String data) {
        _data = data;
    }

    public abstract void postExecute();
}

package fr.handipressante.app.server;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.handipressante.app.MyConstants;

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

    protected JSONArray getData(JSONObject response) throws ServerResponseException {
        JSONArray data = response.optJSONArray("data");
        if (data == null) {
            throw new ServerResponseException();
        }

        return data;
    }

    protected void checkResponse(JSONObject response) throws ServerResponseException {
        int version = response.optInt("version");
        if (!response.has("version")) {
            throw new ServerResponseException();
        }

        if (version > MyConstants.API_VERSION) {
            throw new ServerResponseException("Veuillez mette HandiPressante à jour.");
        }
        try {
            if (!response.getBoolean("success")) {
                String error = response.getString("errorText");
                throw new ServerResponseException(error);
            }
        } catch (JSONException e) {
            throw new ServerResponseException();
        }
    }
}

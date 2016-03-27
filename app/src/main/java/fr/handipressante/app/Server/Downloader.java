package fr.handipressante.app.Server;

/**
 * Created by Nico on 27/03/2016.
 */
public class Downloader {
    public interface Listener<T> {
        void onResponse(T response);
    }
}

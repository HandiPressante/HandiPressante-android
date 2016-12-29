package fr.handipressante.app.server;

/**
 * Created by Nico on 21/12/2016.
 */
public class ServerResponseException extends Exception {
    public ServerResponseException() {
        super("Une erreur serveur est survenue.");
    }

    public ServerResponseException(String error) {
        super("Erreur : " + error);
    }
}

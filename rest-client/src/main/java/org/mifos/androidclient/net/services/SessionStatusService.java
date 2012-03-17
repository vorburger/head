package org.mifos.androidclient.net.services;

import org.mifos.androidclient.entities.SessionStatus;

/**
 * A simple service providing an utility to check if the user
 * session is available.
 */
public class SessionStatusService extends RestNetworkService {

    public final static String STATUS_PATH = "/status.json";

    public SessionStatusService(String serverURL) {
        super(serverURL);
    }

    public SessionStatus getSessionStatus() {
        return mRestConnector.getForObject(getServerUrl() + STATUS_PATH, SessionStatus.class);
    }

}

package org.mifos.androidclient.net.services;

import org.mifos.androidclient.entities.account.AcceptedPaymentTypes;

public class SystemSettingsService extends RestNetworkService {

    private static final String ACCEPTED_PAYMENT_TYPES_PATH = "/admin/payment-types/state-accepted.json";

    public SystemSettingsService(String serverURL) {
        super(serverURL);
    }

    public AcceptedPaymentTypes getAcceptedPaymentTypes() {
        String url = getServerUrl() + ACCEPTED_PAYMENT_TYPES_PATH;
        return mRestConnector.getForObject(url, AcceptedPaymentTypes.class);
    }

}

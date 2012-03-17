/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.androidclient.net.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.androidclient.entities.collectionsheet.CollectionSheetData;

public class CollectionSheetService extends RestNetworkService {

    private final static String GET_COLLECTION_SHEET_PATH = "/collectionsheet/customer/id-%s.json";
    private final static String SET_COLLECTION_SHEET_PATH = "/collectionsheet/save.json";
    
    public static final String INVALID_COLLECTION_SHEET = "invalidCollectionSheet";
    public static final String ERRORS = "errors";

    // TODO Urgh, what's this... should @deprecated see below...
    public final static String STATUS_KEY = "status";
    public final static String STATUS_SUCCESS = "success";
    public final static String STATUS_ERROR = "error";
    public final static String STATUS_INTERRUPT = "interrupt";
    public final static String CAUSE_KEY = "cause";
    
    public CollectionSheetService(String serverURL) {
        super(serverURL);
    }

    public CollectionSheetData getCollectionSheetForCustomer(Integer customerId) {
        String url = getServerUrl() + String.format(GET_COLLECTION_SHEET_PATH, customerId.toString());
        return mRestConnector.getForObject(url, CollectionSheetData.class);
    }

    @SuppressWarnings("unchecked")
    // TODO Urgh... we shouldn't return a Map here, but a clear return holder helper class, and @deprecated this...
	public Map<String, String> setCollectionSheetForCustomer(Map<String, String> params) {
        String url = getServerUrl() + String.format(SET_COLLECTION_SHEET_PATH);
        Map<String, Object> map = mRestConnector.postForObject(url, params, Map.class);
        Map<String, String> results = new HashMap<String, String>();
        StringBuilder stringBuilder = new StringBuilder();
        List<String> errorCause = (List<String>) map.get(INVALID_COLLECTION_SHEET);

        if(errorCause != null && !errorCause.isEmpty()) {
            stringBuilder.append("Errors: ");
            for (String error : errorCause) {
                stringBuilder.append(error);
                stringBuilder.append(", ");
            }
        }
        String errors = (String) map.get(ERRORS);
        if (errors != null && errors.length() > 0) {
            stringBuilder.append(errors);
        }
        if(stringBuilder.length() > 0) {
            results.put(STATUS_KEY, STATUS_ERROR);
            results.put(CAUSE_KEY, stringBuilder.toString());
        }
        else {
            results.put(STATUS_KEY, STATUS_SUCCESS);
        }

        return results;
    }
}

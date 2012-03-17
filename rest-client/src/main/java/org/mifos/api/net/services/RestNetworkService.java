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

package org.mifos.api.net.services;

import java.util.Map;

import org.mifos.api.net.RestConnector;

public abstract class RestNetworkService {

    protected final static String STATUS_PATH = "/status.json";
    protected final static String PATH_SUFFIX = ".json";

	private final String mServerURL;
    protected final RestConnector mRestConnector;

    public RestNetworkService(String serverURL) {
    	mServerURL = serverURL;
        mRestConnector = RestConnector.getInstance();
    }

    protected String getServerUrl() {
    	return mServerURL;
    }

    protected String prepareQueryString(Map<String, String> params) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("?");
        for (Map.Entry<String, String> entry: params.entrySet()) {
            buffer.append(entry.getKey());
            buffer.append("=");
            buffer.append(entry.getValue());
            buffer.append("&");
        }
        buffer.deleteCharAt(buffer.length()-1);
        return buffer.toString();
    }


}

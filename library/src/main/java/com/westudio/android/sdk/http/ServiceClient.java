package com.westudio.android.sdk.http;

import com.westudio.android.sdk.loopj.android.http.AsyncHttpClient;

public class ServiceClient {

    private static volatile ServiceClient client = null;
    private AsyncHttpClient httpClient = null;

    private ServiceClient() {
        httpClient = new AsyncHttpClient();
    }

    public static ServiceClient getInstance() {
        if (client == null) {
            synchronized (ServiceClient.class) {
                if (client == null) {
                    client = new ServiceClient();
                }
            }
        }

        return client;
    }
}

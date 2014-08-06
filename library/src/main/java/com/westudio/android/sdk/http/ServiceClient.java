package com.westudio.android.sdk.http;

import com.westudio.android.sdk.loopj.android.http.AsyncHttpClient;
import com.westudio.android.sdk.uitls.Serializer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServiceClient {

    private static volatile ServiceClient client = null;
    private AsyncHttpClient httpClient = null;

    private String serviceUrl = null;
    private final Serializer serializer_ = new Serializer();

    private static final String FORMAT = "json";
    private static final String CONTENT_TYPE = "application/json";

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

    // The only request interface
    public <T extends SpecificRecordBase, W extends SpecificRecordBase> void invoke(T requestObj, String opName, Class<W> clazz, ServiceCallback<W> callback) {

        try {
            if (serviceUrl == null) {
                throw new IllegalArgumentException("Endpoint url is missing");
            }

            if (callback == null) {
                throw new IllegalArgumentException("callback is missing");
            }

            if (clazz == null) {
                throw new IllegalArgumentException("target bind class is missing");
            }

            ByteArrayOutputStream os = serializer_.serializer(requestObj);
            HttpEntity httpEntity = new ByteArrayEntity(os.toByteArray());
            String url = generateUrl(opName);

            httpClient.post(null, url, null, httpEntity, CONTENT_TYPE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    private String generateUrl(String opName) {
        return String.format("%s/%s/%s", serviceUrl, FORMAT, opName);
    }
}

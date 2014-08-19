package com.westudio.android.sdk.http;

import com.westudio.android.sdk.exceptions.ServiceClientError;
import com.westudio.android.sdk.loopj.android.http.AsyncHttpClient;
import com.westudio.android.sdk.utils.ALog;
import com.westudio.android.sdk.utils.Serializer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

import java.io.ByteArrayOutputStream;

public class ServiceClient {

    private static final String LOG_TAG = "ServiceClient";

    private static volatile ServiceClient client = null;
    private AsyncHttpClient httpClient = null;

    private String serviceUrl = null;
    private final Serializer serializer_ = new Serializer();

    private static final String DEFAULT_FORMAT = "json";
    private static final String CONTENT_TYPE = "application/json";

    private String charset = "UTF-8";

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
    public <T extends SpecificRecordBase, W extends SpecificRecordBase> void invoke(T requestObj, String opName, Class<T> requestType, Class<W> responseType, ServiceCallback<W> callback) {

        try {
            if (serviceUrl == null) {
                throw new IllegalArgumentException("service url is missing");
            }

            if (callback == null) {
                throw new IllegalArgumentException("callback is missing");
            }

            if (requestType == null || responseType == null) {
                throw new IllegalArgumentException("bind class is missing");
            }

            ByteArrayOutputStream os = serializer_.serializer(requestObj, requestType);
            HttpEntity httpEntity = new ByteArrayEntity(os.toByteArray());
            String url = generateUrl(opName);

            ResponseHandler handler = new ResponseHandler(callback, responseType);

            httpClient.post(null, url, null, httpEntity, CONTENT_TYPE, handler);
        } catch (Exception e) {
            ALog.e(LOG_TAG, "Fail to send request", e);
            if (callback != null) {
                callback.onErrorResponse(new ServiceClientError("Fail to send request", e));
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Generate the final service url
     * @param opName specific operation name
     * @return the final service url
     */
    private String generateUrl(String opName) {
        return String.format("%s/%s/%s", serviceUrl, DEFAULT_FORMAT, opName);
    }

    /**
     * Get inner async http client
     * @return inner async http client
     */
    public AsyncHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Get current service url setting
     * @return service url
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * Set target service url
     * @param serviceUrl target service url
     */
    public void setServiceUrl(String serviceUrl) {
        if (serviceUrl != null) {
            this.serviceUrl = serviceUrl;
        }
    }

    /**
     * Get current charset for message encoding
     * @return charset charset for message encoding
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Set charset for message encoding
     * @param charset charset for message encoding
     */
    public void setCharset(String charset) {
        if (charset != null) {
            this.charset = charset;
        }
    }
}

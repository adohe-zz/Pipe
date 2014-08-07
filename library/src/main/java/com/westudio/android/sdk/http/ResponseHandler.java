package com.westudio.android.sdk.http;

import com.westudio.android.sdk.loopj.android.http.AsyncHttpResponseHandler;
import com.westudio.android.sdk.uitls.Serializer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

public class ResponseHandler extends AsyncHttpResponseHandler {

    private static final String LOG_TAG = "ResponseHandler";

    protected static final int SUCCESS_RESPONSE_HANDLING_MESSAGE = 100;

    private ServiceCallback callback;
    private Class<?> clazz;

    private Serializer serializer = new Serializer();

    public ResponseHandler(ServiceCallback callback, Class<?> clazz) {
        super();
        this.callback = callback;
        this.clazz = clazz;
    }

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine statusLine = response.getStatusLine();
            String responseBody = null;
            try {
                HttpEntity entity = null;
                HttpEntity temp = response.getEntity();
                if (temp != null) {
                    entity = new BufferedHttpEntity(temp);
                    responseBody = EntityUtils.toString(entity, "UTF-8");
                }
            } catch (IOException e) {
                sendFailureMessage(e, "error to get response body", responseBody);
                return;
            }

            if (statusLine.getStatusCode() >= 300) {
                sendFailureMessage(statusLine.getStatusCode(), response.getAllHeaders(), responseBody, new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase()));
            } else {
                sendSuccessMessage(statusLine.getStatusCode(), response.getAllHeaders(), response.getEntity().getContent());
            }
        }
    }

    @Override
    public void sendSuccessMessage(int statusCode, Header[] headers, InputStream is) {
        try {
            Object responseObj = serializer.deserialize(is);
            sendMessage(obtainMessage(SUCCESS_RESPONSE_HANDLING_MESSAGE, new Object[]{responseObj}));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendFailureMessage(int statusCode, Header[] headers, String responseBody, Throwable error) {
    }

    private void sendFailureMessage(Throwable e, String errorMessage, String responseBody) {
    }

    @Override
    public void sendFinishMessage() {
        super.sendFinishMessage();
    }

    @Override
    public void sendCancelMessage() {
        super.sendCancelMessage();
    }
}

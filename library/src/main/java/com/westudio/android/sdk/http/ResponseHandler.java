package com.westudio.android.sdk.http;

import android.os.Message;
import android.util.Log;

import com.westudio.android.sdk.exceptions.ServiceClientError;
import com.westudio.android.sdk.loopj.android.http.AsyncHttpResponseHandler;
import com.westudio.android.sdk.utils.Serializer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ResponseHandler extends AsyncHttpResponseHandler {

    private static final String LOG_TAG = "ResponseHandler";

    protected static final int SUCCESS_RESPONSE_HANDLING_MESSAGE = 100;

    private ServiceCallback callback;
    private Class<? extends SpecificRecordBase> clazz;

    private Serializer serializer = new Serializer();
    private String charset = DEFAULT_CHARSET;

    public ResponseHandler(ServiceCallback callback, Class<? extends SpecificRecordBase> clazz) {
        super();
        this.callback = callback;
        this.clazz = clazz;
    }

    @Override
    public void onFailure(Throwable error, String errorMsg) {
        this.callback.onErrorResponse(new ServiceClientError(errorMsg, error));
    }

    @SuppressWarnings("unchecked")
    protected void handleSuccessMessage(Object responseObject) {
        this.callback.onResponse(responseObject);
    }

    protected void handleMessage(Message msg) {
        Log.v(LOG_TAG, "HandleMessage");
        Object[] response;

        switch (msg.what) {
            case SUCCESS_RESPONSE_HANDLING_MESSAGE:
                response = (Object[])msg.obj;
                handleSuccessMessage(response[0]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[])msg.obj;
                handleFailureMessage((Throwable)response[0], (String)response[1]);
                break;
        }
    }

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {
        Log.v("ResponseHandler", "response");
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine statusLine = response.getStatusLine();
            String responseBody = null;
            try {
                HttpEntity entity;
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
                sendFailureMessage(new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase()), "response exceptions", responseBody);
            } else {
                Log.v("ResponseHandler", "success");
                sendSuccessMessage(statusLine.getStatusCode(), response.getAllHeaders(), responseBody);
            }
        }
    }

    @Override
    public void sendSuccessMessage(int statusCode, Header[] headers, String responseBody) {
        try {
            Object responseObj = serializer.deserialize(new ByteArrayInputStream(responseBody.getBytes()), clazz);
            sendMessage(obtainMessage(SUCCESS_RESPONSE_HANDLING_MESSAGE, new Object[]{responseObj}));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendFailureMessage(int statusCode, Header[] headers, String responseBody, Throwable error) {
    }

    @Override
    public void sendFailureMessage(Throwable e, String errorMessage, String responseBody) {
        if (e instanceof HttpResponseException) {
            HttpResponseException exception = (HttpResponseException)e;
            if (exception.getStatusCode() >= 300 && responseBody != null) {
                try {
                    Object responseObj = serializer.deserialize(new ByteArrayInputStream(responseBody.getBytes(charset)), clazz);
                    sendMessage(obtainMessage(SUCCESS_RESPONSE_HANDLING_MESSAGE, new Object[]{responseObj}));
                    return;
                } catch (IOException e1) {/**/}
            }
        }

        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, errorMessage}));
    }

    @Override
    public void sendFinishMessage() {
    }

    @Override
    public void sendCancelMessage() {
    }
}

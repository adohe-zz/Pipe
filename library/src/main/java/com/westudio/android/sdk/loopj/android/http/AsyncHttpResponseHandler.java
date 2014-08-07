package com.westudio.android.sdk.loopj.android.http;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class AsyncHttpResponseHandler implements ResponseHandlerInterface {

    private static final String LOG_TAG = "AsyncHttpResponseHandler";

    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;

    public static final String DEFAULT_CHARSET = "UTF-8";

    private Handler handler;

    public AsyncHttpResponseHandler() {
        if (Looper.myLooper() != null) {
            this.handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    AsyncHttpResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }

    public void onSuccess(String content) {
    }

    public void onSuccess(int statusCode, Header[] headers, String content) {
        onSuccess(statusCode, content);
    }

    public void onSuccess(int statusCode, String content) {
        onSuccess(content);
    }

    public void onFailure(Throwable error) {
    }

    public void onFailure(Throwable error, String content) {
        onFailure(error);
    }

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {
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
        }

        if (statusLine.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase()), "response exception", responseBody);
        } else {
            sendSuccessMessage(statusLine.getStatusCode(), response.getAllHeaders(), responseBody);
        }
    }

    @Override
    public void sendFinishMessage() {
    }

    @Override
    public void sendCancelMessage() {
    }

    @Override
    public void sendSuccessMessage(int statusCode, Header[] headers, String responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, headers, responseBody}));
    }

    @Override
    public void sendFailureMessage(int statusCode, Header[] headers, String responseBody, Throwable error) {
    }

    @Override
    public void sendFailureMessage(Throwable e, String errorMessage, String responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, errorMessage, responseBody}));
    }

    protected void handleSuccessMessage(int statusCode, Header[] headers, String responseBody) {
        onSuccess(statusCode, headers, responseBody);
    }

    protected void handleFailureMessage(Throwable e, String errorMessage) {
        onFailure(e, errorMessage);

    }

    protected void handleMessage(Message msg) {
        Object[] response;

        switch (msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[])msg.obj;
                handleSuccessMessage((Integer)response[0], (Header[])response[1], (String)response[2]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[])msg.obj;
                handleFailureMessage((Throwable)response[0], (String)response[1]);
                break;
        }
    }

    protected void sendMessage(Message msg) {
        if (this.handler != null) {
            this.handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg;
        if (this.handler != null) {
            msg = this.handler.obtainMessage(responseMessage, response);
        } else {
            msg = Message.obtain();
            msg.what = responseMessage;
            msg.obj = response;
        }

        return msg;
    }
}

package com.westudio.android.sdk.http;

import com.westudio.android.sdk.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.BufferedHttpEntity;

import java.io.IOException;

public class ResponseHandler extends AsyncHttpResponseHandler {

    private static final String LOG_TAG = "ResponseHandler";

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine statusLine = response.getStatusLine();
            String responseBody = null;
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if (temp != null) {
                entity = new BufferedHttpEntity(temp);
            }
        }
    }

    @Override
    public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {
        super.sendSuccessMessage(statusCode, headers, responseBody);
    }

    @Override
    public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        super.sendFailureMessage(statusCode, headers, responseBody, error);
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

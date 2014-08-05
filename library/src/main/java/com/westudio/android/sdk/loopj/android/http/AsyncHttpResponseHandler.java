package com.westudio.android.sdk.loopj.android.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.IOException;

public class AsyncHttpResponseHandler implements ResponseHandlerInterface {

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {

    }

    @Override
    public void sendFinishMessage() {

    }

    @Override
    public void sendCancelMessage() {

    }

    @Override
    public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {

    }

    @Override
    public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

    }
}

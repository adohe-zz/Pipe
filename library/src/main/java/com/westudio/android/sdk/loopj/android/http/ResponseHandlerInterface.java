package com.westudio.android.sdk.loopj.android.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

public interface ResponseHandlerInterface {

    void sendResponseMessage(HttpResponse response) throws IOException;

    void sendFinishMessage();

    void sendCancelMessage();

    void sendSuccessMessage(int statusCode, Header[] headers, String responseBody);

    void sendFailureMessage(int statusCode, Header[] headers, String responseBody, Throwable error);
}

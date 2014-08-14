package com.westudio.android.sdk.loopj.android.http;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class AsyncHttpRequest implements Runnable {

    private static final String LOG_TAG = "AsyncHttpRequest";

    private final AbstractHttpClient httpClient;
    private final HttpUriRequest request;
    private final HttpContext httpContext;
    private final AsyncHttpResponseHandler responseHandler;

    private boolean isCancelled = false;
    private boolean isFinished = false;

    public AsyncHttpRequest(AbstractHttpClient httpClient, HttpUriRequest request, HttpContext httpContext, AsyncHttpResponseHandler responseHandler) {
        this.httpClient = httpClient;
        this.request = request;
        this.httpContext = httpContext;
        this.responseHandler = responseHandler;
    }

    @Override
    public void run() {
        makeRequestWithExceptionsHandling();
    }

    private void makeRequest() throws IOException {
        if (!Thread.currentThread().isInterrupted()) {
            try {
                HttpResponse response = httpClient.execute(request, httpContext);
                if (!Thread.currentThread().isInterrupted()) {
                    if (response != null) {
                        responseHandler.sendResponseMessage(response);
                    }
                } else {
                    // SOME POTENTIAL BUGS
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    throw e;
                }
            }
        }
    }

    private void makeRequestWithExceptionsHandling() {
        try {
            makeRequest();
            return;
        } catch (IOException e) {
            Log.e(LOG_TAG, "IO Exception", e);
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(e, "IO Exception", null);
            }
        }
    }

    public boolean isCancelled() {
        if (isCancelled) {
        }

        return isCancelled;
    }

    public boolean isDone() {
        return isCancelled() || isFinished;
    }

    public boolean cancel(boolean mayInterrupting) {
        isCancelled = true;
        request.abort();
        return isCancelled();
    }
}

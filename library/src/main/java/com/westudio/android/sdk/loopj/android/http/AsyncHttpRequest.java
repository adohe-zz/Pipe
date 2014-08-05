package com.westudio.android.sdk.loopj.android.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class AsyncHttpRequest implements Runnable {

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
        if (isCancelled()) {
            return;
        }

        if (responseHandler != null) {
        }

        if (isCancelled()) {
            return;
        }


    }

    private void makeRequest() throws IOException {
        if (isCancelled()) {
            return;
        }

        if (request.getURI().getScheme() == null) {
            throw  new MalformedURLException("No valid URL schema was provided");
        }

        HttpResponse response = httpClient.execute(request, httpContext);

        if (!isCancelled() && responseHandler != null) {
            responseHandler.sendResponseMessage(response);
        }
    }

    private void makeRequestWithExceptionsHandling() {
        try {
            makeRequest();
            return;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {

        } catch (SocketTimeoutException e) {

        } catch (IOException e) {

        } catch (NullPointerException e) {

        } catch (Throwable t) {

        }
    }

    public boolean isCancelled() {
        if (isCancelled) {

        }

        return isCancelled;
    }

    public boolean isFinished() {
        return isCancelled || isFinished;
    }
}

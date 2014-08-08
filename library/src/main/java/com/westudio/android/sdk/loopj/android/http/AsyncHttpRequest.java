package com.westudio.android.sdk.loopj.android.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
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

    //TODO:ADD RESPONSE HANDLER SEND FAILURE MESSAGE
    private void makeRequestWithExceptionsHandling() {
        try {
            makeRequest();
            return;
        } catch (UnknownHostException e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(e, "can't resolve host", null);
            }
        } catch (SocketException e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(e, "can't resolve host", null);
            }
        } catch (SocketTimeoutException e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(e, "time out exception", null);
            }
        } catch (IOException e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(e, "io exception", null);
            }
        } catch (NullPointerException e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(e, "null pointer exception", null);
            }
        } catch (Throwable t) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(t, "connect exception", null);
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

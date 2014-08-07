package com.westudio.android.sdk.exceptions;

public class ServiceClientError extends Exception {

    public ServiceClientError(String msg) {
        super(msg);
    }

    public ServiceClientError(String msg, Throwable t) {
        super(msg, t);
    }

    public ServiceClientError(Throwable t) {
        super(t);
    }
}

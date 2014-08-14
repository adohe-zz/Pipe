package com.westudio.android.sdk.exceptions;

/**
 * Indicates that there was a network error when performing a Async Request
 */
public class NetworkError extends ServiceClientError {

    public NetworkError(Throwable cause) {
        super(cause);
    }
}

package com.westudio.android.sdk.http;

import com.westudio.android.sdk.exceptions.ServiceClientError;

public interface ServiceCallback<R> {

    void onResponse(R response);

    void onErrorResponse(ServiceClientError error);
}

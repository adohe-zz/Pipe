package com.westudio.android.sdk.loopj.android.http;

import android.content.Context;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHttpClient {

    private static final String LOG_TAG = "AsyncHttpClient";

    private static final int DEFAULT_MAX_CONNECTIONS = 10;
    private static final int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private int maxConnections = DEFAULT_MAX_CONNECTIONS;
    private int timeout = DEFAULT_SOCKET_TIMEOUT;

    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext;
    private ExecutorService threadPool;
    private final Map<String, String> clientHeaderMap;

    public AsyncHttpClient() {

        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, timeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

        HttpConnectionParams.setSoTimeout(httpParams, timeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_BUFFER_SIZE);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(httpParams, "");

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, registry);

        httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        httpClient = new DefaultHttpClient(cm, httpParams);
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {

            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {

            }
        });

        threadPool = getDefaultThreadPool();
        clientHeaderMap = new HashMap<String, String>();
    }

    protected ExecutorService getDefaultThreadPool() {
        return Executors.newCachedThreadPool();
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public ExecutorService getThreadPool() {
        return this.threadPool;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public void setMaxConnections(final int maxConnections) {
        if (maxConnections < 1)
            this.maxConnections = DEFAULT_MAX_CONNECTIONS;
        this.maxConnections = maxConnections;
        final HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(this.maxConnections));
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        if (timeout < 1000)
            this.timeout = DEFAULT_SOCKET_TIMEOUT;
        this.timeout = timeout;
        final HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, this.timeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, this.timeout);
        HttpConnectionParams.setSoTimeout(httpParams, this.timeout);
    }

    public void removeAllHeaders() {
        clientHeaderMap.clear();
    }

    public void addHeader(String header, String value) {
        clientHeaderMap.put(header, value);
    }

    public void removeHeader(String header) {
        clientHeaderMap.remove(header);
    }

    public void post(String url, AsyncHttpResponseHandler responseHandler) {

    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

    }

    public void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

    }

    public void post(Context context, String url, RequestParams params, String contentType, AsyncHttpResponseHandler responseHandler) {

    }

    public void post(Context context, String url, Header[] headers, RequestParams params, String contentType,
            AsyncHttpResponseHandler responseHandler) {

    }

    public void post(Context context, String url, Header[] headers, HttpEntity entity, String contentType,
            AsyncHttpResponseHandler responseHandler) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPost(URI.create(url).normalize()), entity);
        if (headers != null)
            request.setHeaders(headers);
        sendRequest(httpClient, httpContext, request, contentType, responseHandler, context);
    }

    protected void sendRequest(DefaultHttpClient httpClient, HttpContext httpContext, HttpUriRequest request, String contentType,
             AsyncHttpResponseHandler responseHandler, Context context) {
        if (request == null) {
            throw new IllegalArgumentException("HttpUriRequest must not be null");
        }

        if (responseHandler == null) {
            throw new IllegalArgumentException("AsyncHttpResponseHandler must not be null");
        }

        if (contentType != null) {
            request.addHeader(HEADER_CONTENT_TYPE, contentType);
        }

        AsyncHttpRequest asyncHttpRequest = newAsyncHttpRequest(httpClient, httpContext, request, responseHandler);
        threadPool.submit(asyncHttpRequest);

        if (context != null) {
        }
    }

    private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
        if (entity != null) {
            requestBase.setEntity(entity);
        }

        return requestBase;
    }

    protected AsyncHttpRequest newAsyncHttpRequest(DefaultHttpClient client, HttpContext context, HttpUriRequest request, AsyncHttpResponseHandler responseHandler) {
        return new AsyncHttpRequest(client, request, context, responseHandler);
    }
}

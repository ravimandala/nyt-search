package com.ravimandala.labs.nytimessearch.net;

import com.loopj.android.http.AsyncHttpClient;
import com.ravimandala.labs.nytimessearch.utils.Constants;

public class WebClient {
    private AsyncHttpClient client;

    public WebClient() {
        this.client = new AsyncHttpClient();
        this.client.setTimeout(30 * 1000);
    }

    private String getApiUrl(String relativeUrl) {
        return Constants.API_BASE_URL + relativeUrl;
    }
}

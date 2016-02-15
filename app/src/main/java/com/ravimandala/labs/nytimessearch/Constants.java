package com.ravimandala.labs.nytimessearch;

public interface Constants {
    String TAG = "NYTSearch";
    String API_BASE_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    String SEARCH_QUERY = "searchQuery";
    String RESULTS_PAGE = "resultsPage";

    int ARTS = 1 << 0;
    int FASHION_AND_STYLE = 1 << 1;
    int SPORTS = 1 << 2;
}

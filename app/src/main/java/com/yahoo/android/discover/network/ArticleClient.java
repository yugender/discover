package com.yahoo.android.discover.network;

import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yahoo.android.discover.models.SearchFilters;

import java.util.List;

/**
 * Created by yboini on 11/18/16.
 */

public class ArticleClient {
    private static String ARTICLE_SEARCH_API = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private static String API_KEY = "839ff0668df34266bfec9945be4bcb59";
    private AsyncHttpClient client;

    public ArticleClient() {
        this.client = new AsyncHttpClient();
        this.client.setTimeout(20 * 1000);
    }

    public void getArticles(String keyword, int page, SearchFilters filters, JsonHttpResponseHandler handler) {
        RequestParams requestParams = getRequestParams(keyword, page, filters);
        client.get(ARTICLE_SEARCH_API, requestParams, handler);
    }

    public RequestParams getRequestParams(String query, int page, SearchFilters filters) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("api-key", API_KEY);
        requestParams.add("q", query);
        if (page != 0) {
            requestParams.add("page", String.valueOf(page));
        }
        if (filters != null) {
            String beginDate = filters.getBeginDate();
            if (beginDate != null && !beginDate.isEmpty()) {
                requestParams.add("begin_date", filters.getBeginDate());
            }
            if (filters.getSortOrder() != null) {
                requestParams.add("sort", filters.getSortOrder().toString());
            }
            List<String> newsDeskValues = filters.getNewsDesks();
            if (newsDeskValues != null && !newsDeskValues.isEmpty()) {
                StringBuilder fqParam = new StringBuilder();
                fqParam.append("news_desk:(\"");
                fqParam.append(TextUtils.join("\"%20\"", newsDeskValues));
                fqParam.append("\")");
                requestParams.add("fq", fqParam.toString());
            }
        }
        return requestParams;
    }
}

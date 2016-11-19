package com.yahoo.android.discover.models;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by yboini on 11/16/16.
 */
@Parcel
public class SearchFilters {
    private String beginDate;
    private SortOrder sortOrder;
    private List<String> newsDesks;

    public SearchFilters() {

    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        if ("Oldest".equals(sortOrder)) {
            this.sortOrder = SortOrder.OLDEST;
        } else {
            this.sortOrder = SortOrder.NEWEST;
        }
    }

    public List<String> getNewsDesks() {
        return newsDesks;
    }

    public void setNewsDesks(List<String> newsDesks) {
        this.newsDesks = newsDesks;
    }

    public enum SortOrder {
        NEWEST, OLDEST
    }
}

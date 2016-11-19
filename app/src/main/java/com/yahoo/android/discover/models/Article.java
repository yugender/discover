package com.yahoo.android.discover.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yboini on 11/16/16.
 */

public class Article {
    private String title;
    private String imageUrl;
    private String url;

    public Article() {

    }

    public Article(String title) {
        this.title = title;
    }

    public Article(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static ArrayList<Article> getArticles(JSONObject response) {
        ArrayList<Article> articles = new ArrayList<>();
        JSONObject responseNode = response.optJSONObject("response");
        if (responseNode != null) {
            JSONArray docsNode = responseNode.optJSONArray("docs");
            if (docsNode != null) {
                for (int i = 0; i < docsNode.length(); i++) {
                    JSONObject doc = docsNode.optJSONObject(i);
                    if (doc != null) {
                        JSONObject headlineNode = doc.optJSONObject("headline");
                        JSONArray multimediaNode = doc.optJSONArray("multimedia");
                        String url = doc.optString("web_url");
                        if (headlineNode != null) {
                            String headline = headlineNode.optString("main");
                            if (headline != null && url != null) {
                                Article article = new Article(headline, url);
                                if (multimediaNode != null) {
                                    for (int j=0; j < multimediaNode.length(); j++) {
                                        JSONObject imageNode = multimediaNode.optJSONObject(j);
                                        if (imageNode != null) {
                                            if ("xlarge".equals(imageNode.optString("subtype")) ||
                                                    "xlarge".equals(imageNode.optString("subtype"))) {
                                                if (imageNode.optString("url") != null) {
                                                    article.setImageUrl("http://www.nytimes.com/" +
                                                            imageNode.optString("url"));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                                articles.add(article);
                            }
                        }
                    }
                }
            }
        }
        return articles;
    }

    public static ArrayList<Article> getArticles() {
        ArrayList<Article> articles = new ArrayList<>();
        Article article = new Article();
        article.setTitle("test title 1");
        articles.add(article);
        return articles;
    }
}

package com.yahoo.android.discover.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.android.discover.R;
import com.yahoo.android.discover.adapters.ArticleAdapter;
import com.yahoo.android.discover.databinding.ActivitySearchBinding;
import com.yahoo.android.discover.fragments.SearchFiltersDialogFragment;
import com.yahoo.android.discover.models.Article;
import com.yahoo.android.discover.models.SearchFilters;
import com.yahoo.android.discover.network.ArticleClient;
import com.yahoo.android.discover.network.NetworkStatus;
import com.yahoo.android.discover.utils.BitmapWorkerTask;
import com.yahoo.android.discover.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

public class ArticleSearchActivity extends AppCompatActivity implements SearchFiltersDialogFragment.OnFilterSearchListener{
    private static String TAG = ArticleSearchActivity.class.getSimpleName();
    ArticleClient client;
    List<Article> articles;
    ArticleAdapter adapter;
    SearchFilters mFilters;
    String mSearchQuery = null;
    private ActivitySearchBinding mBinding;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        client = new ArticleClient();
        // Lookup the recyclerview in activity layout
        RecyclerView rvArticles = mBinding.rvArticles;

        articles = new ArrayList<>();
        adapter = new ArticleAdapter(this, articles);
        // Attach the adapter to the recyclerview to populate items
        rvArticles.setAdapter(adapter);
        // Set layout manager to position the items
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);
        mFilters = new SearchFilters();
        adapter.setOnItemClickListener((view, position) -> {
            Article article = articles.get(position);
            if (article != null) {
                launchArticle(article.getUrl());
            }
        });
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, final RecyclerView view) {
                fetchArticles(mSearchQuery, page, mFilters, view);
            }
        };
        rvArticles.addOnScrollListener(scrollListener);
    }

    private void launchArticle(String url) {
        if (url != null) {
            Bitmap bitmap = null;
            try {
                bitmap = new BitmapWorkerTask(getBaseContext()).execute(R.drawable.ic_action_share).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, url);
            int requestCode = 100;

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            builder.setShowTitle(true);
            builder.addDefaultShareMenuItem();
            if (bitmap != null) {
                builder.setActionButton(bitmap, "Share Link", pendingIntent, true);
            }
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(url));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchQuery = query;
                fetchArticles(query, 0, mFilters, null);
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                articles.clear();
                adapter.notifyDataSetChanged();
                scrollListener.resetState();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void onSearchSettingsClick(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        SearchFiltersDialogFragment searchFiltersDialogFragment = SearchFiltersDialogFragment.newInstance(mFilters);
        searchFiltersDialogFragment.show(fm, "fragment_search_filters");
    }

    @Override
    public void onUpdateFilters(SearchFilters filters) {
        mFilters = filters;
    }

    private void fetchArticles(String query, int page, SearchFilters filters, RecyclerView view) {
        ArticleResponseHandler responseHandler;
        if (page != 0) {
            responseHandler = new ArticleResponseHandler(true, view);
        } else {
            responseHandler = new ArticleResponseHandler(false, view);
        }
        if (NetworkStatus.getInstance(getBaseContext()).isOnline()) {
            client.getArticles(query, page, filters, responseHandler);
        } else {
            Toast.makeText(getBaseContext(), "there is no network available", Toast.LENGTH_SHORT).show();
        }

    }

    private class ArticleResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "failure response: " + errorResponse);
        }

        private boolean isScrollRequest;
        RecyclerView view;
        ArticleResponseHandler(boolean isScrollRequest, RecyclerView view) {
            this.isScrollRequest = isScrollRequest;
            this.view = view;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            ArrayList<Article> articlesList = Article.getArticles(response);
            if (isScrollRequest) {
                if (!articlesList.isEmpty()) {
                    final int curSize = adapter.getItemCount();
                    articles.addAll(articlesList);
                    view.post(() -> adapter.notifyItemRangeInserted(curSize, articles.size() - 1));
                }
            } else {
                if (!articlesList.isEmpty()) {
                    int curSize = adapter.getItemCount();
                    articles.clear();
                    adapter.notifyItemRangeRemoved(0, curSize);
                    articles.addAll(articlesList);
                    adapter.notifyItemRangeInserted(0, articlesList.size());
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.d(TAG, "failure status code: " + String.valueOf(statusCode));
            Log.d(TAG, "error response: " + responseString);
            throwable.printStackTrace();
        }
    }
}

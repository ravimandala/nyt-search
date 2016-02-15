package com.ravimandala.labs.nytimessearch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ravimandala.labs.nytimessearch.R;
import com.ravimandala.labs.nytimessearch.adapter.ArticlesAdapter;
import com.ravimandala.labs.nytimessearch.utils.Constants;
import com.ravimandala.labs.nytimessearch.misc.DividerItemDecoration;
import com.ravimandala.labs.nytimessearch.misc.EndlessRecyclerViewScrollListener;
import com.ravimandala.labs.nytimessearch.model.Article;
import com.ravimandala.labs.nytimessearch.model.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    private static final int SETTINGS = 101;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.etQuery)
    EditText etQuery;

    @Bind(R.id.btnSearch)
    Button btnSearch;

    @Bind(R.id.rvRecyclerView)
    RecyclerView rvResults;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<Article> articles = new ArrayList<>();
    ArticlesAdapter adapter;
    String query;
    int currPage;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        adapter = new ArticlesAdapter(articles);
        rvResults.setAdapter(adapter);
        rvResults.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                getResources().getConfiguration().orientation));
//        rvResults.setItemAnimator(new SlideInUpAnimator());  // Resulting in runtime exceptions
        final StaggeredGridLayoutManager lmStaggeredGrid =
                new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        rvResults.setLayoutManager(lmStaggeredGrid);
        rvResults.setOnScrollListener(new EndlessRecyclerViewScrollListener(lmStaggeredGrid) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                currPage = page;
                customLoadDataFromApi(page);
            }
        });
        etQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnSearch.callOnClick();
                    return true;
                }
                return false;
            }
        });
        adapter.setOnItemClickListener(new ArticlesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent i = new Intent(itemView.getContext(), ArticleActivity.class);
                i.putExtra("article", articles.get(position));
                startActivity(i);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                btnSearch.callOnClick();
            }
        });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (savedInstanceState != null) {
            settings = savedInstanceState.getParcelable("settings");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(Constants.TAG, "Saving instance");
        outState.putString(Constants.SEARCH_QUERY, query);
        outState.putInt(Constants.RESULTS_PAGE, currPage);
        outState.putParcelable(Constants.SETTINGS, settings);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(Constants.TAG, "Restoring saved instance");

        etQuery.setText(savedInstanceState.getString(Constants.SEARCH_QUERY));
        currPage = savedInstanceState.getInt(Constants.RESULTS_PAGE);
        settings = savedInstanceState.getParcelable(Constants.SETTINGS);
        btnSearch.callOnClick();
    }

    private void customLoadDataFromApi(int page) {
        RequestParams params = new RequestParams();
        params.add("q", query);
        params.add("api-key", getResources().getString(R.string.api_key));
        params.add("page", String.valueOf(page));
        if (settings != null) {
            params.add("begin_date", new SimpleDateFormat("yyyyMMdd").format(settings.getBeginDate().getTime()));  // YYYYMMDD
            params.add("sort", settings.isOldestFirst() ? "oldest" : "newest");
            int newsDeskValues = settings.getNewsDeskValues();
            if (newsDeskValues != 0) {
                StringBuilder sb = new StringBuilder();

                if ((newsDeskValues & Constants.ARTS) != 0)
                    sb.append(" \"Arts\" ");
                if ((newsDeskValues & Constants.FASHION_AND_STYLE) != 0)
                    sb.append(" \"Fashion\" ");
                if ((newsDeskValues & Constants.SPORTS) != 0)
                    sb.append(" \"Sports\" ");
                params.add("fq", "news_desk:( " + sb.toString() + " )");
            }
        }

        Log.d(Constants.TAG, "Base URL = " + Constants.API_BASE_URL);
        Log.d(Constants.TAG, "Params = " + params.toString());
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Constants.API_BASE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonResults = null;
                int currSize = articles.size();

                Log.d(Constants.TAG, "Got the response with resultCode: " + statusCode);
                try {
                    if (response.optJSONObject("response") != null) {
                        adapter.addAll(Article.fromJSONArray(response.getJSONObject("response").getJSONArray("docs")));
                        // For efficiency purposes, notify the adapter of only the elements that got changed
                        // curSize will equal to the index of the first element inserted because the list is 0-indexed
                        adapter.notifyItemRangeInserted(currSize, articles.size() - 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(Constants.TAG, articles.toString());
                swipeRefreshLayout.setRefreshing(false);
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        query = etQuery.getText().toString();

        currPage = 0;
        adapter.clear();
        customLoadDataFromApi(currPage);
    }

    public void settingsClicked(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("settings", settings);
        startActivityForResult(intent, SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS && resultCode == RESULT_OK) {
            settings = data.getParcelableExtra("settings");
        }
    }
}

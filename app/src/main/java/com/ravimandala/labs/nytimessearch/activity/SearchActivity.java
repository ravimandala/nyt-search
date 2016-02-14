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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ravimandala.labs.nytimessearch.R;
import com.ravimandala.labs.nytimessearch.adapter.ArticlesAdapter;
import com.ravimandala.labs.nytimessearch.adapter.EndlessRecyclerViewScrollListener;
import com.ravimandala.labs.nytimessearch.model.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "NYTSearch";
    private static final int SETTINGS = 101;
    private static final String API_BASE_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private static final String SEARCH_QUERY = "searchQuery";
    private static final String RESULTS_PAGE = "resultsPage";

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
    Calendar cal = Calendar.getInstance();
    String sortOrder;
    String query;
    int currPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        adapter = new ArticlesAdapter(articles);
        rvResults.setAdapter(adapter);
//        rvResults.setItemAnimator(new SlideInUpAnimator());
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

        currPage = 0;
        cal.setTime(new Date());
        sortOrder = "Newest";
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_QUERY, query);
        outState.putInt(RESULTS_PAGE, currPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        etQuery.setText(savedInstanceState.getString(SEARCH_QUERY));
        currPage = savedInstanceState.getInt(RESULTS_PAGE);
        btnSearch.callOnClick();
    }

    private void customLoadDataFromApi(int page) {
        RequestParams params = new RequestParams();
        params.add("q", query);
        params.add("api-key", getResources().getString(R.string.api_key));
        params.add("page", String.valueOf(page));

        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_BASE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonResults = null;
                int currSize = articles.size();

                Log.d(TAG, "Got the response with resultCode: " + statusCode);
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
                Log.d(TAG, articles.toString());
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

         adapter.clear();
        customLoadDataFromApi(currPage);
    }

    public void settingsClicked(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);

        intent.putExtra("begin_date_year", cal.get(Calendar.YEAR));
        intent.putExtra("begin_date_month", cal.get(Calendar.MONTH));
        intent.putExtra("begin_date_day", cal.get(Calendar.DAY_OF_MONTH));
        intent.putExtra("sort_order", sortOrder);

        startActivityForResult(intent, SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS && resultCode == RESULT_OK) {
            cal.set(data.getIntExtra("begin_date_year", 2016),
                    data.getIntExtra("begin_date_month", 1),
                    data.getIntExtra("begin_date_day", 1));
            Log.d(TAG, "Date: " + cal.getTime());
            sortOrder = data.getStringExtra("sort_order");
        }
    }
}

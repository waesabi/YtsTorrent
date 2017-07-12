package kumarsanket.torrentmovie;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ThreeDMovieActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    public RecyclerView recyclerView;
    public SwipeRefreshLayout refreshLayout;

    private ProgressBar progressBar;
    private TextView waitTextView ;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 20;
    private static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private RelativeLayout relativeLayout;

    private static final String TAG = "ThreeDMovieActivity";
    PaginationAdapter adapter;
    GridLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_dmovie);

        relativeLayout = (RelativeLayout)findViewById(R.id.parent_view);
        recyclerView = (RecyclerView)findViewById(R.id.three_d_recycler_view);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.three_d_refresh_layout);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        waitTextView = (TextView)findViewById(R.id.wait_a_second);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("3D Movie");

        adapter = new PaginationAdapter(this,this,recyclerView);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage = currentPage+1;
                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                },1000);

            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        // Lookup the swipe container view
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                currentPage = 1;
                refreshData();

            }
        });

//        setUpEnterTransition();

        // Configure the refreshing colors
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // mocking network delay for API call
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFirstPage();
            }
        }, 1000);


    }

    public void setUpEnterTransition()
    {
        if(Build.VERSION.SDK_INT >= 21)
        {
            Slide slideTransition = new Slide();
            slideTransition.setDuration(350);
            slideTransition.setSlideEdge(Gravity.LEFT);
            TransitionManager.beginDelayedTransition(relativeLayout,slideTransition);
            // set background color while sliding
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            getWindow().setExitTransition(slideTransition);
            getWindow().setReenterTransition(slideTransition);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home :
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData()
    {
        getSupportLoaderManager().restartLoader(0,null,this);
        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }


    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");
        progressBar.setVisibility(View.VISIBLE);
        waitTextView.setVisibility(View.VISIBLE);
        getSupportLoaderManager().initLoader(0,null,this);
        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;

    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        adapter.removeLoadingFooter();
        isLoading = false;
        getSupportLoaderManager().restartLoader(0,null,this);
        if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }


    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            public ArrayList<Movie> movieData = null;


            @Override
            protected void onStartLoading() {
                if(movieData!=null)
                {
                    deliverResult(movieData);
                }
                else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                movieData = data;
                super.deliverResult(data);

            }

            @Override
            public ArrayList<Movie> loadInBackground() {

                //  url ---- https://yts.ag/api/v2/list_movies.json?page=12&quality=3D
                String url = "https://yts.ag/api/v2/list_movies.json?page="+currentPage+"&quality=3D";
                Log.v("URL ",url);
                try {
                    String jsonString = NetworkUtilities.getResponseFromHttpUrl(url);
                    return NetworkUtilities.extractMovie(jsonString);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        if(data!=null)
        {
            refreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.INVISIBLE);
            waitTextView.setVisibility(View.INVISIBLE);
            adapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }
}

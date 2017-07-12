package kumarsanket.torrentmovie.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import kumarsanket.torrentmovie.Movie;
import kumarsanket.torrentmovie.NetworkUtilities;
import kumarsanket.torrentmovie.PaginationAdapter;
import kumarsanket.torrentmovie.PaginationScrollListener;
import kumarsanket.torrentmovie.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WarMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    public RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView waitTextView;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 14;
    private int currentPage = PAGE_START;

    private static final String TAG = "ActionMovieFragment";

    PaginationAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    GridLayoutManager layoutManager;
    LinearLayoutManager linearLayoutManager ;

    public WarMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_action_movie, container, false);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        waitTextView = (TextView)view.findViewById(R.id.wait_a_second);
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        adapter = new PaginationAdapter(getActivity(),getContext(),recyclerView);
        layoutManager = new GridLayoutManager(getContext(),1);
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
                }, 1000);

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
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                currentPage = 1;
                progressBar.setVisibility(View.INVISIBLE);
                waitTextView.setVisibility(View.INVISIBLE);
                refreshData();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
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

        return view;
    }


    private void refreshData()
    {
        getLoaderManager().restartLoader(0,null,this);
        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }


    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");
        getLoaderManager().initLoader(0,null,this);
        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;

    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        adapter.removeLoadingFooter();
        isLoading = false;
        getLoaderManager().restartLoader(0,null,this);
        if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }


    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(getContext()) {
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

                //  url ---- https://yts.ag/api/v2/list_movies.json?page=3&genre=Film-Noir
                String url = "https://yts.ag/api/v2/list_movies.json?page="+currentPage+"&genre=War";
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
            swipeContainer.setRefreshing(false);
            progressBar.setVisibility(View.INVISIBLE);
            waitTextView.setVisibility(View.INVISIBLE);
            adapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

}

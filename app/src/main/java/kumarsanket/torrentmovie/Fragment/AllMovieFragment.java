package kumarsanket.torrentmovie.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.RelativeLayout;
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
public class AllMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{


    public RecyclerView recyclerView;
    public RelativeLayout relativeLayout;
    private ProgressBar progressBar;
    private TextView waitTextView ;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 20;
    // 300
    private int currentPage = PAGE_START;

    private SwipeRefreshLayout swipeContainer;
    private static final String TAG = "AllMovieFragment";

    PaginationAdapter adapter;
    GridLayoutManager layoutManager;
    LinearLayoutManager linearLayoutManager ;
    private ArrayList<Movie> movieArrayList ;

    public AllMovieFragment() {
        // Required empty public constructor
        movieArrayList = new ArrayList<>();
    }

    //  network change listener
    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras()!=null)
            {
                // check network connection
                ConnectivityManager cm =
                        (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected)
                {
                    swipeContainer.setRefreshing(false);
                    progressBar.setVisibility(View.VISIBLE);
                    waitTextView.setVisibility(View.VISIBLE);
                    loadFirstPage();

                }
                else
                {
                    swipeContainer.setRefreshing(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    waitTextView.setVisibility(View.INVISIBLE);
                    showSnackBar();
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(networkReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(networkReceiver);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_action_movie, container, false);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        relativeLayout = (RelativeLayout)view.findViewById(R.id.relative_layout);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        waitTextView = (TextView)view.findViewById(R.id.wait_a_second);
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        adapter = new PaginationAdapter(getActivity(),getContext(),recyclerView);
        layoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);

        movieArrayList = new ArrayList<>();

        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage = currentPage+1;

                Log.v("Current page is : ",currentPage+"");
                // mocking network delay for API call
                loadNextPage();

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
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadFirstPage();
//            }
//        }, 1000);

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

                String url = "https://yts.ag/api/v2/list_movies.json?page="+currentPage;
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
            movieArrayList = data;
            swipeContainer.setRefreshing(false);
            waitTextView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            adapter.addAll(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }


    public void showSnackBar()
    {
        Snackbar.make(relativeLayout,"Check your network connection ",Snackbar.LENGTH_INDEFINITE)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();

    }



}

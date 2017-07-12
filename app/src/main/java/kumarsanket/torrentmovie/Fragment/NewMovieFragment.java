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
import kumarsanket.torrentmovie.NewMovieAdapter;
import kumarsanket.torrentmovie.R;

import static kumarsanket.torrentmovie.R.id.swipeContainer;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView loadingText;
    private NewMovieAdapter adapter;
    private GridLayoutManager layoutManager;
    private RelativeLayout relativeLayout;

    public NewMovieFragment() {
        // Required empty public constructor
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
                    loadData();
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    loadingText.setVisibility(View.INVISIBLE);
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
        View view = inflater.inflate(R.layout.fragment_new_movie, container, false);

        /**
         *  get reference to view
         */
        relativeLayout = (RelativeLayout)view.findViewById(R.id.relative_layout);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(swipeContainer);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        loadingText = (TextView)view.findViewById(R.id.loading_text);

        // adapter
        adapter = new NewMovieAdapter(getContext(),getActivity(),recyclerView);
        layoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);


        // Configure the refreshing colors
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingText.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                refreshData();
            }
        });
        return view;
    }


    public void loadData()
    {
        /**
         *  load data
         */
        getLoaderManager().initLoader(0,null,this);
    }

    public void refreshData()
    {
        adapter.clearDate();
        getLoaderManager().restartLoader(0,null,this);
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

                String url = "https://yts.ag/api/v2/list_movies.json";
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
            loadingText.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            adapter.setMovieList(data);
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

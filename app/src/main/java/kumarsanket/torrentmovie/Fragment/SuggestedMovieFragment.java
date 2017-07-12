package kumarsanket.torrentmovie.Fragment;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
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
import kumarsanket.torrentmovie.R;
import kumarsanket.torrentmovie.SuggestMovieAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuggestedMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{


    public RecyclerView recyclerView ;
    public String movieId ;
    public SuggestMovieAdapter adapter;
    public ProgressBar progressBar;
    public TextView textView;
    public SuggestedMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggested_movie, container, false);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        textView = (TextView)view.findViewById(R.id.suggestion_text_id);
        recyclerView = (RecyclerView)view.findViewById(R.id.suggested_movie_recycler_view);
        adapter = new SuggestMovieAdapter(getActivity(),getContext(),recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        /**
         *    inside scrollingView recycler view in not smooth so use below code
         */
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        getLoaderManager().initLoader(1,null,this);
        if(!isNetworkAvailable())
        {
            textView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
        return view;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void setMovieId(String id)
    {
        movieId = id;
        Log.e("movie-id",movieId+"");
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

                Log.e("movie-id2",movieId+"");
                //  url ---- https://yts.ag/api/v2/list_movies.json?page=3&genre=Film-Noir
                String url = "https://yts.ag/api/v2/movie_suggestions.json?movie_id="+movieId;
                Log.v("URL ",url);
                try {
                    String jsonString = NetworkUtilities.getResponseFromHttpUrl(url);
                    return NetworkUtilities.extractSuggestMovie(jsonString);

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
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            adapter.setMovieData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }
}

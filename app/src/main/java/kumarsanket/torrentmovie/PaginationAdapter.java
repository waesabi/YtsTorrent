package kumarsanket.torrentmovie;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;

import java.util.ArrayList;

/**
 * Created by sanketkumar on 21/05/17.
 */

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int NATIVE_AD = 2;
    private ArrayList<Movie> movies;
    private Context context;
    private RecyclerView recyclerView;
    public Activity activity;

    private VideoController videoController;
    private RecyclerViewAnimator mAnimator;

    // for progress footer
    private boolean isLoadingAdded = false;

    public PaginationAdapter(Activity activity1,Context context,RecyclerView recyclerView1) {
        this.context = context;
        activity = activity1;
        movies = new ArrayList<>();
        recyclerView = recyclerView1;
        mAnimator = new RecyclerViewAnimator(recyclerView);
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;

    }
    public ArrayList<Movie> getMovies() {
        return movies;
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case ITEM:
                View v1 = inflater.inflate(R.layout.list_item, parent, false);
                /**
                 * First item's entrance animations.
                 */
                mAnimator.onCreateViewHolder(v1);
                viewHolder = getViewHolder(parent, inflater);
                break;
            case NATIVE_AD :
                View v2 = inflater.inflate(R.layout.native_ad,parent,false);
                viewHolder = new NativeAdHolder(v2);
                break;
            case LOADING:
                View v3 = inflater.inflate(R.layout.item_loading, parent, false);
                viewHolder = new LoadingViewHolder(v3);
                break;
        }

        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_item, parent, false);
        viewHolder = new movieViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final Movie movie = movies.get(position);
        switch (getItemViewType(position))
        {
            case ITEM :
                movieViewHolder viewHolder = (movieViewHolder)holder;
                /**
                 *  use placeholder
                 */
                Glide.with(context)
                        .load(movie.getLarge_image_cover())
                        .into(((movieViewHolder) holder).imageView);
                viewHolder.nameView.setText("Name : "+movie.getMovie_name());
                viewHolder.ratingView.setText("Rating : "+movie.getMovie_rating());
                viewHolder.yearView.setText("Year : "+movie.getMovie_year());

                String genre_string,genre = "";
                genre_string = movie.getMovie_genre();

                for(int i=0;i<genre_string.length();i++)
                {
                    char x = genre_string.charAt(i);
                    if(x!= '['&& x!='"'&& x!=']')
                    {
                        if(x==',')
                        {
                            genre = genre+" ";
                        }
                        genre = genre+x;
                        if(x==',')
                        {
                            genre = genre+" ";
                        }

                    }
                }
                viewHolder.genreView.setText("Genre : "+genre);

                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,SecondMovieDetailActivity.class);
                        Log.e("total Movies ",movies.size()+"");
                        Log.e("MovieQuality" , movie.getMovieQualities().size()+"");
                        intent.putExtra(Intent.EXTRA_TEXT,movie);
                        if(Build.VERSION.SDK_INT > 21)
                        {
                            v.getContext().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                        }
                        else {
                            v.getContext().startActivity(intent);
                        }
                    }
                });

                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,SecondMovieDetailActivity.class);
                        Log.e("total Movies ",movies.size()+"");
                        Log.e("MovieQuality" , movie.getMovieQualities().size()+"");
                        intent.putExtra(Intent.EXTRA_TEXT,movie);
                        if(Build.VERSION.SDK_INT > 21)
                        {
                            // use pair for shared element transition
                            Pair pair = Pair.create(((movieViewHolder) holder).imageView,"shared_element");
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity);
                            v.getContext().startActivity(intent, options.toBundle());
                        }
                        else
                        {
                            v.getContext().startActivity(intent);
                        }

                    }
                });



                /**
                 * Item's entrance animations during scroll are performed here.
                 */
                mAnimator.onBindViewHolder(viewHolder.itemView, position);
                break;
            case NATIVE_AD :
                NativeAdHolder adHolder = (NativeAdHolder) holder;
                MobileAds.initialize(context,context.getResources().getString(R.string.admob_native_ad));
                //Load the Ad
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice("7EC37977E212E01AA58D851FCFFD4898")
                        .build();
                adHolder.adView.loadAd(adRequest);
                /**
                 * Item's entrance animations during scroll are performed here.
                 */
                mAnimator.onBindViewHolder(adHolder.itemView, position);
                break;
            case LOADING :
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
                break;

        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position%5 == 1)
        {
            /**
             *  check for size()-1
             */
            return (position == movies.size()-1) ? LOADING : NATIVE_AD;
//            return NATIVE_AD;
        }
        else
        {
            return (position == movies.size()-1 && isLoadingAdded) ? LOADING : ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0:movies.size();
    }


    /*
   View Holders
   _________________________________________________________________________________________________
    */

    public static class movieViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView ;
        public CardView cardView;
        public TextView nameView,ratingView,genreView,yearView;

        public movieViewHolder(View view)
        {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image_view);
            cardView = (CardView)view.findViewById(R.id.card_view);
            nameView = (TextView)view.findViewById(R.id.name_view);
            ratingView = (TextView)view.findViewById(R.id.rating_view);
            genreView =(TextView)view.findViewById(R.id.genre_view);
            yearView = (TextView)view.findViewById(R.id.year_view);
        }

    }


    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;
        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }


    private class NativeAdHolder extends RecyclerView.ViewHolder {

        public NativeExpressAdView adView;
        public NativeAdHolder(View view) {
            super(view);
            adView = (NativeExpressAdView) view.findViewById(R.id.nativeAdView);
        }
    }



    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Movie mc) {
        movies.add(mc);
        notifyItemInserted(movies.size() - 1);
    }

    public void addAll(ArrayList<Movie> mcList) {
        for (Movie mc : mcList) {
            add(mc);
        }
    }

    public void remove(Movie city) {
        int position = movies.indexOf(city);
        if (position > -1) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
//        add(new Movie());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
//        int position = movies.size() - 1;
//        Movie item = getItem(position);
//        if (item != null) {
//            movies.remove(position);
//            notifyItemRemoved(position);
//        }
    }

    public Movie getItem(int position) {
        return movies.get(position);
    }


}

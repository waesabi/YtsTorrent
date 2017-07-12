package kumarsanket.torrentmovie;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;

/**
 * Created by sanketkumar on 09/07/17.
 */

public class NewMovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Movie> movieArrayList;
    private Context mContext;

    private static final int ITEM = 0;
    private static final int NATIVE_AD = 1;
    private RecyclerViewAnimator mAnimator;
    private RecyclerView mRecyclerView;
    public Activity mActivity;

    /**
     *  constructor
     * @param context
     */
    public NewMovieAdapter(Context context,Activity activity,RecyclerView recyclerView)
    {
        mContext = context;
        mActivity = activity;
        mRecyclerView = recyclerView;
        movieArrayList = new ArrayList<>();
        mAnimator = new RecyclerViewAnimator(mRecyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType)
        {
            case ITEM :
                View v1 = inflater.inflate(R.layout.list_item, parent, false);
                /**
                 * First item's entrance animations.
                 */
                mAnimator.onCreateViewHolder(v1);
                viewHolder = new MovieViewHolder(v1);
                break;
            case NATIVE_AD :
                View v2 = inflater.inflate(R.layout.native_ad,parent,false);
                viewHolder = new NativeAdHolder(v2);
                break;
        }
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Movie movie = movieArrayList.get(position);
        switch (getItemViewType(position))
        {
            case ITEM :
                MovieViewHolder viewHolder = (MovieViewHolder)holder;
                /**
                 *  use placeholder
                 */
                Glide.with(mContext)
                        .load(movie.getLarge_image_cover())
                        .into(((MovieViewHolder) holder).imageView);
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
                        Intent intent = new Intent(mContext,SecondMovieDetailActivity.class);
                        Log.e("total Movies ",movieArrayList.size()+"");
                        Log.e("MovieQuality" , movie.getMovieQualities().size()+"");
                        intent.putExtra(Intent.EXTRA_TEXT,movie);
                        if(Build.VERSION.SDK_INT > 21)
                        {
                            v.getContext().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(mActivity).toBundle());
                        }
                        else {
                            v.getContext().startActivity(intent);
                        }
                    }
                });

                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext,SecondMovieDetailActivity.class);
                        Log.e("total Movies ",movieArrayList.size()+"");
                        Log.e("MovieQuality" , movie.getMovieQualities().size()+"");
                        intent.putExtra(Intent.EXTRA_TEXT,movie);
                        if(Build.VERSION.SDK_INT > 21)
                        {
                            // use pair for shared element transition
                            // Pair pair = Pair.create(((MovieViewHolder) holder).imageView,"shared_element");
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mActivity);
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
                MobileAds.initialize(mContext,mContext.getResources().getString(R.string.admob_native_ad));
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

        }
    }

    @Override
    public int getItemCount() {
        return movieArrayList == null ? 0 : movieArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position%6 == 3 ? NATIVE_AD : ITEM;
    }


    class MovieViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView ;
        public CardView cardView;
        public TextView nameView,ratingView,genreView,yearView;
        public MovieViewHolder(View view)
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

    private class NativeAdHolder extends RecyclerView.ViewHolder {

        public NativeExpressAdView adView;
        public NativeAdHolder(View view) {
            super(view);
            adView = (NativeExpressAdView) view.findViewById(R.id.nativeAdView);
        }
    }

    public void setMovieList(ArrayList<Movie> movieList)
    {
        movieArrayList = movieList;
        notifyDataSetChanged();
    }

    public void clearDate()
    {
        movieArrayList = null;
        notifyDataSetChanged();
    }


}

package kumarsanket.torrentmovie;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by sanketkumar on 14/06/17.
 */

public class SuggestMovieAdapter extends RecyclerView.Adapter<SuggestMovieAdapter.ViewHolder>{


    public Context mContext;
    public ArrayList<Movie> arrayMovieList;

    // spring bound animation
    private RecyclerViewAnimator mAnimator;
    private RecyclerView mRecyclerView ;
    public Activity mActivity;

    public SuggestMovieAdapter(Activity activity,Context context,RecyclerView recyclerView)
    {
        mContext = context;
        mRecyclerView = recyclerView;
        mActivity = activity;
        mAnimator = new RecyclerViewAnimator(recyclerView);
    }

    @Override
    public SuggestMovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.suggest_movie_list,parent,false);
        /**
         * First item's entrance animations.
         */
//        mAnimator.onCreateViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SuggestMovieAdapter.ViewHolder holder, int position) {
        final Movie movie = arrayMovieList.get(position);
        Glide.with(mContext).load(movie.getMedium_image_cover()).into(holder.imageview);
        holder.nameText.setText("Name : "+movie.getMovie_name());

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,SuggestedMovieDetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT,movie);
                if(Build.VERSION.SDK_INT>21)
                {
                    v.getContext().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(mActivity).toBundle());
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
//        mAnimator.onBindViewHolder(holder.itemView, position);

    }

    public String getMovieGenre(String genre)
    {
        genre = genre.replace("[","");
        genre = genre.replace("]","");
        genre = genre.replace("\""," ");
        return genre;
    }

    @Override
    public int getItemCount() {
        return arrayMovieList == null ? 0:arrayMovieList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public CardView cardview;
        public ImageView imageview;
        public TextView nameText ;
        public ViewHolder(View itemView)
        {
            super(itemView);
            cardview = (CardView)itemView.findViewById(R.id.card_view);
            imageview = (ImageView)itemView.findViewById(R.id.suggest_movie_image);
            nameText = (TextView)itemView.findViewById(R.id.name_text);

        }
    }


    public void setMovieData(ArrayList<Movie> movieList)
    {
        arrayMovieList = movieList;
        notifyDataSetChanged();
    }


}

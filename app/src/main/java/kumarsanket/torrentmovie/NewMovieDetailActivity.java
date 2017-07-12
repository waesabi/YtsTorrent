package kumarsanket.torrentmovie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static kumarsanket.torrentmovie.R.id.trailer_card_view;

public class NewMovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    public ImageView movie_image_view,trailer_thumbnail;
    public TextView movie_name_view,movie_description_view,genre_text;
    public Toolbar toolbar;
    public FloatingActionButton floatingActionButton;
    public CoordinatorLayout coordinatorLayout;
    public RecyclerView recyclerView;
    public String[] torrentMovieQuality ;
    public String[] magnetUrlArray ;
    private AlertDialog myDialog;
    public String[] torrentFileDownload ;
    public String imdbUrl = "";
    public String movieId ;
    public SuggestMovieAdapter adapter;
    public ProgressBar progressBar;
    public TextView waitView;
    public RelativeLayout circular_reveal_layout;
    public Button download_movie,download_torrent_file,imdb_homepage,yts_homepage;
    public CardView trailer_card;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_movie_detail);

        /**
         *  set toolbar
         */
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        /**
         *  get reference to views
         */
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinate_layout);
        movie_image_view = (ImageView)findViewById(R.id.movie_image);
        movie_name_view = (TextView)findViewById(R.id.movie_name);
        genre_text = (TextView)findViewById(R.id.movie_genre);
        movie_description_view = (TextView)findViewById(R.id.description_view);
        trailer_thumbnail = (ImageView)findViewById(R.id.trailer_thumbnail);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
//        waitView = (TextView)findViewById(R.id.wait_view);
        circular_reveal_layout = (RelativeLayout) findViewById(R.id.relative_layout_circular);
        download_movie = (Button)findViewById(R.id.download_movie);
//        download_torrent_file = (Button)findViewById(R.id.download_torrent_file);
        imdb_homepage = (Button)findViewById(R.id.imdb_homepage);
        yts_homepage = (Button)findViewById(R.id.yts_homepage);
        trailer_card = (CardView)findViewById(trailer_card_view);

        Intent intent = getIntent();
        final Movie movie = (Movie) intent.getSerializableExtra(Intent.EXTRA_TEXT);
        int length = movie.getMovieQualities().size();
        torrentMovieQuality = new String[length];
        magnetUrlArray = new String[length];
        torrentFileDownload = new String[length];


        trailer_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TrailerActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, movie.getMovie_trailer_code());
                startActivity(intent);
            }
        });


        /**
         *  set magnet urls
         */
        for(int i=0;i<length;i++)
        {
            MovieQuality movieQuality = movie.getMovieQualities().get(i);
            torrentMovieQuality[i] = movieQuality.getQuality()+", "+movieQuality.getSize();
            torrentFileDownload[i] = movieQuality.getUrl();
            magnetUrlArray[i] = "magnet:?xt=urn:btih:"+movieQuality.getHash()+"&dn=\n" +
                    movie.getMovie_slug()+"&tr=udp://tracker.openbittorrent.com:80&tr=udp://tracker.publicbt.com:80&tr=\n" +
                    "       udp://tracker.istole.it:80&tr=udp://open.demonii.com:80&tr=udp://tracker.coppersurfer.tk:80";
        }

        /**
         *  set movie name and genre
         */
        Glide.with(this).load(movie.getLarge_image_cover())
                .into(movie_image_view);
        movie_name_view.setText("Name : "+movie.getMovie_name());
        movie_description_view.setText(movie.getMovie_full_description());
        movieId = movie.getMovie_id();

        final String thumbnail_url = "https://img.youtube.com/vi/"+movie.getMovie_trailer_code()+"/0.jpg";
        imdbUrl = "http://www.imdb.com/title/"+movie.getMovie_imdb_code();

        Glide.with(this).load(thumbnail_url)
                .into(trailer_thumbnail);

        genre_text.setText("Genre : "+getMovieGenre(movie.getMovie_genre()));

        setupWindowAnimations();



        adapter = new SuggestMovieAdapter(this,this,recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(1,null,this);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(circular_reveal_layout.getVisibility() == View.INVISIBLE)
                {
                    doCircularRevealAnimation();
                }
                else
                {
                    doExitRevealAnimation();
                }
            }
        });


        circular_reveal_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExitRevealAnimation();
            }
        });

        download_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTorrentDialog();
                myDialog.show();
                doExitRevealAnimation();
            }
        });

        imdb_homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.imdb.com/title/"+movie.getMovie_imdb_code();
                openBrowser(url);
                doExitRevealAnimation();
            }
        });

        yts_homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = movie.getMovie_url();
                openBrowser(url);
                doExitRevealAnimation();
            }
        });



    }

    public void  openBrowser(String url)
    {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        try {
            customTabsIntent.launchUrl(this, Uri.parse(url));
        }
        catch (ActivityNotFoundException a)
        {
            a.printStackTrace();
        }
    }


    public void createTorrentDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewMovieDetailActivity.this);
        builder.setTitle("Choose movie Quality");
        builder.setItems(torrentMovieQuality, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String  url = magnetUrlArray[which];
                String  torrentFile = torrentFileDownload[which];
                openTorrent(url,torrentFile);
            }
        });
        builder.setCancelable(true);
        myDialog = builder.create();
    }


    public void openTorrent(String url,final String torrentFile)
    {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        try {
            customTabsIntent.launchUrl(this, Uri.parse(url));
        }
        catch (ActivityNotFoundException a)
        {
            a.printStackTrace();
            new MaterialDialog.Builder(this)
                    .title("No App Found")
                    .content("Please Install BitTorrent or µTorrent . would you like to download torrent file ?")
                    .titleColor(Color.WHITE)
                    .contentColor(getResources().getColor(R.color.background_color))
                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                    .positiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            downloadTorrentFile(torrentFile);
                        }
                    })
                    .negativeText("Cancel")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        }
                    })
                    .show();
        }
    }


    public void downloadTorrentFile(String url)
    {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        try {
            customTabsIntent.launchUrl(this, Uri.parse(url));
        }
        catch (ActivityNotFoundException a)
        {
            a.printStackTrace();
        }
    }


    public void doCircularRevealAnimation()
    {
        int centerX =  circular_reveal_layout.getWidth();
        int centerY = circular_reveal_layout.getHeight();
        int startRadius = 0;
        int endRadius = (int) Math.hypot(centerX*2,centerY*2);

        if(Build.VERSION.SDK_INT >= 21)
        {
            Animator animator = ViewAnimationUtils.createCircularReveal(circular_reveal_layout,
                    centerX,
                    centerY,
                    startRadius,
                    endRadius);
            animator.setDuration(500);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if(Build.VERSION.SDK_INT>=21) {
                        Fade fadeTransition = new Fade();
                        fadeTransition.setDuration(800);
                        TransitionManager.beginDelayedTransition(circular_reveal_layout, fadeTransition);
                    }
                    download_movie.setVisibility(View.VISIBLE);
//                    download_torrent_file.setVisibility(View.VISIBLE);
                    imdb_homepage.setVisibility(View.VISIBLE);
                    yts_homepage.setVisibility(View.VISIBLE);
                }
            });
            circular_reveal_layout.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.INVISIBLE);
            animator.start();
        }

    }


    public void doExitRevealAnimation()
    {
        int centerX =  circular_reveal_layout.getWidth();
        int centerY = circular_reveal_layout.getHeight();
        int endRadius = 0;
        int startRadius = (int) Math.hypot(centerX*2,centerY*2);

        if(Build.VERSION.SDK_INT>=21)
        {
            Animator animator = ViewAnimationUtils.createCircularReveal(circular_reveal_layout,
                    centerX,
                    centerY,
                    startRadius,
                    endRadius);
            animator.setDuration(500);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    circular_reveal_layout.setVisibility(View.INVISIBLE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if(Build.VERSION.SDK_INT>=21) {
                        Slide slideTransition = new Slide();
                        slideTransition.setSlideEdge(Gravity.RIGHT);
                        TransitionManager.beginDelayedTransition(circular_reveal_layout, slideTransition);
                    }
                    download_movie.setVisibility(View.INVISIBLE);
//                    download_torrent_file.setVisibility(View.INVISIBLE);
                    imdb_homepage.setVisibility(View.INVISIBLE);
                    yts_homepage.setVisibility(View.INVISIBLE);
                }
            });
            animator.start();
        }
    }

    private void setupWindowAnimations() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Slide slide = new Slide();
            slide.setDuration(350);
            slide.setSlideEdge(Gravity.LEFT);
            TransitionManager.beginDelayedTransition(coordinatorLayout,slide);
            // set background color while sliding
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            getWindow().setEnterTransition(slide);
            getWindow().setReturnTransition(slide);
        }

    }

    public String getMovieGenre(String genre)
    {
        genre = genre.replace("[","");
        genre = genre.replace("]","");
        genre = genre.replace("\""," ");
        return genre;
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
            progressBar.setVisibility(View.INVISIBLE);
            adapter.setMovieData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_detail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

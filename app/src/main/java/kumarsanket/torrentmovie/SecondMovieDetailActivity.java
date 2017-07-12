package kumarsanket.torrentmovie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class SecondMovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    private Toolbar toolbar;
    private ImageView shareImageView,movieImageView,trailer_thumbnail,backButton,downloadImage;
    private TextView movieNameView , movieGenreView,movieDescriptionView,waitText,toolBarMovieName;
    private RecyclerView recyclerView;
    private SuggestMovieAdapter adapter;
    private String movieId ;
    private RelativeLayout relativeLayout;
    private CardView trailerCardView;
    private ImageView FABImage;
    private ScrollView scrollView;
    private String[] torrentMovieQuality ;
    private String[] magnetUrlArray ;
    private String[] torrentFileDownload ;
    private AlertDialog myDialog;
    private Movie movie = null;
    private String shareText = "";
    private LinearLayout layout;
    private AdView adView;

    private Button downloadMovie,imdbHomepage,ytsButton;
    private TextView downloadTextView,imdbHomepageTextView,ytsHomepageTextView;
    private ImageView closeImageView;
    private View customDialogView ;
    private Dialog customDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_movie_detail);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        /**
         *   get reference to views
         */
//        favImageView = (ImageView)findViewById(R.id.fav_icon);
        toolBarMovieName = (TextView)findViewById(R.id.toolbar_movie_name);
        shareImageView = (ImageView)findViewById(R.id.share_icon);
        movieImageView = (ImageView)findViewById(R.id.movie_image);
        movieNameView = (TextView)findViewById(R.id.movie_name);
        movieGenreView = (TextView)findViewById(R.id.movie_genre);
        movieDescriptionView = (TextView)findViewById(R.id.description_view);
        trailer_thumbnail = (ImageView)findViewById(R.id.trailer_thumbnail);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        relativeLayout = (RelativeLayout)findViewById(R.id.relative_layout);
        waitText = (TextView)findViewById(R.id.wait_text);
        trailerCardView = (CardView)findViewById(R.id.trailer_card_view);
        FABImage = (ImageView) findViewById(R.id.fab);
        scrollView = (ScrollView)findViewById(R.id.scroll_view);
        backButton = (ImageView)findViewById(R.id.back_button);
        layout = (LinearLayout) findViewById(R.id.suggested);
        adView = (AdView)findViewById(R.id.banner_ads);
        downloadImage = (ImageView)findViewById(R.id.download_movie_icon);


        /**
         *  Initialize the Mobile Ads SDK.
         */

        MobileAds.initialize(this,getResources().getString(R.string.admob_banner_ad));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("7EC37977E212E01AA58D851FCFFD4898")
                .build();

        adView.loadAd(adRequest);

        /**
         *  shareIcon is clicked
         */
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareItem();
            }
        });

        /**
         *  get intent and set movie
         */
        Intent intent = getIntent();
        movie = (Movie)intent.getSerializableExtra(Intent.EXTRA_TEXT);
        int length = movie.getMovieQualities().size();
        torrentMovieQuality = new String[length];
        magnetUrlArray = new String[length];
        torrentFileDownload = new String[length];


        /**
         *  set magnet urls
         */
        for(int i=0;i<length;i++)
        {
            MovieQuality movieQuality = movie.getMovieQualities().get(i);
            torrentMovieQuality[i] = movieQuality.getQuality()+", "+movieQuality.getSize();
            torrentFileDownload[i] = movieQuality.getUrl();
            magnetUrlArray[i] = "magnet:?xt=urn:btih:"
                    +movieQuality.getHash()+
                    "&dn=\n"
                    +movie.getMovie_slug()
                    +"&tr=udp://tracker.openbittorrent.com:80&tr=udp://tracker.publicbt.com:80&tr=\n"
                    +"udp://tracker.istole.it:80&tr=udp://open.demonii.com:80&tr=udp://tracker.coppersurfer.tk:80";
            /**
             *  you can also use below trackers
             */
//                    +"udp://open.demonii.com:1337/announce\n" +
//                    "udp://tracker.openbittorrent.com:80\n" +
//                    "udp://tracker.coppersurfer.tk:6969\n" +
//                    "udp://glotorrents.pw:6969/announce\n" +
//                    "udp://tracker.opentrackr.org:1337/announce\n" +
//                    "udp://torrent.gresille.org:80/announce\n" +
//                    "udp://p4p.arenabg.com:1337\n" +
//                    "udp://tracker.leechers-paradise.org:6969";
        }


        /**
         *  share text
         */
        shareText = "#HD Movie Download\n"
                +"Name : "+movie.getMovie_name()+"\n"
                +"Rating : "+movie.getMovie_rating();
        /**
         *  set movie name
         */
        toolBarMovieName.setText(movie.getMovie_name()+"");

        /**
         *  set data to view
         */
        Glide.with(this).load(movie.getLarge_image_cover())
                .into(movieImageView);
        movieNameView.setText("Name : "+movie.getMovie_name());
        movieGenreView.setText("Genre : "+getMovieGenre(movie.getMovie_genre()));
        movieDescriptionView.setText(movie.getMovie_full_description());
        movieId = movie.getMovie_id();

        /**
         *  get trailer thumbnail image url
         *  set trailer image
         */
        final String thumbnail_url = "https://img.youtube.com/vi/"+movie.getMovie_trailer_code()+"/0.jpg";
        Glide.with(this).load(thumbnail_url)
                .into(trailer_thumbnail);

        /**
         *  set adapter
         */
        adapter = new SuggestMovieAdapter(this,this,recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(1,null,this);

        /**
         *  set activity transition animation
         */
        setUpEnterTransition();


        /**
         *  play trailer
         */
        trailerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TrailerActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, movie.getMovie_trailer_code());
                startActivity(intent);
            }
        });

        /**
         *  download image icon is clicked
         */
        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTorrentDialog();
                myDialog.show();
//                showCustomDialog();

            }
        });
        /**
         *  FABImage click
         */
        FABImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        /**
         *  back Button is clicked
         */
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });


        if(!isNetworkAvailable())
        {
            layout.setVisibility(View.GONE);
            waitText.setVisibility(View.GONE);
        }


    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void goBack()
    {
        FABImage.animate()
                .scaleX(0f)
                .scaleY(0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        supportFinishAfterTransition();
                    }
                });
    }



    public void onShareItem(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/*");
        i.putExtra(Intent.EXTRA_TEXT,shareText+"\nhttp://play.google.com/store/apps/details?id="+getPackageName());
        startActivity(Intent.createChooser(i, "Share...."));
    }

    /**
     *  activity transition
     */
    public void setUpEnterTransition()
    {
        if(Build.VERSION.SDK_INT >= 21)
        {
            final Slide slideTransition = new Slide();
            slideTransition.setDuration(300);
            slideTransition.setSlideEdge(Gravity.LEFT);
            TransitionManager.beginDelayedTransition(relativeLayout,slideTransition);
            /**
             *  set background color while sliding
             */
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            getWindow().setEnterTransition(slideTransition);
            getWindow().setReturnTransition(slideTransition);
            /**
             *  fab animation
             */
            setUpFabAnimation(slideTransition);
        }
    }


    /**
     *  fab animation
     * @param slide  --------   slideTransition
     */
    public void setUpFabAnimation(final Slide slide)
    {
        slide.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                FABImage.animate()
                        .scaleX(0f)
                        .scaleY(0f);

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                /**
                 *  make fab visible with scaling
                 */
                FABImage.setVisibility(View.VISIBLE);
                FABImage.animate()
                        .scaleX(1f)
                        .scaleY(1f);
                slide.removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
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

                //  url ---- https://yts.ag/api/v2/list_movies.json?page=3&genre=Film-Noir
                String url = "https://yts.ag/api/v2/movie_suggestions.json?movie_id="+movieId;

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
            waitText.setVisibility(View.INVISIBLE);
            adapter.setMovieData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }

    public Boolean checkVersion()
    {
        return Build.VERSION.SDK_INT >= 21 ? true : false;
    }


    public void showDialog()
    {
        final View dialogView = View.inflate(this,R.layout.custom_dialog,null);
        final Dialog dialog = new Dialog(this,R.style.MyAlertDialogStyle);
        dialog.setContentView(dialogView);

        ImageView imageView = (ImageView)dialog.findViewById(R.id.close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRevealForView(dialogView,false,dialog);
            }
        });

        downloadMovie = (Button)dialog.findViewById(R.id.download_movie);
        downloadMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRevealForView(dialogView,false,dialog);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                createTorrentDialog();
                                myDialog.show();
                            }
                        },300);
            }
        });
        imdbHomepage = (Button)dialog.findViewById(R.id.Imdb_Homepage);
        imdbHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRevealForView(dialogView,false,dialog);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://www.imdb.com/title/"+movie.getMovie_imdb_code();
                        openBrowser(url);
                    }
                },300);

            }
        });
        ytsButton = (Button)dialog.findViewById(R.id.yts_homepage);
        ytsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRevealForView(dialogView,false,dialog);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = movie.getMovie_url();
                        openBrowser(url);
                    }
                },300);

            }
        });


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                createRevealForView(dialogView,true,null);
            }
        });


        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK) {
                    createRevealForView(dialogView, false, dialog);
                    return true;
                }
                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

    }

    public void createRevealForView(View dialogView , boolean isTrue , final Dialog dialog)
    {
        final View view = dialogView.findViewById(R.id.dialog);
        int width = view.getWidth()/2;
        int height = view.getHeight()/2;
        int radius = (int) Math.hypot(width,height);
        if(isTrue) {
            if (Build.VERSION.SDK_INT >= 21) {
                Animator animation = ViewAnimationUtils.createCircularReveal(view, width, height, 0, radius);
                view.setVisibility(View.VISIBLE);
                animation.setDuration(300);
                animation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        /**
                         *   FABImage scale animation
                         */
                        FABImage.animate()
                                .scaleY(0f)
                                .scaleX(0f);

                    }
                });
                animation.start();
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= 21) {
                Animator animation = ViewAnimationUtils.createCircularReveal(view, width, height, radius*2,0);
                animation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        dialog.dismiss();
                        /**
                         *   FABImage scale animation
                         */
                        view.setVisibility(View.INVISIBLE);
                        FABImage.animate()
                                .scaleX(1f)
                                .scaleY(1f);

                    }
                });
                animation.setDuration(300);
                animation.start();
            }
        }
    }

    public void createTorrentDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SecondMovieDetailActivity.this);
        builder.setTitle("Choose movie quality");
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
                    .title("No app found")
                    .content("Please Install BitTorrent or µTorrent to download this movie . would you like to download torrent app ?")
                    .titleColor(Color.WHITE)
                    .contentColor(getResources().getColor(R.color.background_color))
                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                    .positiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            downloadTorrentApp();
//                            downloadTorrentFile(torrentFile);
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


    /**
     *  Download torrent file
     * @param url
     */
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

    public void downloadTorrentApp()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=BitTorrent,+Inc.&hl=en"));
        startActivity(intent);
    }

    public void showCustomDialog()
    {
        customDialogView = View.inflate(this,R.layout.second_custom_dialog,null);
        customDialog = new Dialog(this,R.style.MySecondAlertDialogStyle);
        customDialog.setContentView(customDialogView);

        customDialog.setCancelable(true);
        customDialog.setCanceledOnTouchOutside(true);

        closeImageView = (ImageView)customDialog.findViewById(R.id.close_image);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSlideFromTopAnimation(customDialogView,false,customDialog);
            }
        });

        downloadTextView = (TextView) customDialog.findViewById(R.id.download_text);
        downloadTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSlideFromTopAnimation(customDialogView,false,customDialog);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createTorrentDialog();
                        myDialog.show();
                    }
                },300);
            }
        });
        imdbHomepageTextView = (TextView) customDialog.findViewById(R.id.imdb_text);
        imdbHomepageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSlideFromTopAnimation(customDialogView,false,customDialog);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://www.imdb.com/title/"+movie.getMovie_imdb_code();
                        openBrowser(url);
                    }
                },300);

            }
        });
        ytsHomepageTextView = (TextView) customDialog.findViewById(R.id.yts_text);
        ytsHomepageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSlideFromTopAnimation(customDialogView,false,customDialog);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = movie.getMovie_url();
                        openBrowser(url);
                    }
                },300);

            }
        });


        customDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                createSlideFromTopAnimation(customDialogView,true,null);
            }
        });


        customDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK) {
                    createSlideFromTopAnimation(customDialogView, false, customDialog);
                    return true;
                }
                return false;
            }
        });

        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.show();
    }


    public void createSlideFromTopAnimation(View dialogView , boolean isTrue , final Dialog dialog)
    {

        final ViewGroup view = (ViewGroup) dialogView.findViewById(R.id.relative_layout_table);
        int actionbarSize = Utils.dpToPx(300);
        if(isTrue)
        {
            view.setTranslationY(-actionbarSize);
            view.animate()
                    .translationY(0)
                    .setDuration(200)
                    .setStartDelay(200)
                    .setListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.VISIBLE);
                        }
                    }).start();


        }
        else
        {
            view.animate()
                    .translationY(-actionbarSize)
                    .setDuration(200)
                    .setStartDelay(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            dialog.dismiss();
                        }
                    })
                    .start();
        }


    }
}

package kumarsanket.torrentmovie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import kumarsanket.torrentmovie.Fragment.SuggestedMovieFragment;

public class SuggestedMovieDetailActivity extends AppCompatActivity {

    public ImageView movie_image_view,trailer_thumbnail,play_icon,downloadIcon;
    public TextView movie_name_view,movie_rating_view ,movie_description_view,genre_text;
    public CardView trailer_card_view;
    public String url = "http://www.imdb.com/";
    public String downloadUrl = "";
    public String torrent_magnet_url = "";
    public String[] torrentMovieQuality ;
    public String[] magnetUrlArray ;
    private AlertDialog myDialog;
    public String[] torrentFileDownload ;
    public String imdbUrl = "";


    public FrameLayout frameLayout ;
    public RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private ImageView backButton,shareIcon,FABImage;
    private TextView toolBarMovieName;
    private Movie movie = null;
    private AdView adView;
    private String shareText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_movie_detail);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        /**
         *  set custom toolbar
         */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        backButton = (ImageView)findViewById(R.id.back_button);
        toolBarMovieName = (TextView)findViewById(R.id.toolbar_movie_name);
        shareIcon = (ImageView)findViewById(R.id.share_icon);
        relativeLayout = (RelativeLayout)findViewById(R.id.parent_layout);
        movie_image_view = (ImageView)findViewById(R.id.movie_image);
        movie_name_view = (TextView)findViewById(R.id.movie_name);
        movie_rating_view = (TextView)findViewById(R.id.movie_rating);
        movie_description_view = (TextView)findViewById(R.id.description_view);
        trailer_card_view = (CardView)findViewById(R.id.trailer_card_view);
        trailer_thumbnail = (ImageView)findViewById(R.id.trailer_thumbnail);
        play_icon = (ImageView)findViewById(R.id.play_icon);
        genre_text =(TextView)findViewById(R.id.movie_genre);
        frameLayout = (FrameLayout)findViewById(R.id.suggestion_movie);
        FABImage = (ImageView)findViewById(R.id.fab);
        backButton = (ImageView)findViewById(R.id.back_button);
        toolBarMovieName = (TextView)findViewById(R.id.toolbar_movie_name);
        shareIcon = (ImageView)findViewById(R.id.share_icon);
        adView = (AdView)findViewById(R.id.banner_ads);
        downloadIcon = (ImageView)findViewById(R.id.download_movie_icon);


        /**
         *  initialize ads
         */
        MobileAds.initialize(this,getResources().getString(R.string.admob_banner_ad));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("7EC37977E212E01AA58D851FCFFD4898")
                .build();

        adView.loadAd(adRequest);


        /**
         *  get intent
         */
        Intent intent = getIntent();
        movie = (Movie) intent.getSerializableExtra(Intent.EXTRA_TEXT);
        int length = movie.getMovieQualities().size();


        /**
         *  share text
         */
        shareText = "#HD Movie Download\n"
                +"Name : "+movie.getMovie_name()+"\n"
                +"Rating : "+movie.getMovie_rating();


        Log.e("MovieQuality" , length+"");
        torrentMovieQuality = new String[length];
        magnetUrlArray = new String[length];
        torrentFileDownload = new String[length];
        for(int i=0;i<length;i++)
        {
            MovieQuality movieQuality = movie.getMovieQualities().get(i);
            torrentMovieQuality[i] = movieQuality.getQuality()+","+movieQuality.getSize();
            torrentFileDownload[i] = movieQuality.getUrl();
            magnetUrlArray[i] = "magnet:?xt=urn:btih:"+movieQuality.getHash()+"&dn=\n" +
                    movie.getMovie_slug()+"&tr=udp://tracker.openbittorrent.com:80&tr=udp://tracker.publicbt.com:80&tr=\n" +
                    "       udp://tracker.istole.it:80&tr=udp://open.demonii.com:80&tr=udp://tracker.coppersurfer.tk:80";
        }

        Log.v("image_url",movie.getLarge_image_cover());
        Glide.with(this).load(movie.getMedium_image_cover())
                .into(movie_image_view);
        movie_name_view.setText("Name : "+movie.getMovie_name());
        movie_rating_view.setText("Rating : "+ movie.getMovie_rating());
        movie_description_view.setText(movie.getMovie_full_description());

        final String thumbnail_url = "https://img.youtube.com/vi/"+movie.getMovie_trailer_code()+"/0.jpg";
        imdbUrl = "http://www.imdb.com/title/"+movie.getMovie_imdb_code();

        /**
         * set movie image
         */
        Glide.with(this).load(thumbnail_url)
                .into(trailer_thumbnail);
        trailer_card_view.setVisibility(View.VISIBLE);
        play_icon.setVisibility(View.VISIBLE);

        /**
         *  set movie genre
         */
        genre_text.setText("Genre : "+getMovieGenre(movie.getMovie_genre()));

        /**
         *  set movie name
         */
        getSupportActionBar().setTitle(movie.getMovie_name());


        /**
         *  create torrent dialog
         */
        createTorrentDialog();

        trailer_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TrailerActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, movie.getMovie_trailer_code());
                startActivity(intent);
            }
        });

        /**
         *  set fragment
         */
        SuggestedMovieFragment fragment = new SuggestedMovieFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.suggestion_movie,fragment);
        fragment.setMovieId(movie.getMovie_id());
        Log.e("movieId",movie.getMovie_id());
        transaction.commit();


        /**
         *  Activity enter animation
         */
        setUpEnterTransition();

        /**
         *  back button animation
         */
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonAnimation();
            }
        });

        /**
         *  set title
         */
        toolBarMovieName.setText(movie.getMovie_name());

        /**
         *  setup share
         */
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/*");
                i.putExtra(Intent.EXTRA_TEXT
                        ,shareText+"\nhttp://play.google.com/store/apps/details?id="+getPackageName());
                startActivity(Intent.createChooser(i, "Share...."));
            }
        });

        /**
         *  download icon clicked
         */
        downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTorrentDialog();
                myDialog.show();
            }
        });

        /**
         *  FABImage click listener
         */
        FABImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


    }


    /**
     *  set up enter transition
     */
    public void setUpEnterTransition()
    {
        if(Build.VERSION.SDK_INT >= 21)
        {
            Slide slideTransition = new Slide();
            slideTransition.setSlideEdge(Gravity.LEFT);
            slideTransition.setDuration(300);
            TransitionManager.beginDelayedTransition(relativeLayout,slideTransition);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            getWindow().setEnterTransition(slideTransition);
            getWindow().setReturnTransition(slideTransition);

            slideTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    FABImage.animate()
                            .scaleX(0f)
                            .scaleY(0f);
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    FABImage.setVisibility(View.VISIBLE);
                    FABImage.animate()
                            .scaleX(1f)
                            .scaleY(1f);
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

    }

    public String getMovieGenre(String genre)
    {
        genre = genre.replace("[","");
        genre = genre.replace("]","");
        genre = genre.replace("\""," ");
        return genre;
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
                    .content("Please Install BitTorrent or µTorrent to download this movie. would you like to download torrent file ?")
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
//                            Toast.makeText(MovieDetailActivity.this, "Install BitTorrent", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SuggestedMovieDetailActivity.this);
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

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }


    public void backButtonAnimation()
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

        Button downloadMovie = (Button)dialog.findViewById(R.id.download_movie);
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
        Button imdbHomepage = (Button)dialog.findViewById(R.id.Imdb_Homepage);
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
        Button ytsButton = (Button)dialog.findViewById(R.id.yts_homepage);
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
                Animator animation = ViewAnimationUtils.createCircularReveal(view, width, height, 0, radius
                );
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
}

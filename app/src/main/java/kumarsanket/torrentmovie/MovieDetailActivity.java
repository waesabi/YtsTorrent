package kumarsanket.torrentmovie;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import kumarsanket.torrentmovie.Fragment.SuggestedMovieFragment;

public class MovieDetailActivity extends AppCompatActivity {

    public ImageView movie_image_view,trailer_thumbnail,play_icon;
    public TextView movie_name_view,movie_rating_view ,movie_description_view,genre_text;
    public CardView trailer_card_view;
    public String url = "http://www.imdb.com/";
    public String downloadUrl = "";
    public String torrent_magnet_url = "";
    FloatingActionMenu fab_menu;
    FloatingActionButton fab_download, fab_imdb, fab_yts;

    public String[] torrentMovieQuality ;
    public String[] magnetUrlArray ;
    private AlertDialog myDialog;
    public String[] torrentFileDownload ;
    public String imdbUrl = "";


    public RelativeLayout parent_view;
    public FrameLayout frameLayout ;
    public ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_movie_detail);
        setContentView(R.layout.second_movie_detail_activity);


        scrollView = (ScrollView)findViewById(R.id.scroll_view);
        parent_view = (RelativeLayout)findViewById(R.id.parent_view);
        movie_image_view = (ImageView)findViewById(R.id.movie_image);
        movie_name_view = (TextView)findViewById(R.id.movie_name);
        movie_rating_view = (TextView)findViewById(R.id.movie_rating);
        movie_description_view = (TextView)findViewById(R.id.description_view);
        trailer_card_view = (CardView)findViewById(R.id.trailer_card_view);
        trailer_thumbnail = (ImageView)findViewById(R.id.trailer_thumbnail);
        play_icon = (ImageView)findViewById(R.id.play_icon);
        fab_menu = (FloatingActionMenu)findViewById(R.id.fab_menu);
        fab_download = (FloatingActionButton)findViewById(R.id.fab_download);
        fab_imdb = (FloatingActionButton)findViewById(R.id.fab_imdb_homepage);
        fab_yts = (FloatingActionButton)findViewById(R.id.fab_yts_homepage);
        genre_text =(TextView)findViewById(R.id.movie_genre);
        frameLayout = (FrameLayout)findViewById(R.id.suggestion_movie);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        final Movie movie = (Movie) intent.getSerializableExtra(Intent.EXTRA_TEXT);
        int length = movie.getMovieQualities().size();


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
        Glide.with(this).load(movie.getLarge_image_cover())
                .into(movie_image_view);
        movie_name_view.setText("Name : "+movie.getMovie_name());
        movie_rating_view.setText("Rating : "+ movie.getMovie_rating());
        movie_description_view.setText(movie.getMovie_full_description());

        final String thumbnail_url = "https://img.youtube.com/vi/"+movie.getMovie_trailer_code()+"/0.jpg";
        imdbUrl = "http://www.imdb.com/title/"+movie.getMovie_imdb_code();

        Glide.with(this).load(thumbnail_url)
                .into(trailer_thumbnail);
        trailer_card_view.setVisibility(View.VISIBLE);
        play_icon.setVisibility(View.VISIBLE);

        genre_text.setText("Genre : "+getMovieGenre(movie.getMovie_genre()));

        /**
         *  set movie name
         */
        getSupportActionBar().setTitle(movie.getMovie_name());


        createTorrentDialog();
        /**
         magnet:?xt=urn:btih:1342F9FA01F83204D3743E05569D3554FD1A16A3&dn=
         ice-age-collision-course-2016&tr=udp://tracker.openbittorrent.com:80&tr=udp://tracker.publicbt.com:80&tr=
         udp://tracker.istole.it:80&tr=udp://open.demonii.com:80&tr=udp://tracker.coppersurfer.tk:80
         */

        /**
         *
         *  instead of 'Url+Encoded+Movie+Name' just use "slug" - see in API
         *  after that copy from above link to get magnet link
         * magnet:?xt=urn:btih:TORRENT_HASH&dn=Url+Encoded+Movie+Name&tr=http://track.one:1234/announce&tr=udp://track.two:80

         **/

        trailer_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TrailerActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, movie.getMovie_trailer_code());
                startActivity(intent);
            }
        });


        fab_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.show();
                fab_menu.close(true);
            }
        });

        fab_yts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = movie.getMovie_url();
                openBrowser(url);
                fab_menu.close(true);
            }
        });

        fab_imdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.imdb.com/title/"+movie.getMovie_imdb_code();
                openBrowser(url);
                fab_menu.close(true);
            }
        });

        setupWindowAnimations();
        SuggestedMovieFragment fragment = new SuggestedMovieFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.suggestion_movie,fragment);
        fragment.setMovieId(movie.getMovie_id());
        Log.e("movieId",movie.getMovie_id());
        transaction.commit();



    }

    private void setupWindowAnimations() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Slide slide = new Slide();
            slide.setDuration(300);
            slide.setSlideEdge(Gravity.RIGHT);
            TransitionManager.beginDelayedTransition(scrollView,slide);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_detail,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     *  youtube thumbnail url = https://img.youtube.com/vi/<insert-youtube-video-id-here>/0.jpg
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();

        switch (item.getItemId())
        {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.movie_home_page :
                openBrowser(imdbUrl);
                return true;
            case R.id.movie_download_torrent :
                createTorrentDialog();
                myDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createTorrentDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity.this);
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

    public void copyMagnetUrl()
    {
        ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newUri(getContentResolver(), "URI", Uri.parse(torrent_magnet_url));
        myClipboard.setPrimaryClip(myClip);
    }



    /*
    magnet:?xt=urn:btih:A52324E4506C66CB30067BE13EE366C225BC4D70&dn=
    logan-2017&tr=udp://tracker.openbittorrent.com:80&tr=udp://tracker.publicbt.com:80&tr=
    udp://tracker.istole.it:80&tr=udp://open.demonii.com:80&tr=udp://tracker.coppersurfer.tk:80

     */

    /*
    magnet:?xt=urn:btih:A52324E4506C66CB30067BE13EE366C225BC4D70&dn=Logan+
    %282017%29+%5B720p%5D+%5BYTS.AG%5D&tr=udp%3A%2F%2Fglotorrents.pw%3A6969%2Fannounce&tr=
    udp%3A%2F%2Ftracker.openbittorrent.com%3A80&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=
    udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fp4p.arenabg.ch%3A1337&tr=
    udp%3A%2F%2Ftracker.internetwarriors.net%3A1337
     */

}

package kumarsanket.torrentmovie;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import kumarsanket.torrentmovie.Fragment.ActionMovieFragment;
import kumarsanket.torrentmovie.Fragment.AdventureMovieFragment;
import kumarsanket.torrentmovie.Fragment.AnimationMovieFragment;
import kumarsanket.torrentmovie.Fragment.BiographyMovieFragment;
import kumarsanket.torrentmovie.Fragment.ComedyMovieFragment;
import kumarsanket.torrentmovie.Fragment.CrimeMovieFragment;
import kumarsanket.torrentmovie.Fragment.DocumentaryMovieFragment;
import kumarsanket.torrentmovie.Fragment.DramaMovieFragment;
import kumarsanket.torrentmovie.Fragment.FamilyMovieFragment;
import kumarsanket.torrentmovie.Fragment.FantasyMovieFragment;
import kumarsanket.torrentmovie.Fragment.FilmNoirMovieFragment;
import kumarsanket.torrentmovie.Fragment.HistoryMovieFragment;
import kumarsanket.torrentmovie.Fragment.HorrorMovieFragment;
import kumarsanket.torrentmovie.Fragment.MusicMovieFragment;
import kumarsanket.torrentmovie.Fragment.MusicalMovieFragment;
import kumarsanket.torrentmovie.Fragment.MysteryMovieFragment;
import kumarsanket.torrentmovie.Fragment.NewMovieFragment;
import kumarsanket.torrentmovie.Fragment.RomanceMovieFragment;
import kumarsanket.torrentmovie.Fragment.SciFiMovieFragment;
import kumarsanket.torrentmovie.Fragment.SportMovieFragment;
import kumarsanket.torrentmovie.Fragment.ThrillerMovieFragment;
import kumarsanket.torrentmovie.Fragment.WarMovieFragment;
import kumarsanket.torrentmovie.Fragment.WesternMovieFragment;

public class MainActivity extends AppCompatActivity {


    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar ;
    public TabLayout tabLayout ;
    public ViewPager viewPager ;
    private NavigationView navigationView ;
    private AppBarLayout appBarLayout;

    // for tool bar animation
    boolean pendingIntroAnimation = false;
    private static final int ANIM_DURATION_TOOLBAR = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        appBarLayout = (AppBarLayout)findViewById(R.id.appbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
//        if (navigationView != null) {
//            setupDrawerContent(navigationView);
//        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        startIntroAnimation();
    }




    private void setupWindowAnimations() {


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.LEFT);
            slide.setDuration(260);
            TransitionManager.beginDelayedTransition(mDrawerLayout,slide);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            getWindow().setExitTransition(slide);
            getWindow().setReenterTransition(slide);
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new NewMovieFragment(), "New");
        adapter.addFragment(new ActionMovieFragment(), "Action");
        adapter.addFragment(new AdventureMovieFragment(), "Adventure");
        adapter.addFragment(new AnimationMovieFragment(), "Animation");
        adapter.addFragment(new BiographyMovieFragment(), "Biography");
        adapter.addFragment(new ComedyMovieFragment(), "Comedy");
        adapter.addFragment(new CrimeMovieFragment(), "Crime");
        adapter.addFragment(new DocumentaryMovieFragment(), "Documentary");
        adapter.addFragment(new DramaMovieFragment(), "Drama");
        adapter.addFragment(new FamilyMovieFragment(), "Family");
        adapter.addFragment(new FantasyMovieFragment(), "Fantasy");
        adapter.addFragment(new FilmNoirMovieFragment(), "Film-Noir");
        adapter.addFragment(new HistoryMovieFragment(), "History");
        adapter.addFragment(new HorrorMovieFragment(), "Horror");
        adapter.addFragment(new MusicMovieFragment(), "Music");
        adapter.addFragment(new MusicalMovieFragment(), "Musical");
        adapter.addFragment(new MysteryMovieFragment(), "Mystery");
        adapter.addFragment(new RomanceMovieFragment(), "Romance");
        adapter.addFragment(new SciFiMovieFragment(), "Sci-Fi ");
        adapter.addFragment(new SportMovieFragment(), "Sport");
        adapter.addFragment(new ThrillerMovieFragment(), "Thriller ");
        adapter.addFragment(new WarMovieFragment(), "War");
        adapter.addFragment(new WesternMovieFragment(), "Western");

        viewPager.setAdapter(adapter);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId())
                        {
                            case R.id.nav_show_airing_today :
                                Intent intent = new Intent(MainActivity.this,ThreeDMovieActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_top_rated_movie :
                                Intent topRated = new Intent(MainActivity.this,TopRatedActivity.class);
                                startActivity(topRated);
                                break;
                            case R.id.nav_share :
                                Intent shareIntent = ShareCompat.IntentBuilder.from(MainActivity.this)
                                        .setType("text/plain")
                                        .setText("#HD Movie Download \nDownload new movie")
                                        .getIntent();
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Take a look at \"HD Movie Download\"");
                                shareIntent.putExtra(Intent.EXTRA_TEXT
                                        ,"http://play.google.com/store/apps/details?id="+getPackageName());
                                if(shareIntent.resolveActivity(getPackageManager())!=null)
                                {
                                    startActivity(shareIntent);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this,"Please install gmail app",Toast.LENGTH_SHORT).show();
                                }
                                break ;
                            case R.id.nav_feedback :
                                sendFeedback();
                                break ;
                            case R.id.nav_rate_us :
                                rateUs();
                                break;
                        }
                        menuItem.setChecked(true);
                        menuItem.setCheckable(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.open_drawer,
                R.string.close_drawer)
        {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }


    static class Adapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


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


    public void sendFeedback()
    {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "sanket.kr7922@gmail.com" });
        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        Email.putExtra(Intent.EXTRA_TEXT, "#HD Movie Download \n");
        startActivity(Intent.createChooser(Email, "Sending Feedback"));
    }


    public void helpAndSupport()
    {
        Intent rewardedIntent = new Intent(MainActivity.this,RewardedVideoActivity.class);
        startActivity(rewardedIntent);
        Toast.makeText(MainActivity.this,"Please wait..." ,Toast.LENGTH_SHORT).show();
    }


    public void showHelpDialog()
    {
        new MaterialDialog.Builder(MainActivity.this)
                .title("Help & Support")
                .content("Please play with ads to help \nThanks.")
                .titleColor(Color.WHITE)
                .contentColor(getResources().getColor(R.color.background_color))
                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .show();
    }


    public void rateUs()
    {
        final String appPackageName = getPackageName() ; // getPackageName() from Context or Activity object
        Log.e("PackageName" , appPackageName+"");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }
        catch (android.content.ActivityNotFoundException a) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            a.printStackTrace();
        }
    }



    // toolbar animation
    private void startIntroAnimation() {

        int actionbarSize = Utils.dpToPx(140);
        appBarLayout.setTranslationY(-actionbarSize);
        appBarLayout.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (navigationView != null) {
                            setupDrawerContent(navigationView);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    public void startAboutUsActivity()
    {
        Intent intent = new Intent(this,AboutUsActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(intent,activityOptions.toBundle());
    }
}

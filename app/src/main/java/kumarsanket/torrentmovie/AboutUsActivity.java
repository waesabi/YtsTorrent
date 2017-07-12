package kumarsanket.torrentmovie;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class AboutUsActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);



        relativeLayout = (RelativeLayout)findViewById(R.id.view_group);
        toolbar = (Toolbar)findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        setUpEnterTransitionAnimation();
    }

    public void setUpEnterTransitionAnimation()
    {
        Slide slideTransition = new Slide();
        slideTransition.setSlideEdge(Gravity.RIGHT);
        slideTransition.setDuration(300);
        TransitionManager.beginDelayedTransition(relativeLayout,slideTransition);

        /**
         *  set background color while sliding
         */
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getWindow().setEnterTransition(slideTransition);
        getWindow().setReturnTransition(slideTransition);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home :
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

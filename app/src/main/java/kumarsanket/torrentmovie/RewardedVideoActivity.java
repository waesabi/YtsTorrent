package kumarsanket.torrentmovie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;

public class RewardedVideoActivity extends AppCompatActivity{

    private ProgressBar progressBar;
    private NativeExpressAdView adView;
    private VideoController videoController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded_video);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        adView = (NativeExpressAdView)findViewById(R.id.nativeAdView);

        adView.setVideoOptions(new VideoOptions.Builder().setStartMuted(false).build());

        videoController = adView.getVideoController();
        videoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoStart() {
                super.onVideoStart();
            }

            @Override
            public void onVideoEnd() {
                super.onVideoEnd();
            }
        });


        adView.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded() {
                if(videoController.hasVideoContent())
                {
                    Toast.makeText(RewardedVideoActivity.this,"Please click on ad to support",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                    super.onAdLoaded();
            }
        });


        adView.loadAd(new AdRequest.Builder().addTestDevice("7EC37977E212E01AA58D851FCFFD4898").build());


    }


}

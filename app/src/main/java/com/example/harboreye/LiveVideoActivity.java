package com.example.harboreye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.kinesisvideo.auth.KinesisVideoCredentials;
import com.amazonaws.kinesisvideo.auth.KinesisVideoCredentialsProvider;
import com.amazonaws.kinesisvideo.auth.StaticCredentialsProvider;
import com.amazonaws.kinesisvideo.client.KinesisVideoClient;
import com.amazonaws.kinesisvideo.client.KinesisVideoClientConfiguration;
import com.amazonaws.kinesisvideo.common.exception.KinesisVideoException;
import com.amazonaws.kinesisvideo.storage.DefaultStorageCallbacks;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.kinesisvideo.service.KinesisVideoAndroidServiceClient;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.AWSKinesisVideo;
import com.amazonaws.services.kinesisvideo.model.GetDataEndpointRequest;
import com.amazonaws.services.kinesisvideo.model.GetDataEndpointResult;
import com.amazonaws.services.kinesisvideoarchivedmedia.AWSKinesisVideoArchivedMedia;
import com.amazonaws.services.kinesisvideoarchivedmedia.AWSKinesisVideoArchivedMediaClient;
import com.amazonaws.util.json.AwsJsonFactory;
import com.example.harboreye.ui.CCTV.CCTVFragment;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.*;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.handlers.*;
import com.amazonaws.http.*;
import com.amazonaws.internal.*;
import com.amazonaws.metrics.*;
import com.amazonaws.transform.*;
import com.amazonaws.util.*;
import com.amazonaws.util.AWSRequestMetrics.Field;
import com.amazonaws.services.kinesisvideo.*;
import com.amazonaws.services.kinesisvideoarchivedmedia.model.*;
import com.amazonaws.services.kinesisvideoarchivedmedia.model.transform.*;
import com.amazonaws.mobileconnectors.kinesisvideo.client.KinesisVideoAndroidClientFactory;

import static com.amazonaws.services.kinesisvideo.model.APIName.GET_HLS_STREAMING_SESSION_URL;
import static com.amazonaws.services.kinesisvideoarchivedmedia.model.DiscontinuityMode.ALWAYS;
import static com.amazonaws.services.kinesisvideoarchivedmedia.model.HLSFragmentSelectorType.*;
import static com.amazonaws.services.kinesisvideoarchivedmedia.model.PlaybackMode.LIVE;
import static java.sql.Types.NULL;

public class LiveVideoActivity extends AppCompatActivity {
    public static final String ACCESS_KEY = "AKIA5HQTEQLQE5VC6OPF";
    public static final String SECRET_KEY = "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";
    public static final String ENDPOINT = "https://kinesisvideo.ap-northeast-2.amazonaws.com";
    private PlayerView playerView;
    private PlayerControlView playerControlView;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private String a;
    private TextView livetext;
    private String cc;
    private GetHLSStreamingSessionURLRequest getHLSStreamingSessionURLRequest;
    private AWSKinesisVideoArchivedMediaClient awsKinesisVideoArchivedMediaClient;

    private AWSKinesisVideoArchivedMedia awsKinesisVideoArchivedMedia;
    private GetHLSStreamingSessionURLResult getHLSStreamingSessionURLResult;
    private AWSKinesisVideo awsKinesisVideo;
    private AWSCredentials awsCredentials;
    private AWSCredentialsProvider awsCredentialsProvider;
    private AWSKinesisVideoClient awsKinesisVideoClient;
    private KinesisVideoClientConfiguration kinesisVideoClientConfiguration;
    private Handler handler;
    private HLSFragmentSelector hlsFragmentSelector;
    private GetDataEndpointRequest getDataEndpointRequest;
    private GetDataEndpointResult getDataEndpointResult;
    private KinesisVideoAndroidServiceClient kinesisVideoAndroidServiceClient;
    private AWSMobileClient mobileClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //가로 화면 유지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_live_video);

        playerView = findViewById(R.id.video_view);
        livetext = findViewById(R.id.livetext);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                //Log.i("체크?", cc);
                livetext.setText(cc);
            }
        };

///////////kinesis streams 테스트중///////////
        class NewRunnable implements Runnable {
            @Override
            public void run() {
                try {

                    awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

                        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                                getApplicationContext(),
                                "ap-northeast-2:ce2f558c-c059-49fa-b0b5-d04793e564ba", // 자격 증명 풀 ID
                                Regions.AP_NORTHEAST_2 // 리전
                        );



                    AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
                        @Override
                        public void onResult(final UserStateDetails details) {
                            awsKinesisVideoArchivedMediaClient = new AWSKinesisVideoArchivedMediaClient(AWSMobileClient.getInstance());
                        }
                        @Override
                        public void onError(final Exception e) {
                            e.printStackTrace();
                        }
                    });
                    //awsKinesisVideoArchivedMediaClient = new AWSKinesisVideoArchivedMediaClient(AWSMobileClient.getInstance());



                    /*hlsFragmentSelector = new HLSFragmentSelector();
                    hlsFragmentSelector.withFragmentSelectorType(SERVER_TIMESTAMP);*/


                    getHLSStreamingSessionURLRequest = new GetHLSStreamingSessionURLRequest()
                            .withStreamName("youjin_stream")
                            .withStreamARN("arn:aws:kinesisvideo:ap-northeast-2:909499466464:stream/youjin_stream/1598726297770")
                            .withPlaybackMode(LIVE)
                            .withDiscontinuityMode(ALWAYS);
                            //.withMaxMediaPlaylistFragmentResults((long) 5)
                           // .withExpires(3600);
                     /*hlsFragmentSelector = new HLSFragmentSelector();
                    hlsFragmentSelector.withFragmentSelectorType(SERVER_TIMESTAMP);
                    getHLSStreamingSessionURLRequest.setHLSFragmentSelector(hlsFragmentSelector);*/

                    cc = getHLSStreamingSessionURLRequest.toString();


                    //  kinesisVideoAndroidServiceClient=new KinesisVideoAndroidServiceClient(awsCredentials,
                    //   Region.getRegion(Regions.AP_NORTHEAST_2),kinesisVideoAndroidServiceClient.getDataEndpoint("youjin_stream",GET_HLS_STREAMING_SESSION_URL,NULL,awsCredentialsProvider));
                   getHLSStreamingSessionURLResult = new GetHLSStreamingSessionURLResult();
                   getHLSStreamingSessionURLResult=awsKinesisVideoArchivedMediaClient.getHLSStreamingSessionURL(getHLSStreamingSessionURLRequest);


                    // getHLSStreamingSessionURLResult=archivedMediaClient.getHLSStreamingSessionURL(getHLSStreamingSessionURLRequest);
                    // a = getHLSStreamingSessionURLResult.getHLSStreamingSessionURL();

                } catch (Exception e) {
                    cc = "실패";
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }
        NewRunnable newRunnable = new NewRunnable();
       // Thread thread = new Thread(newRunnable);
       // thread.start();
    }

    //엑소플레이어 빌드
    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();

        playerView.setPlayer(player);
        Intent intent = getIntent();

       /* Uri uri = Uri.parse(intent.getExtras().getString("url"));
        MediaSource mediaSource = buildMediaSource(uri);*/


        Log.i("이니셜", "");
        Uri uri = Uri.parse(getString(R.string.live_url));
        HlsMediaSource mediaSource = (HlsMediaSource) buildHlsMediaSource(uri);

        player.prepare(mediaSource, false, false);
        player.setPlayWhenReady(playWhenReady);

    }

    private MediaSource buildMediaSource(Uri uri) {

        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, getString(R.string.app_name));
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private HlsMediaSource buildHlsMediaSource(Uri uri) {

        DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory(getString(R.string.app_name));
        return new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    //시작
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    //시작
    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    //시작 ui
    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    //일시정지
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    //정지
    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }
}


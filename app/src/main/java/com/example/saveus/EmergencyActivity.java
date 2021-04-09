package com.example.saveus;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class EmergencyActivity extends YouTubeBaseActivity {
    // 객체 선언
    YouTubePlayerView playerView;
    YouTubePlayer player;

    // 유튜브 API Key와 동영상 ID 변수설정
    private String API_KEY = "유튜브 API키 입력"; // 개인정보라서 깃허브에 올리면 위험함
    private String videoId = "q3xPn7_AZTk";

    // logcat 사용설정
    private final String TAG = "EmergencyActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        setTitle("응급처치");

        initPlayer();

        Button btnPlay = findViewById(R.id.youtubeBtn);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
            }
        });
    }

    private void initPlayer() {
        playerView = findViewById(R.id.youTubePlayerView);
        playerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player = youTubePlayer;
                player.cueVideo(videoId);

                player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String id) {
                        Log.d(TAG, "onLoaded : " + id);
                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {

                    }

                    @Override
                    public void onVideoEnded() {

                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        Log.d(TAG, "onError : " + errorReason);
                    }
                });

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    private void playVideo() {
        if (player != null) {
            if (player.isPlaying())
                player.pause();
            player.cueVideo(videoId);
        }
    }
}

package com.example.saveus;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class FractureActivity extends YouTubeBaseActivity {
    // 객체 선언
    YouTubePlayerView playerView;
    YouTubePlayer player;

    // 유튜브 API Key와 동영상 ID 변수설정
    private String API_KEY = "API KEY 입력"; // 개인정보라서 깃허브에 올리면 위험함
    private String videoId = "vb7iYw_u24E";

    // logcat 사용설정
    private final String TAG = "FractureActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fracture);
        setTitle("골절");

        initPlayer();   // 유튜브 플레이어 가져오기
    }

    // 유튜브 플레이어를 가져오는 메서드
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
                        Log.d(TAG, "onLoaded : " + id); // 정상적으로 load가 되면 TAG에 상태 저장
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
                        // 에러가 나면 에러 이유를 TAG에 상태 저장
                    }
                });

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }
}

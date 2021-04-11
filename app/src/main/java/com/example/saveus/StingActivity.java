package com.example.saveus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.youtube.player.YouTubeBaseActivity;
//import com.google.android.youtube.player.YouTubeInitializationResult;
//import com.google.android.youtube.player.YouTubePlayer;
//import com.google.android.youtube.player.YouTubePlayerView;

public class StingActivity extends AppCompatActivity {
    // 객체 선언
    //YouTubePlayerView playerView_Animal, playerView_Snake, playerView_Bee; // 동물, 뱀, 벌 동영상 2개이므로
   // YouTubePlayer player_Animal, player_Snake, player_Bee; // 동물, 뱀, 벌 동영상 3개이므로

    // 유튜브 API Key와 동영상 ID 변수설정
    private String API_KEY = "API KEY 입력"; // 개인정보라서 깃허브에 올리면 위험함
    private String videoId_Animal = "YcKSwrV8q3w";
    private String videoId_Snake = "kuEphbssvBI";
    private String videoId_Bee = "OiWefNZ0wK0";

    // logcat 사용설정
    private final String TAG = "StingActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sting);
        setTitle("쏘임/물림");

        //initPlayer();   // 유튜브 플레이어 가져오기
    }

    // 유튜브 플레이어를 가져오는 메서드
    /*
    private void initPlayer() {
        playerView_Animal = findViewById(R.id.youTubePlayerView_animal);
        playerView_Snake = findViewById(R.id.youTubePlayerView_snake);
        playerView_Bee = findViewById(R.id.youTubePlayerView_bee);

        playerView_Animal.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player_Animal = youTubePlayer;
                player_Animal.cueVideo(videoId_Animal);

                player_Animal.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
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

        playerView_Snake.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player_Snake = youTubePlayer;
                player_Snake.cueVideo(videoId_Snake);

                player_Snake.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {
                        Log.d(TAG, "onLoaded : " + s); // 정상적으로 load가 되면 TAG에 상태 저장
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

        playerView_Bee.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player_Bee = youTubePlayer;
                player_Bee.cueVideo(videoId_Bee);

                player_Bee.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {
                        Log.d(TAG, "onLoaded : " + s); // 정상적으로 load가 되면 TAG에 상태 저장
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
    */
}
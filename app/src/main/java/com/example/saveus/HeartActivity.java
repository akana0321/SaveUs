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

public class HeartActivity extends AppCompatActivity{
    // 객체 선언
    // YouTubePlayerView playerView_AED, playerView_Heart; // 성인 및 유아 동영상 2개이므로
    //YouTubePlayer player_AED, player_Heart; // 성인 및 유아 동영상 2개이므로

    // 유튜브 API Key와 동영상 ID 변수설정
    private String API_KEY = "API KEY 입력"; // 개인정보라서 깃허브에 올리면 위험함
    private String videoId_AED = "LjqDfa3m2rM";
    private String videoId_Heart = "E5WrtJycVYs";

    // logcat 사용설정
    private final String TAG = "AirwayActivity";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        setTitle("심장마비");

        /****************************************************
         ****************** 변수 선언부 *********************
         ****************************************************/

        /****************************************************
         *************** 인텐트 변환 메서드 ******************
         ****************************************************/

        //initPlayer();
    }
    // 유튜브 플레이어를 가져오는 메서드
    /*private void initPlayer() {
        playerView_Heart = findViewById(R.id.youTubePlayerView_heart);
        playerView_AED = findViewById(R.id.youTubePlayerView_aed);

        playerView_Heart.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player_Heart = youTubePlayer;
                player_Heart.cueVideo(videoId_Heart);

                player_Heart.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
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

        playerView_AED.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player_AED = youTubePlayer;
                player_AED.cueVideo(videoId_AED);

                player_AED.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
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
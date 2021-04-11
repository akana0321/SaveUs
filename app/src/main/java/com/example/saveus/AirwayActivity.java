package com.example.saveus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.youtube.player.YouTubeBaseActivity;
// com.google.android.youtube.player.YouTubeInitializationResult;
//import com.google.android.youtube.player.YouTubePlayer;
// com.google.android.youtube.player.YouTubePlayerView;

public class AirwayActivity extends AppCompatActivity {

    // 객체 선언
    //YouTubePlayerView playerView_Adult, playerView_Child; // 성인 및 유아 동영상 2개이므로
    //YouTubePlayer player_Adult, player_Child; // 성인 및 유아 동영상 2개이므로

    // 유튜브 API Key와 동영상 ID 변수설정
    private String API_KEY = "API KEY 입력"; // 개인정보라서 깃허브에 올리면 위험함
    private String videoId_Adult = "BhaR7UieJXQ";
    private String videoId_child = "AQmoPWdI2pg";
    // logcat 사용설정
    private final String TAG = "AirwayActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airway);
        setTitle("기도폐쇄");

        YouTubePlayerView youTubePlayerView_ADULT = findViewById(R.id.youTubePlayerView_adult);
        YouTubePlayerView youTubePlayerView_CHILD = findViewById(R.id.youTubePlayerView_child);
        getLifecycle().addObserver(youTubePlayerView_ADULT);
        getLifecycle().addObserver(youTubePlayerView_CHILD);

        //initPlayer();   // 유튜브 플레이어 가져오기
    }
    public boolean onCreateOptionsMenu(Menu menu) { // 상단 우측 탭 호출
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.tbEmer :
                Intent intent = new Intent(getApplicationContext(), EmergencyActivity.class);
                startActivity(intent); // 응급처치 기능 클릭시 페이지 전환
                return true;
            case R.id.tbAed:
                intent = new Intent(getApplicationContext(), AedActivity.class);
                startActivity(intent); //자동제세동기 기능 클릭시 페이지 전환
                return true;
            case R.id.tbMoun:
                intent = new Intent(getApplicationContext(), MountainActivity.class);
                startActivity(intent); //등산중 사고 신고 클릭시 페이지 전환
                return true;
            case R.id.tbPati:
                intent = new Intent(getApplicationContext(), PatientActivity.class);
                startActivity(intent); //환자 상태파악 클릭시 페이지 전환
                return true;
            case R.id.tbCont:
                intent = new Intent(getApplicationContext(), ContactActivity.class);
                startActivity(intent); //문의하기 기능클릭시 페이지 전환
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
    // 유튜브 플레이어를 가져오는 메서드
    /*private void initPlayer() {
        playerView_Adult = findViewById(R.id.youTubePlayerView_adult);
        playerView_Child = findViewById(R.id.youTubePlayerView_child);

        playerView_Adult.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player_Adult = youTubePlayer;
                player_Adult.cueVideo(videoId_Adult);

                player_Adult.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
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

        playerView_Child.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player_Child = youTubePlayer;
                player_Child.cueVideo(videoId_child);

                player_Child.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
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
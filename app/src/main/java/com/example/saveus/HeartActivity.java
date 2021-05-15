package com.example.saveus;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class HeartActivity extends MainActivity{
    BottomNavigationView frBottom;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("심장마비");

        YouTubePlayerView youTubePlayerView_heart = findViewById(R.id.youtube_player_heart);
        YouTubePlayerView youtubePlayerView_aed = findViewById(R.id.youtube_player_aed);
        getLifecycle().addObserver(youTubePlayerView_heart);
        getLifecycle().addObserver(youtubePlayerView_aed);

        BottomNavigationView frBottom = (BottomNavigationView)findViewById(R.id.frBottom);
        frBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.frBack :
                        finish();
                        return true;
                    case R.id.frMain :
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        // 활성화 되어 있는 모든 인텐트 삭제
                        for (int i = 0; i < actList.size(); i++)
                            actList.get(i).finish();
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.frExit :
                        moveTaskToBack(true); // 태스크를 백그라운드로 이동
                        finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                        System.exit(0);
                        return true;
                }
                return false;
            }
        });

        // 버튼 클릭 시 이동
        Button heabtn_pat = (Button) findViewById(R.id.heabtn_pat);
        Button heabtn_aed = (Button) findViewById(R.id.heabtn_aed);

        heabtn_pat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientActivity.class);
                startActivity(intent); // 환자 상태파악 페이지로 전환
            }
        });

        heabtn_aed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AedActivity.class);
                startActivity(intent); // AED 위치 페이지로 전환
            }
        });
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
                intent = new Intent(getApplicationContext(), ReportActivity.class);
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
package com.example.saveus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    TextView MainTv_EMER, MainTv_AED, Main_MOUN,Main_PATI,Main_COUNT;
    BottomNavigationView frBottom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("메인화면");

        MainTv_EMER = (TextView) findViewById(R.id.mainTv_Emer);
        MainTv_AED = (TextView)findViewById(R.id.mainTv_AED);
        Main_MOUN = (TextView)findViewById(R.id.mainTV_Moun);
        Main_PATI = (TextView)findViewById(R.id.mainTv_Pati);
        Main_COUNT = (TextView)findViewById(R.id.mainTv_Cont);

        // Bottom Navigation 설정
        frBottom = (BottomNavigationView) findViewById(R.id.frBottom);

        MainTv_EMER.setOnClickListener(new View.OnClickListener() {  // 응급처치 클릭시 응급상황 분류 페이지 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmergencyActivity.class);
                startActivity(intent);
            }
        });

        // 바텀 네이게이션 각 버튼 클릭시 실행.
        frBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.frBack :
                        Intent intent = new Intent(getApplicationContext(), EmergencyActivity.class);
                        startActivity(intent); // 2021.04.07 임시로 페이지가 전환되는 지 확인하기 위해 작성해 봄.
                        return true;
                    case R.id.frMain :
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
                startActivity(intent);
                return true;
            // 응급처치 기능 클릭시 페이지 전환
            case R.id.tbAed:
                //자동제세동기 기능 클릭시 페이지 전환
                return true;
            case R.id.tbMoun:
                //등산중 사고 신고 클릭시 페이지 전환
                return true;
            case R.id.tbPati:
                //환자 상태파악 클릭시 페이지 전환
                return true;
            case R.id.tbCont:
                //문의하기 기능클릭시 페이지 전환
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}

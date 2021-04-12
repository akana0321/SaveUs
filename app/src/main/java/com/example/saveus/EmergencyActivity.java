package com.example.saveus;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmergencyActivity extends MainActivity {
    TextView EmerTv_BruTv,EmerTv_BurnTv,EmerTv_AirTv,EmerTv_SeiTv,EmerTv_StiTv,Emer_HeaTv;
    BottomNavigationView frBottom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("응급처치");

        EmerTv_BruTv = (TextView)findViewById(R.id.emerTv_bru);
        EmerTv_BurnTv = (TextView)findViewById(R.id.emerTv_burn);
        EmerTv_AirTv = (TextView)findViewById(R.id.emerTv_air);
        EmerTv_SeiTv = (TextView)findViewById(R.id.emerTv_sei);
        EmerTv_StiTv = (TextView)findViewById(R.id.emerTv_sti);
        Emer_HeaTv = (TextView)findViewById(R.id.emerTv_heaTv);
        frBottom = (BottomNavigationView)findViewById(R.id.frBottom_1);

        // 텍스트뷰 클릭 시 이동
        EmerTv_BruTv.setOnClickListener(new View.OnClickListener() {  // 응급처치 클릭시 응급상황 분류 페이지 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BruiseActivity.class);
                startActivity(intent);
            }
        });
        EmerTv_BurnTv.setOnClickListener(new View.OnClickListener() {  // 응급처치 클릭시 응급상황 분류 페이지 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BurnActivity.class);
                startActivity(intent);
            }
        });
        EmerTv_AirTv.setOnClickListener(new View.OnClickListener() {  // 응급처치 클릭시 응급상황 분류 페이지 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AirwayActivity.class);
                startActivity(intent);
            }
        });
        EmerTv_SeiTv.setOnClickListener(new View.OnClickListener() {  // 응급처치 클릭시 응급상황 분류 페이지 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeizureActivity.class);
                startActivity(intent);
            }
        });
        EmerTv_StiTv.setOnClickListener(new View.OnClickListener() {  // 응급처치 클릭시 응급상황 분류 페이지 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StingActivity.class);
                startActivity(intent);
            }
        });
        Emer_HeaTv.setOnClickListener(new View.OnClickListener() {  // 응급처치 클릭시 응급상황 분류 페이지 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HeartActivity.class);
                startActivity(intent);
            }
        });

        // 바텀 네이게이션 각 버튼 클릭시 실행.
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

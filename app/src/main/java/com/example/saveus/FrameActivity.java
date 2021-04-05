package com.example.saveus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.saveus.R.layout.activity_emergency;
import static com.example.saveus.R.layout.activity_fracture;
import static com.example.saveus.R.layout.activity_main;

public class FrameActivity extends AppCompatActivity {
    View v1;
    Toolbar toolBar;
    BottomNavigationView frBottom;
    FrameLayout content;
    Button MainTv_EMER;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        // Toolbar 설정
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 상단 좌측 탭에 뒤로가기 버튼이 활성화 되어 있어서 false 전환 2021.04.04 16:53
        //        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        //
        //        // Bottom Navigation 설정
        frBottom = (BottomNavigationView) findViewById(R.id.frBottom);


        content = (FrameLayout)findViewById(R.id.content_layout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(activity_main,content,true); // 1. 메인 페이지 xml 가져오기
        v1 = inflater.inflate(activity_main,null); //2. 메인 페이지를 뷰객체로 가져오기.


        MainTv_EMER = (Button) findViewById(R.id.mainTv_Emer);
        MainTv_EMER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeView(0);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // 상단 우측 탭 호출
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.tbEmer :
                changeView(0);
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

    private void changeView(int index) {
        // LayoutInflater 초기화.
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FrameLayout frame = (FrameLayout) findViewById(R.id.content_layout);
        if (frame.getChildCount() > 0) {
            // FrameLayout에서 뷰 삭제.
            frame.removeViewAt(0);
        }

        // XML에 작성된 레이아웃을 View 객체로 변환.
        View view = null;
        switch (index) {
            case 0:
                view = inflater.inflate(activity_emergency, frame, false);
                break;
            case 1:
                view = inflater.inflate(activity_fracture, frame, false);
                break;
        }

        // FrameLayout에 뷰 추가.
        if (view != null) {
            frame.addView(view);
        }
    }
}

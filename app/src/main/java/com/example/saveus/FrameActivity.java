package com.example.saveus;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FrameActivity extends AppCompatActivity {
    Toolbar toolBar;
    BottomNavigationView frBottom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        // Toolbar 설정
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);

        // Bottom Navigation 설정
        frBottom = (BottomNavigationView) findViewById(R.id.frBottom);
    }
}

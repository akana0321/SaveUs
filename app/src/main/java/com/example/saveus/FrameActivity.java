package com.example.saveus;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class FrameActivity extends AppCompatActivity {
    Toolbar FrToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        setTitle("메인화면");

        FrToolbar = (Toolbar) findViewById(R.id.frToolbar);
        setSupportActionBar(FrToolbar);
    }
}

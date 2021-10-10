package com.example.saveus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView MainTv_EMER, MainTv_AED, Main_MOUN,Main_PATI,Main_CONT;
    BottomNavigationView frBottom;
    private PermissionSupport permission;

    public static ArrayList<Activity> actList = new ArrayList<Activity>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("메인화면");

        permissionCheck();

        MainTv_EMER = (TextView) findViewById(R.id.mainTv_Emer);
        MainTv_AED = (TextView)findViewById(R.id.mainTv_AED);
        Main_MOUN = (TextView)findViewById(R.id.mainTV_Moun);
        Main_PATI = (TextView)findViewById(R.id.mainTv_Pati);
        Main_CONT = (TextView)findViewById(R.id.mainTv_Cont);
        frBottom = (BottomNavigationView) findViewById(R.id.frBottom);

        // 텍스트뷰 클릭시 인텐트 전환
        MainTv_EMER.setOnClickListener(new View.OnClickListener() {  // 응급처치 클릭시 응급상황 분류 페이지 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmergencyActivity.class);
                startActivity(intent);
            }
        });
        MainTv_AED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AedActivity.class);
                permissionCheck_gps(intent);
            }
        });
        Main_MOUN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                permissionCheck_gps(intent);
            }
        });
        Main_PATI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientActivity.class);
                permissionCheck_camera(intent);
            }
        });
        Main_CONT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                startActivity(intent);
            }
        });

        // 바텀 네이게이션 각 버튼 클릭시 실행.
        frBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.frMain :
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        // 활성화 되어 있는 모든 인텐트 삭제
                        for (int i = 0; i < actList.size(); i++)
                            actList.get(i).finish();
                        startActivity(intent);
                        finish();
                        return true;
                    default :
                        moveTaskToBack(true); // 태스크를 백그라운드로 이동
                        finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                        System.exit(0);
                        return true;
                }
            }
        });

    }

    // 권한 체크
    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            permission = new PermissionSupport(this, this);
            if (!permission.checkPermission()) {
                openAlert(); // 모든 권한이 허용되지 않았다면 권한 설정 화면으로 넘기기
            }
        }
    }

    // Request Permission에 대한 결과 값
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(!permission.permissionResult(requestCode, permissions, grantResults)) {
            openAlert();
        }
    }

    // 경고창
    private void openAlert() {
        AlertDialog.Builder permissionAlert = new AlertDialog.Builder(MainActivity.this);
        // alert의 title과 message 세팅
        permissionAlert.setTitle("미허용 권한 안내");
        permissionAlert.setMessage("모든 권한이 허용되지 않았습니다.\n" +
                "Cancle버튼을 누르면 앱이 종료되고, OK버튼을 누르시면 다시 어플리케이션 정보 화면으로 이동합니다.");
        // OK 버튼
        permissionAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openSettings();
            }
        });
        // Cancle 버튼
        permissionAlert.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true); // 태스크를 백그라운드로 이동
                finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                System.exit(0);
            }
        });
        // Alert를 생성해주고 보여주는 메소드(show를 선언해야 Alert가 생성됨)
        permissionAlert.show();
    }

    // 앱설정 페이지로 진입.
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    private boolean permissionCheck_gps(Intent intent) {
        // SDK 23버전 이하에서는 Permission이 필요하지 않음
        if (Build.VERSION.SDK_INT >= 23) {
            permission = new PermissionSupport(this, this);

            // 권한 체크한 후에 리턴이 false로 들어온다면 권한요청
            if (!permission.checkPermission_gps()) {
                permission.requestPermission();
                Toast.makeText(getApplicationContext(), "위치 권한 승인 후 다시 메뉴를 눌러주세요.", Toast.LENGTH_LONG).show();
            } else {
                startActivity(intent);
                return true;
            }
        }
        return true;
    }
    private boolean permissionCheck_camera(Intent intent) {
        // SDK 23버전 이하에서는 Permission이 필요하지 않음
        if (Build.VERSION.SDK_INT >= 23) {
            permission = new PermissionSupport(this, this);

            // 권한 체크한 후에 리턴이 false로 들어온다면 권한요청
            if (!permission.checkPermission_camera()) {
                permission.requestPermission();
                Toast.makeText(getApplicationContext(), "카메라 권한 승인 후 다시 메뉴를 눌러주세요.", Toast.LENGTH_LONG).show();
            } else {
                startActivity(intent);
                return true;
            }
        }
        return true;
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
                permissionCheck_gps(intent);
            case R.id.tbMoun:
                intent = new Intent(getApplicationContext(), ReportActivity.class);
                permissionCheck_gps(intent);
            case R.id.tbPati:
                intent = new Intent(getApplicationContext(), PatientActivity.class);
                permissionCheck_gps(intent);
            case R.id.tbCont:
                intent = new Intent(getApplicationContext(), ContactActivity.class);
                startActivity(intent); //문의하기 기능클릭시 페이지 전환
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

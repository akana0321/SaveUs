package com.example.saveus;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView MainTv_EMER, MainTv_AED, Main_MOUN,Main_PATI,Main_CONT;
    BottomNavigationView frBottom;

    public static ArrayList<Activity> actList = new ArrayList<Activity>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("메인화면");

        MainTv_EMER = (TextView) findViewById(R.id.mainTv_Emer);
        MainTv_AED = (TextView)findViewById(R.id.mainTv_AED);
        Main_MOUN = (TextView)findViewById(R.id.mainTV_Moun);
        Main_PATI = (TextView)findViewById(R.id.mainTv_Pati);
        Main_CONT = (TextView)findViewById(R.id.mainTv_Cont);
        frBottom = (BottomNavigationView) findViewById(R.id.frBottom);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_MMS,Manifest.permission.RECEIVE_SMS
                    )
                    .withListener(new MultiplePermissionsListener() { // 권한 여부를 다 묻고 실행되는 메소드
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            // check if all permissions are granted (모든 권한 부여를 확인하는 조건)
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(MainActivity.this, "모든 권한 허용", Toast.LENGTH_SHORT).show(); // 권한 허용 됐다고 메세지 출력.
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            //Toast.makeText(MainActivity.this, "list : "+list, Toast.LENGTH_LONG).show();        // 거부한 권한 항목이 저장된 list
                            showSettingsDialog(); // 거부하면 다얼로그 출력.
                        }
                    })
                    .check();
        }

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
                startActivity(intent);
            }
        });
        Main_MOUN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(intent);
            }
        });
        Main_PATI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientActivity.class);
                startActivity(intent);
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

    private void showSettingsDialog() {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("권한 허용 요청");
            builder.setMessage("이 기능을 사용하려면 앱에서 권한이 필요합니다. 앱 설정에서 부여할 수 있습니다.");
            builder.setPositiveButton("[앱 설정] 페이지로 이동", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    openSettings(); // 어플리케이션 정보 설정 페이지 띄움.
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }


    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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

package com.example.saveus;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.gun0912.tedpermission.PermissionListener; // 01. 마시멜로 권한 확인 라이브러리.
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;


public class AedActivity extends MainActivity {
    Context context;
    private MapFragmentActivity activityMap; //MapFragmentActivity 자바 파일 객체 생성
    private FragmentTransaction transaction;  //플레그먼트 화면 전환 객체생성
    private Fragment fragment = null; // 02. 플래그먼트 객체 생성.
    PermissionListener permissionlistener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aed);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("AED 위치");
        context = this.getBaseContext();

        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(context, "권한 허가가 되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(context, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        };

        if (Build.VERSION.SDK_INT >= 23) { // 마시멜로(안드로이드 6.0) 이상 권한 체크
            TedPermission.with(context).setPermissionListener(permissionlistener)
                    .setRationaleMessage("앱을 이용하기 위해서는 접근 권한이 필요합니다")
                    .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                    .setPermissions(new String[]{
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    //android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    //android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 기기, 사진, 미디어, 파일 엑세스 권한 }).check();}
                            });

            FragmentManager fragmentManager = getSupportFragmentManager(); // 플레그먼트 매니저 생성후
            activityMap = new MapFragmentActivity();                               // 해당 객체에 MapFragmnet.java 객체를 생성
            transaction = fragmentManager.beginTransaction();              // 플레그 먼트 매니저를 활용한 페이지 전환 객체 생성 후
            transaction.replace(R.id.map, activityMap).commitAllowingStateLoss();    // 현재 xml map 객체에 해당 activty_map xml 레이아웃 화면이 전환 되도록.
            //transaction.commit();

            /****************************************************
             ****************** 변수 선언부 *********************
             ****************************************************/

            /****************************************************
             *************** 인텐트 변환 메서드 ******************
             ****************************************************/


            // 바텀 네이게이션 각 버튼 클릭시 실행.
            BottomNavigationView frBottom = (BottomNavigationView) findViewById(R.id.frBottom);
            frBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.frBack:
                            finish();
                            return true;
                        case R.id.frMain:
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            // 활성화 되어 있는 모든 인텐트 삭제
                            for (int i = 0; i < actList.size(); i++)
                                actList.get(i).finish();
                            startActivity(intent);
                            finish();
                            return true;
                        case R.id.frExit:
                            moveTaskToBack(true); // 태스크를 백그라운드로 이동
                            finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                            System.exit(0);
                            return true;
                    }
                    return false;
                }
            });
        }
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
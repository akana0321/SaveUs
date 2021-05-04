package com.example.saveus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class AedActivity extends MainActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    GoogleMap gMap;
    final String TAG = "LogAedActivity";

    ArrayList aedOrg = new ArrayList<>(); // AED 설치기관 주된 이름
    ArrayList aedLat = new ArrayList<>();   // AED 위도
    ArrayList aedLng = new ArrayList<>();   // AED 경도
    ArrayList aedPlace = new ArrayList<>(); // AED 세부적지리 위치

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aed);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("AED 위치");


        // AED 파싱한 내용 배열에 저장하는 구문,
        try {
            JSONObject obj;
            obj = new JSONObject(getJsonString());
            JSONArray arr = obj.getJSONArray("item");
            for(int i = 0; i < arr.length(); i++) {
                JSONObject dataObj = arr.getJSONObject(i);
                aedLat.add(dataObj.getDouble("wgs84Lat"));
                aedLng.add(dataObj.getDouble("wgs84Lon"));
                aedOrg.add(dataObj.getString("org")); // AED 대표건물 값 받기
                aedPlace.add(dataObj.getString("buildPlace"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, String.valueOf(aedLat.size()));
        //Log.d(TAG, String.valueOf(aedLng.size()));


        /* aed 배열 리스트 값 확인을 위한 반복문.
        for(int i =0; i<aedLat.size();i++){
            System.out.println(aedLat.get(i));
        }
        */



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.tbEmer:
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.971567, 127.870491), 17));
        gMap.getUiSettings().setZoomControlsEnabled(true);


        //다중 마커 표시
        for(int idx =0; idx <aedPlace.size(); idx++){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng((Double) aedLat.get(idx), (Double) aedLng.get(idx)));
            markerOptions.title(aedOrg.get(idx) + "건물 \t" + aedPlace.get(idx));
            gMap.addMarker(markerOptions);
        }
        gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.5248,126.92723)));
    }

    private String getJsonString() {  //json 파싱 성공
        String ret = "";
        try {
            InputStream inputStream = getAssets().open("chungju.json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }
}

    /* 파싱 작업 전 원본주석
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

            //FragmentManager fragmentManager = getSupportFragmentManager(); // 플레그먼트 매니저 생성후
            //activityMap = new MapFragmentActivity();                               // 해당 객체에 MapFragmnet.java 객체를 생성
            //transaction = fragmentManager.beginTransaction();              // 플레그 먼트 매니저를 활용한 페이지 전환 객체 생성 후
            //transaction.replace(R.id.map, activityMap).commitAllowingStateLoss();    // 현재 raw map 객체에 해당 activty_map raw 레이아웃 화면이 전환 되도록.
            //transaction.commit();

            /****************************************************
             ****************** 변수 선언부 *********************
             ****************************************************/

/****************************************************
 *************** 인텐트 변환 메서드 ******************
 ****************************************************/

/*
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
    */
/*
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




/* 21.05.02 작업 코딩 확인 필요
public class AedActivity extends MainActivity implements OnMapReadyCallback{

    GoogleMap gMap;
    ArrayList<AedPoint> aedpoints;
    ArrayList<Location> aedpoint_address;
    Context context = this;
    final String TAG = "LogAedActivity";

    private ClusterManager<AedItem> clusterManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aed);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("AED 위치");

        aedpoints = (ArrayList<AedPoint>)getIntent().getSerializableExtra("aedsite");
        aedpoint_address = (ArrayList<Location>)getIntent().getSerializableExtra("aed_address");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.tbEmer:
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.971567, 127.870491), 17));
        gMap.getUiSettings().setZoomControlsEnabled(true);

        clusterManager = new ClusterManager<>(this,gMap);

        gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d(TAG,"Load");
                LatLng latLng = new LatLng(37.564214,127.001699);
                for(int i = 0 ; i < aedpoints.size(); i++) {
                    AedItem clinicItem = new AedItem(aedpoint_address.get(i).getLatitude(), aedpoint_address.get(i).getLongitude(),
                            aedpoints.get(i).getBuildPlace());
                    clusterManager.addItem(clinicItem);
                } // 개수만큼 item 추가
            }
        });

        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<AedItem>() {
            @Override
            public boolean onClusterClick(Cluster<AedItem> cluster) {

                LatLng latLng = new LatLng(cluster.getPosition().latitude,cluster.getPosition().longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
                gMap.moveCamera(cameraUpdate);
                return false;
            }
        });


        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String marker_number = null;
                for (int i = 0; i < aedpoints.size(); i++) {
                    if (aedpoints.get(i).findIndex(marker.getTitle()) != null) {
                        marker_number = aedpoints.get(i).findIndex(marker.getTitle());
                        Log.d(TAG, "marker_number " + marker_number);
                    }
                } // marker title로 clinic을 검색하여 number 반환받아옴
                final int marker_ID_number = Integer.parseInt(marker_number);
                Log.d(TAG, "marker number = " + String.valueOf(marker_ID_number));
                Log.d(TAG, "marker clinic name = " + aedpoints.get(marker_ID_number).getBuildPlace());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("병원정보");
                builder.setMessage(
                        "설치 장소: " + aedpoints.get(marker_ID_number - 1).getBuildPlace() +
                                "\n 설치 주소 : " + aedpoints.get(marker_ID_number - 1).getBuildAddress() +
                                "\n 전화번호 : " +aedpoints.get(marker_ID_number - 1).getClerkTel()
                );
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });// 마커 클릭 시 Alert Dialog가 나오도록 설정
    }

}

    /*
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

            //FragmentManager fragmentManager = getSupportFragmentManager(); // 플레그먼트 매니저 생성후
            //activityMap = new MapFragmentActivity();                               // 해당 객체에 MapFragmnet.java 객체를 생성
            //transaction = fragmentManager.beginTransaction();              // 플레그 먼트 매니저를 활용한 페이지 전환 객체 생성 후
            //transaction.replace(R.id.map, activityMap).commitAllowingStateLoss();    // 현재 raw map 객체에 해당 activty_map raw 레이아웃 화면이 전환 되도록.
            //transaction.commit();

            /****************************************************
             ****************** 변수 선언부 *********************
             ****************************************************/

            /****************************************************
             *************** 인텐트 변환 메서드 ******************
             ****************************************************/

/*
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
    */
/*
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

*/
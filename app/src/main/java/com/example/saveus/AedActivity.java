package com.example.saveus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AedActivity extends MainActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private FusedLocationProviderClient mFusedLocationProviderClient; // Deprecated된 FusedLocationApi를 대체
    private LocationRequest locationRequest;
    private Location mCurrentLocatiion;
    private Marker currentMarker = null;
    private final LatLng mDefaultLocation = new LatLng(37.56, 126.97);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000 * 60 * 15;  // LOG 찍어보니 이걸 주기로 하지 않는듯
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 30 ; // 30초 단위로 화면 갱신

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    GoogleMap gMap;
    final String TAG = "LogAedActivity";
    ArrayList aedOrg = new ArrayList<>(); // AED 설치기관 주된 이름
    ArrayList aedLat = new ArrayList<>();   // AED 위도
    ArrayList aedLng = new ArrayList<>();   // AED 경도
    ArrayList aedPlace = new ArrayList<>(); // AED 세부적지리 위치
    ClusterManager clusterManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aed);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("AED 위치");




        // AED 파싱한 내용 배열에 저장하는 구문, 경기도 AED 정보 저장하기
        try {
            JSONObject obj;
            obj = new JSONObject(getJsonString());
            JSONArray arr = obj.getJSONArray("item");
            for(int i = 0; i < arr.length(); i++) {
                JSONObject dataObj = arr.getJSONObject(i);
                aedPlace.add(dataObj.getString("buildPlace")); // AED 건물명 가져오기
                aedLat.add(dataObj.getDouble("wgs84Lat"));
                aedLng.add(dataObj.getDouble("wgs84Lon"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        /* 충주시 항목
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

        */
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

        clusterManager = new ClusterManager<>(this,gMap);

        /*
        //다중 마커 표시
        for(int idx =0; idx <aedPlace.size(); idx++){
            double offset = idx / 200d;
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng((Double) aedLat.get(idx), (Double) aedLng.get(idx)));
            markerOptions.title((String) aedPlace.get(idx));
            //markerOptions.title(aedOrg.get(idx) + "건물 \t" + aedPlace.get(idx));
            gMap.addMarker(markerOptions);
        }
         */

        gMap.setOnCameraIdleListener(clusterManager);
        gMap.setOnMarkerClickListener(clusterManager);
        setDefaultLocation();
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        // 화면 전환에 따른 마커 표시 실패
        gMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                VisibleRegion vr = gMap.getProjection().getVisibleRegion();
                double left = vr.latLngBounds.southwest.longitude;
                double top = vr.latLngBounds.northeast.latitude;
                double right = vr.latLngBounds.northeast.longitude;
                double bottom = vr.latLngBounds.southwest.latitude;
                Log.d(TAG,"ieft = " + String.valueOf(left)+ " top = " + String.valueOf(top)+" right = "+String.valueOf(right) + " bottom = "+String.valueOf(bottom));
                if(clusterManager != null) clusterManager.clearItems();
                findMarker(left,top,right,bottom);
            }
        });
        //addItems();// 클러스터 표시
        gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.5248,126.92723)));

        /*
        for(int i =0; i <aedPlace.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(Double.parseDouble(aedLat[i]), Double.parseDouble(aedLng[i])));
            markerOptions.title(aedPlace[i]);
            //markerOptions.title(aedOrg.get(idx) + "건물 \t" + aedPlace.get(idx));
            gMap.addMarker(markerOptions);

        }
        */

    }

    private String getJsonString() {  //json 파싱 성공
        String ret = "";
        try {
            InputStream inputStream = getAssets().open("Gyeonggi-do.json");

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

    private void updateLocationUI() {
        if (gMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                gMap.setMyLocationEnabled(true);
                gMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                gMap.setMyLocationEnabled(false);
                gMap.getUiSettings().setMyLocationButtonEnabled(false);
                mCurrentLocatiion = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setDefaultLocation() {
        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mDefaultLocation);
        markerOptions.title("위치정보 가져올 수 없음");
        markerOptions.snippet("위치 퍼미션과 GPS 활성 여부 확인하세요");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = gMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15);
        gMap.moveCamera(cameraUpdate);
    }

    String getCurrentAddress(LatLng latlng) {
        // 위치 정보와 지역으로부터 주소 문자열을 구한다.
        List<Address> addressList = null ;
        Geocoder geocoder = new Geocoder( this, Locale.getDefault());
        // 지오코더를 이용하여 주소 리스트를 구한다.
        try {
            addressList = geocoder.getFromLocation(latlng.latitude,latlng.longitude,1);
        } catch (IOException e) {
            Toast. makeText( this, "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
            return "주소 인식 불가" ;
        }
        if (addressList.size() < 1) { // 주소 리스트가 비어있는지 비어 있으면
            return "해당 위치에 주소 없음" ;
        }
        // 주소를 담는 문자열을 생성하고 리턴
        Address address = addressList.get(0);
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex())
                addressStringBuilder.append("\n");
        }
        return addressStringBuilder.toString();
    }
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);

                LatLng currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "Time :" + CurrentTime() + " onLocationResult : " + markerSnippet);

                // Update 주기를 확인해보려고 시간을 찍어보았음.
                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocatiion = location;
            }
        }
    };
    private String CurrentTime(){
        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        return time.format(today);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = gMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        gMap.moveCamera(cameraUpdate);
    }
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLocationPermissionGranted) {
            Log.d(TAG, "onStart : requestLocationUpdates");
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            if (gMap!=null)
                gMap.setMyLocationEnabled(true);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocationProviderClient != null) {
            Log.d(TAG, "onStop : removeLocationUpdates");
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFusedLocationProviderClient != null) {
            Log.d(TAG, "onDestroy : removeLocationUpdates");
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }


    //화면 전환에 마커 할당.
    public void findMarker(double left, double top, double right,double bottom){
        for(int i =0 ; i<aedPlace.size(); i++){
            if((Double) aedLng.get(i)>= left && (Double) aedLng.get(i)<= right){
                if((Double) aedLat.get(i)>= bottom &&(Double)aedLat.get(i)<=top){
                    AedItem offsetItem = new AedItem((Double) aedLat.get(i),(Double) aedLng.get(i),"위치");
                    clusterManager.addItem(offsetItem);
                }
            }
        }
    }


/*
    private void addItems() {  // 클러스터 표시
        for (int idx = 0; idx < aedPlace.size(); idx++) {
            double offset = idx / 60d;
            AedItem offsetItem = new AedItem(((Double) aedLat.get(idx))+offset,((Double) aedLng.get(idx))+offset,"Title" + idx);
            clusterManager.addItem(offsetItem);
        }
    }
    */

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
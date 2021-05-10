package com.example.saveus;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.w3c.dom.EntityReference;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;
import io.ticofab.androidgpxparser.parser.domain.WayPoint;

public class MountainActivity extends MainActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private GpsTracker gpsTracker; // 현위치를 가져오기 위해 이와 관련한 클래스 객체 생성
    private FusedLocationProviderClient mFusedLocationProviderClient; // Deprecated된 FusedLocationApi를 대체
    private LocationRequest locationRequest;
    private Location mCurrentLocatiion;
    private Marker currentMarker = null;
    //private final LatLng mDefaultLocation = new LatLng(36.4,127.05);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000 * 60 * 15;  // LOG 찍어보니 이걸 주기로 하지 않는듯
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 30; // 30초 단위로 화면 갱신

    GPXParser mParser = new GPXParser();
    GoogleMap gMap;
    final String TAG = "LogMountainActivity";
    List<LatLng> places = new ArrayList<LatLng>();
    ArrayList mountLat = new ArrayList<>(); // 등산로 위도 담을 배열.
    ArrayList mountLong = new ArrayList<>();// 등산로 경도 담을 배열.
    Polyline polyline1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mountain);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("등산 중 사고 신고");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Mount_map);
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
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.5248,126.92723)));
        setDefaultLocation(); // 초기 위치 -> 페이지 클릭시 곧바로 현위치 보여주는 메소드
        getLocationPermission(); // 권한 확인 메소드
        updateLocationUI();
        getDeviceLocation();


        // 답십리 공원 라인 파싱 후 도중 튕김.
        Gpx parsedGpx = null;
        try {
            InputStream in = getAssets().open("PMNTN_답십리공원_112300204.gpx");
            parsedGpx = mParser.parse(in); // consider using a background thread
        } catch (IOException | XmlPullParserException e) {
            Log.d(TAG,"파일 형식이 맞지 않습니다.");
        }
        if (parsedGpx == null) {
            Log.d(TAG,"파일이 담겨져 있지 않습니다.");
        } else {
            List<Track> tracks = parsedGpx.getTracks();
            for (int i = 0; i < tracks.size(); i++) {
                Track track = tracks.get(i);
                //Log.d(TAG, "track " + i + ":");
                List<TrackSegment> segments = track.getTrackSegments();
                for (int j = 0; j < segments.size(); j++) {
                    TrackSegment segment = segments.get(j);
                    //Log.d(TAG, "  segment " + j + ":");
                    for (TrackPoint trackPoint : segment.getTrackPoints()) {
                        Log.d(TAG, "    point: lat " + trackPoint.getLatitude() + ", lon " + trackPoint.getLongitude());
                        mountLat.add(trackPoint.getLatitude());
                        mountLat.add(trackPoint.getLongitude());
                    }
                }
            }
        }



        /*
        // 등산로 GPX파일_spot을 파싱하는 구문
        Gpx parsedGpx = null;
        try {
            InputStream in = getAssets().open("PMNTN_SPOT_답십리공원_112300204.gpx");
            parsedGpx = mParser.parse(in); // consider using a background thread
        } catch (IOException | XmlPullParserException e) {
            Log.d(TAG,"파일 형식이 맞지 않습니다.");
        }
        if (parsedGpx == null) {
            Log.d(TAG,"파일이 담겨져 있지 않습니다.");
        } else {
            List<WayPoint> wayPoints = parsedGpx.getWayPoints();
            for (int i = 0; i < wayPoints.size(); i++) {
                WayPoint wayPoint = wayPoints.get(i);
                //Log.d(TAG, String.valueOf(wayPoint.getLatitude()));
                //Log.d(TAG,String.valueOf(wayPoint.getLongitude()));
                mountLat.add(wayPoint.getLatitude());
                mountLong.add(wayPoint.getLongitude());
            }

        }

        */

        // 위도 경도를 같이 담아서 Arraylist 각각 분리된 위도 경도 삽입.
        for(int i= 0 ; i<mountLat.size(); i++){
            places.add(new LatLng((double)mountLat.get(i),(double)mountLong.get(i)));
        }

        // 라인선 그리는 작업
        for(int i=0; i<places.size()-1; i++){
            LatLng src = places.get(i);
            LatLng dest = places.get(i+1);
            Polyline line = gMap.addPolyline(new PolylineOptions().add(
                new LatLng(src.latitude,src.longitude),
                new LatLng(dest.latitude,dest.longitude)
            ).width(5).color(Color.BLUE).geodesic(true));
        }


        /* 실행 안됨
        for(int i = 0; i<mountLat.size(); i++){
            polyline1 = gMap.addPolyline(new PolylineOptions().clickable(true).add(
                    new LatLng((Double) mountLat.get(i),(Double)mountLong.get(i))
            ).width(10).color(Color.RED).geodesic(true));
            Log.d(TAG, String.valueOf(mountLat.get(i)));
            Log.d(TAG,String.valueOf(mountLong.get(i)));
        }
        */

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
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setDefaultLocation() {  //초기 페이지 전환시 현재위치 보여주는 메소드
        gpsTracker = new GpsTracker(MountainActivity.this);
        double latitude = gpsTracker.getLatitude(); // 위도
        double longitude = gpsTracker.getLongitude(); //경도

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("현위치");
        markerOptions.position(new LatLng(latitude, longitude));
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        currentMarker = gMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
        gMap.moveCamera(cameraUpdate);

        /* 교통대 공실관 경로
        Polyline polyline1 = gMap.addPolyline(new PolylineOptions().clickable(true).add(

                new LatLng(36.97161259371074, 127.87048475208792),
                new LatLng(36.97185329140523, 127.87080862186518),
                new LatLng(36.971787099615135, 127.87120781019526),
                new LatLng(36.97138393019593, 127.87108730051071),
                new LatLng(36.970932618611286, 127.87130572431396),
                new LatLng(36.97053546220316, 127.87199865500016),
                new LatLng(36.970066092867334, 127.87181789047332),
                new LatLng(36.96831978678853, 127.87158110745536),
                new LatLng(36.9667125901397, 127.87157234244111)).width(5).color(Color.RED).geodesic(true));
        */

    }

    String getCurrentAddress(LatLng latlng) {
        // 위치 정보와 지역으로부터 주소 문자열을 구한다.
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        // 지오코더를 이용하여 주소 리스트를 구한다.
        try {
            addressList = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        } catch (IOException e) {
            Toast.makeText(this, "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "주소 인식 불가";
        }
        if (addressList.size() < 1) { // 주소 리스트가 비어있는지 비어 있으면
            return "해당 위치에 주소 없음";
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

    private String CurrentTime() {
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
        } catch (SecurityException e) {
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
            if (gMap != null)
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


}



/* 이전 MapFraeMent방식
public class MountainActivity extends MainActivity {
    Context context;
    private MapFragmentActivity activityMap; //MapFragmentActivity 자바 파일 객체 생성
    private FragmentTransaction transaction;  //플레그먼트 화면 전환 객체생성
    private Fragment fragment = null; // 02. 플래그먼트 객체 생성.
    PermissionListener permissionlistener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mountain);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("등산 중 사고 신고");
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
            transaction.replace(R.id.mountainMap, activityMap).commitAllowingStateLoss();    // 현재 xml map 객체에 해당 activty_map xml 레이아웃 화면이 전환 되도록.
            //transaction.commit();

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
 */
package com.example.saveus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.ticofab.androidgpxparser.parser.GPXParser;

public class ReportActivity extends MainActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private GpsTracker gpsTracker; // 현위치를 가져오기 위해 이와 관련한 클래스 객체 생성
    private FusedLocationProviderClient mFusedLocationProviderClient; // Deprecated된 FusedLocationApi를 대체
    private LocationRequest locationRequest;
    private Location mCurrentLocatiion;
    private Marker currentMarker = null;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000 * 60 * 15;  // LOG 찍어보니 이걸 주기로 하지 않는듯
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 30; // 30초 단위로 화면 갱신

    GPXParser mParser = new GPXParser();
    GoogleMap gMap;
    final String TAG = "LogReportActivity";
    List<LatLng> places = new ArrayList<LatLng>();
    ArrayList mountLat = new ArrayList<>(); // 등산로 위도 담을 배열.
    ArrayList mountLong = new ArrayList<>();// 등산로 경도 담을 배열.


    Button Btn_Report_Location, Btn_Report_119;
    EditText OtherTypeAccident,AdditionalDelivery;
    Spinner TypeSpinner;
    Button NoBtn,SendBtn;
    Dialog dialog_119; // 119버튼 메뉴상자.
    String itemText = null, additon_Message = null; // 사고 유형 저장 문자열
    static Double Latitude , Longitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("위급 상황 신고");

        gpsTracker= new GpsTracker(ReportActivity.this);      // 가상머신 제대로 출력이 안되지만, 실제 폰은 출력 됨.
        Latitude = gpsTracker.getLatitude(); // 위도            // 위급 상황 신고 페이지가 열리면 바로 현재 위치 위도 경도 좌표 저장함.
        Longitude = gpsTracker.getLongitude(); //경도




        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Mount_map);
        mapFragment.getMapAsync(this);

        Btn_Report_Location = (Button)findViewById(R.id.btn_ReportLocation);
        Btn_Report_119 = (Button)findViewById(R.id.btn_Report_119);

        dialog_119 = new Dialog(ReportActivity.this);
        dialog_119.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_119.setContentView(R.layout.activity_dialog_119);

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

        Btn_Report_Location.setOnClickListener(new View.OnClickListener() { // 현재위치 버튼 클릭 시, 현위치의 위도와 경도 좌표 출력.
            @Override
            public void onClick(View v) {
                gpsTracker= new GpsTracker(ReportActivity.this);      // 가상머신 제대로 출력이 안되지만, 실제 폰은 출력 됨.
                double latitude = gpsTracker.getLatitude(); // 위도
                double longitude = gpsTracker.getLongitude(); //경도

                AlertDialog.Builder dlg  = new AlertDialog.Builder(ReportActivity.this);
                dlg.setTitle("현재 위치");
                dlg.setMessage("○ 위도:"+latitude +"\n○ 경도:" + longitude);
                dlg.setNegativeButton("닫기",null);
                dlg.show();
            }
        });
        Btn_Report_119.setOnClickListener(new View.OnClickListener() {  // 119 버튼 클릭 시, 실행되는 메소드.
            @Override
            public void onClick(View v) {
                showDialog_119();

            }
        });
    }

    public void showDialog_119(){
        dialog_119.show(); //119 신고 메소드(xml 저장 되어 있는 형태) 보여줌.

        OtherTypeAccident = dialog_119.findViewById(R.id.edt_other_type_accident);  // 기타 유형 사고 객체 선언 후 연결.
        AdditionalDelivery = dialog_119.findViewById(R.id.edt_Additional_delivery); // 추가 전달사항 객체 선언 후 연결.
        TypeSpinner = dialog_119.findViewById(R.id.TypeSpinner); // 유형 사고 드롭박스 객체 선언 후 연결.

        TypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                itemText = item.toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        OtherTypeAccident.addTextChangedListener(new TextWatcher() {  // 기타 유형 사고 선택시, 하위 항목 텍스트 값 전달.
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    itemText = OtherTypeAccident.getText().toString();
                }
            }
        });

        NoBtn = dialog_119.findViewById(R.id.noBtn);
        SendBtn = dialog_119.findViewById(R.id.SendBtn);
        NoBtn.setOnClickListener(new View.OnClickListener() { // 닫기 버튼 클릭시.
            @Override
            public void onClick(View v) {
                dialog_119.dismiss();
            }
        });

        SendBtn.setOnClickListener(new View.OnClickListener() { // 보내기 버튼 클릭시.
            @Override
            public void onClick(View v) {


                if(AdditionalDelivery.getText().toString().equals("")|| AdditionalDelivery.getText().toString()==null){
                    additon_Message = "신속한 이송 부탁드립니다.";  // 추가 전달 사항에 값이 없는 경우.
                }else {
                    additon_Message = AdditionalDelivery.getText().toString();
                }

                Log.d(TAG, "sendMMS(Method) : " + "start");
                String phone = "01077414253";
                String subject = "[긴급 구조 요청 신고]"; // MMS 제목 부분.
                String text = null; // MMS 내용 초기화.

                text = "[사고유형] " + itemText + "사고"+
                        "\n 사고 발생 위치 \n - 위도 : " + Latitude + "\n - 경도 : " + Longitude +
                        "\n 추가 전달 사항 \n " + additon_Message;

                Log.d(TAG, "subject : " + subject);
                Log.d(TAG, "text : " + text);

                Settings settings = new Settings();
                settings.setUseSystemSending(true);

                Transaction transaction = new Transaction(ReportActivity.this, settings);

                // 제목이 있을경우
                Message message = new Message(text, phone, subject);

                //제목이 없을경우
                //Message message = new Message(text, phone);

                long id = android.os.Process.getThreadPriority(android.os.Process.myTid());
                transaction.sendNewMessage(message, id);

            }
        });

    }

    /*
    public void sendMMS(String phone) {

        Log.d(TAG, "sendMMS(Method) : " + "start");
        String subject = "[긴급 구조 요청 신고]"; // MMS 제목 부분.
        String text = null; // MMS 내용 부분.

        text = "[사고유형] " + ietm
        // 예시 (절대경로) : String imagePath = "/storage/emulated/0/Pictures/Screenshots/Screenshot_20190312-181007.png";

        Log.d(TAG, "subject : " + subject);
        Log.d(TAG, "text : " + text);

        Settings settings = new Settings();
        settings.setUseSystemSending(true);

        // TODO : 이 Transaction 클래스를 위에 링크에서 다운받아서 써야함
        Transaction transaction = new Transaction(this, settings);

        // 제목이 있을경우
         Message message = new Message(text, phone, subject);

        //제목이 없을경우
        //Message message = new Message(text, phone);

        long id = android.os.Process.getThreadPriority(android.os.Process.myTid());
        transaction.sendNewMessage(message, id);
    }
    */
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


        /* Kml 파일 등산로 경로 표시
        try {
                InputStream in = getAssets().open("W2313110100.kml"); // 계명산_ 충청북도 충주시 목행동 소재 W2313110100.kml 적용시, 잘나타남.
                // 가리왕산_ 강원도 평창군 진부면 등산로 W2211050100.kml 적용시, 산 중반 부터 선이 형성되어 있어 확실치 않음.
                KmlLayer layer = new KmlLayer(gMap,in,getApplicationContext());
                layer.addLayerToMap();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        */


        /*
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
        */


        /*
        // 등산로 GPX파일_spot을 파싱하는 구문
        Gpx parsedGpx = null;
        try {
            InputStream in = getAssets().open("buk.gpx");
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
        gpsTracker = new GpsTracker(ReportActivity.this);
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

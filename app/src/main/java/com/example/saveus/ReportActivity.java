package com.example.saveus;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.LongDef;
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


public class ReportActivity extends MainActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private GpsTracker gpsTracker; // 현위치를 가져오기 위해 이와 관련한 클래스 객체 생성
    private FusedLocationProviderClient mFusedLocationProviderClient; // Deprecated된 FusedLocationApi를 대체
    private LocationRequest locationRequest;
    private Location mCurrentLocatiion;
    private Marker currentMarker = null;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int SMS_RECEIVE_PERMISSON=2;
    private boolean mLocationPermissionGranted;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000 * 60 * 15;  // LOG 찍어보니 이걸 주기로 하지 않는듯
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 30; // 30초 단위로 화면 갱신

    GoogleMap gMap;
    final String TAG = "LogReportActivity";

    Button Btn_Report_Location, Btn_Report_119;
    EditText OtherTypeAccident,AdditionalDelivery;
    TextView OtherTypeAccidentTv,ReportLatitude,ReportLongitude;
    Spinner TypeSpinner;
    Button NoBtn,SendBtn,BtnOK;
    Dialog dialog_119; // 119버튼 메뉴상자.
    Dialog dialog_CurrentLocation; // 현재 위치 버튼 메뉴 상자.
    String itemText = null, additon_Message = null; // 사고 유형, 추가전달사항 저장 문자열
    static Double Latitude , Longitude;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("위급 상황 신고");
        checkPermission(); // 21.06.08 권한 확인 메소드 생성
        gpsTracker= new GpsTracker(ReportActivity.this);      // 가상머신 제대로 출력이 안되지만, 실제 폰은 출력 됨.
        Latitude = gpsTracker.getLatitude(); // 위도                  // 위급 상황 신고 페이지가 열리면 바로 현재 위치 위도 경도 좌표 저장함.
        Longitude = gpsTracker.getLongitude(); //경도

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Mount_map);
        mapFragment.getMapAsync(this);

        Btn_Report_Location = (Button)findViewById(R.id.btn_ReportLocation);
        dialog_CurrentLocation = new Dialog(ReportActivity.this); // 현위치 대화상자 출력하기 위하기 위한 다이얼로그 생성.
        dialog_CurrentLocation.requestWindowFeature(Window.FEATURE_NO_TITLE);   // 현위치 대화상자 제목부분 삭제.
        dialog_CurrentLocation.setContentView(R.layout.activity_dialog_location); // 현위치 대화상자를 출력하기 위한 다이얼로그 xml 연결.

        Btn_Report_119 = (Button)findViewById(R.id.btn_Report_119);
        dialog_119 = new Dialog(ReportActivity.this);        // 119 대화상자를 출력하기 위한 다이얼로그 생성.
        dialog_119.requestWindowFeature(Window.FEATURE_NO_TITLE);    // 119 대화상자 제목부분 삭제.
        dialog_119.setContentView(R.layout.activity_dialog_119);     // 119 대화상자를 출력하기 위한 다이얼로그 xml 연결.

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

        Btn_Report_Location.setOnClickListener(new View.OnClickListener() { // 현재위치 버튼 클릭 시, 현위치의 위도와 경도 좌표 출력하기 위한 다이얼로그 호출.
            @Override
            public void onClick(View v) {
                showDialog_Location();  // 메소드를 통한 다이얼로그 호출.
            }
        });
        Btn_Report_119.setOnClickListener(new View.OnClickListener() {  // 119 버튼 클릭 시, 사고 유형 선택 하기 위한 다이얼로그 호출.
            @Override
            public void onClick(View v) {
                showDialog_119();  // 메소드를 통한 다이얼로그 호출.
            }
        });
    }

    public void showDialog_Location(){
        dialog_CurrentLocation.show(); // 현위치 다이얼로그 띄우기.
        dialog_CurrentLocation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 투명 배경.
        ReportLatitude = dialog_CurrentLocation.findViewById(R.id.Rep_Latitude);    // xml 객체 연결
        ReportLongitude = dialog_CurrentLocation.findViewById(R.id.Rep_Longitude);  // xml 객체 연결

        gpsTracker= new GpsTracker(ReportActivity.this);      // 가상머신 제대로 출력이 안되지만, 실제 폰은 출력 됨.
        double latitude = gpsTracker.getLatitude(); // 위도
        double longitude = gpsTracker.getLongitude(); //경도

        ReportLatitude.setText(String.valueOf(latitude)); // 경도 값을 텍스트 뷰에 삽입.
        ReportLongitude.setText(String.valueOf(longitude)); // 위도 값을 텍스트 뷰에 삽입.

        BtnOK = dialog_CurrentLocation.findViewById(R.id.btn_OK);
        BtnOK.setOnClickListener(new View.OnClickListener() {   // 확인 버튼 클릭시 다이얼로그 종료하기.
            @Override
            public void onClick(View v) {
                dialog_CurrentLocation.dismiss();
            }
        });
    }

    public void showDialog_119(){
        dialog_119.show(); //119 신고 다이얼로그 띄우기.
        OtherTypeAccidentTv = dialog_119.findViewById(R.id.tv_Other_Type_accident); // 기타 사고 유형 텍스트뷰
        OtherTypeAccident = dialog_119.findViewById(R.id.edt_other_type_accident);  // 기타 사고 유형 객체 선언 후 연결.
        AdditionalDelivery = dialog_119.findViewById(R.id.edt_Additional_delivery); // 추가 전달사항 객체 선언 후 연결.
        TypeSpinner = dialog_119.findViewById(R.id.TypeSpinner); // 유형 사고 드롭박스 객체 선언 후 연결.

        TypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                itemText = item.toString();  // 사고 유형 선택시, 값 저장.
                if (itemText.equals("기타")) {
                    OtherTypeAccidentTv.setVisibility(View.VISIBLE);
                    OtherTypeAccident.setVisibility(View.VISIBLE);
                }
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

        additon_Message = "신속한 이송 부탁드립니다."; // 추가 전달사항 초기값 설정.
        AdditionalDelivery.addTextChangedListener(new TextWatcher() { // 추가 전달사항 항목을 입력하면, 내용이 저장.
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                additon_Message = AdditionalDelivery.getText().toString();
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
            public void onClick(View v) {   //  라이브러리 통한  MMS 메세지 전송.
                int SMSpermissonCheck = ContextCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.RECEIVE_SMS);
                if (SMSpermissonCheck == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "SMS 권한이 허용되지 않았습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "sendMMS(Method) : " + "start");
                    String phone = "01077414253"; // 전화번호 설정
                    String subject = "[긴급 구조 요청 신고]"; // MMS 제목 부분.
                    String text = null; // MMS 내용 초기화.

                    text = "[사고유형] " + itemText + "사고" +
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
                    Toast.makeText(getApplicationContext(), "메세지를 전송하였습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /* sendMMS 메소드를 선언을 통해서도 전송할 수 있음.
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
        updateLocationUI();
        getDeviceLocation();
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

    private void checkPermission() { // SMS 권한은 총 4가지로 구성되어 있지만, 4가지 권한이 모두 존재해야지 수행 가능하므로 임의로 한개 지정.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            showSettingsDialog();
        }
    }
    private void showSettingsDialog() { // 권한 없는 상태에서 진입시 1차적으로 안내창이 뜨도록 함.

        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
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
                moveTaskToBack(true); // 태스크를 백그라운드로 이동
                finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                System.exit(0);
                //dialog.cancel();
            }
        });
        builder.show();
    }
    private void openSettings() { // 앱설정 페이지로 진입.
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}


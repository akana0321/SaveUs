package com.example.saveus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class MapFragmentActivity extends Fragment implements OnMapReadyCallback {

    private MapView mapView; // txt 파일 출처 표기 / activty_map xml 파일에 해당하는 맵뷰 객체 생성
    private GoogleMap googleMap; // 구글 맵 객체 생성

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.AED_map); // activty_map xml 파일 맵 뷰 id 인 AED_map 객체 연결
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); // 위성 모드 설정.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.971567,127.870491),17)); // 위도. 경도, 줌 배율 설정.
        googleMap.getUiSettings().setZoomControlsEnabled(true); // 줌 확대 기능 설정.

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 마크 (표시 설정하는 ) 메서드 부분
            }
        });
    }
}

package com.example.bumap;

import android.Manifest;
import android.content.Intent;  // Intent import 추가
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 지도 프래그먼트 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 경로 탐색 버튼 설정
        Button routeButton = findViewById(R.id.route_button);
        routeButton.setOnClickListener(v -> {
            // 경로 탐색 화면으로 이동
            Intent intent = new Intent(MainActivity.this, RouteActivity.class);  // TransitActivity 대신 RouteActivity
            startActivity(intent);  // 경로 탐색 화면으로 이동
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 위치 권한 체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // 권한이 허용되면 내 위치 표시
        mMap.setMyLocationEnabled(true);
        showCurrentLocation();
    }

    private void showCurrentLocation() {
        // 위치 권한이 허용된 경우에만 위치를 받아옴
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // 위치 정보가 있으면 현재 위치 표시
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("현재 위치"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        } else {
                            Log.e("Location Error", "현재 위치를 가져올 수 없습니다.");
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Log.e("Location Error", "위치 정보를 가져오는 데 실패했습니다.", e);
                    });
        } else {
            // 위치 권한이 없으면 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우 위치 표시
                showCurrentLocation();
            } else {
                // 권한 거부된 경우
                Log.e("Permission Error", "위치 권한이 허용되지 않았습니다.");
            }
        }
    }
}

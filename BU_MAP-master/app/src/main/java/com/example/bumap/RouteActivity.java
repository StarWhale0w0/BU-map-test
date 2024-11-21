package com.example.bumap;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.shapes.GHPoint;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GraphHopper hopper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        // MapFragment를 이용하여 지도 설정
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // GraphHopper 설정
        hopper = new GraphHopper();
        hopper.setGraphHopperLocation(getFilesDir().getAbsolutePath() + "/graphhopper");
        hopper.setEncodingManager("car"); // 예시: 자동차 경로 탐색
        hopper.importOrLoad();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 출발점과 도착점 설정 (예시)
        GHRequest request = new GHRequest(new GHPoint(37.7749, -122.4194), new GHPoint(34.0522, -118.2437)); // 샌프란시스코 -> LA
        request.setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);

        GHResponse response = hopper.route(request);

        if (response.hasErrors()) {
            // 경로 탐색 오류 처리
            return;
        }

        // 경로가 정상적으로 계산되면, 지도에 경로를 표시
        mMap.addMarker(new MarkerOptions().position(new LatLng(37.7749, -122.4194)).title("Start"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(34.0522, -118.2437)).title("End"));

        // 카메라를 첫 번째 마커로 이동
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.7749, -122.4194), 10));
    }
}

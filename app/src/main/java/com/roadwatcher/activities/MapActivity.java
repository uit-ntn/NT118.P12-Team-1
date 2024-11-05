package com.roadwatcher.activities;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IMapController;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cấu hình osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

        // Khởi tạo MapView
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK); // Chọn nguồn bản đồ OpenStreetMap

        // Cài đặt zoom và điều khiển
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Đặt vị trí trung tâm bản đồ (vị trí tùy chọn)
        GeoPoint startPoint = new GeoPoint(10.880973, 106.796500);
        mapController.setCenter(startPoint);

        // Tạo một Polygon từ dữ liệu GeoJSON
        String geoJsonString = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[106.7749607285711,10.887605873915675],[106.7797193017766,10.889602840281043],[106.78439653184887,10.890801013672188],[106.78626742387723,10.89068119655039],[106.78943980601383,10.892358631873492],[106.79098532551609,10.89407599634319],[106.7949711389698,10.89267814229295],[106.80489500103596,10.890601318442009],[106.80664387836725,10.887565934452425],[106.81026364772828,10.88596835150723],[106.81465617683978,10.881934416448885],[106.81306998577219,10.879258409602315],[106.81083304964977,10.875943323034605],[106.80805802139668,10.872467602166026],[106.80548772612673,10.868600783254337],[106.79954796269061,10.865141196057323],[106.79749203407243,10.86453830528474],[106.79575809865054,10.86453830528474],[106.79392012710372,10.865185389712423],[106.79239426393173,10.866173042185167],[106.79020950529866,10.86709257775864],[106.7883715337519,10.867228805009049],[106.7839326590722,10.86729691861143],[106.78254551073428,10.868148337328535],[106.78046478822614,10.868216450721135],[106.78032607339446,10.869340319452206],[106.78046478822614,10.869987393467397],[106.7819212939823,10.873324910840779],[106.78251083202514,10.874687152098744],[106.78285761910905,10.876117498723119],[106.78327376361113,10.877071059327193],[106.78334312102703,10.877513782857278],[106.7828922978182,10.87819489469662],[106.78181725785737,10.879046282307343],[106.78093603027884,10.880395129581146],[106.78086667286067,10.881144345276411],[106.77996502644066,10.88223411020067],[106.7777792638692,10.883864394577913],[106.7750259570372,10.887664671143526],[106.77516982828712,10.887664671143526],[106.77588918453944,10.885828006220919],[106.7749607285711,10.887605873915675]]]}]}";

        try {
            JSONObject geoJson = new JSONObject(geoJsonString);
            JSONArray coordinates = geoJson.getJSONArray("features")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates")
                    .getJSONArray(0);

            // Chuyển đổi tọa độ từ GeoJSON sang danh sách các GeoPoint
            List<GeoPoint> points = new ArrayList<>();
            double minLat = Double.MAX_VALUE;
            double maxLat = Double.MIN_VALUE;
            double minLon = Double.MAX_VALUE;
            double maxLon = Double.MIN_VALUE;

            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray coordinate = coordinates.getJSONArray(i);
                double lon = coordinate.getDouble(0);
                double lat = coordinate.getDouble(1);
                points.add(new GeoPoint(lat, lon));

                // Tính toán giới hạn vùng
                if (lat < minLat) minLat = lat;
                if (lat > maxLat) maxLat = lat;
                if (lon < minLon) minLon = lon;
                if (lon > maxLon) maxLon = lon;
            }


            // Tạo BoundingBox từ các giá trị nhỏ nhất và lớn nhất
            BoundingBox boundingBox = new BoundingBox(maxLat, maxLon, minLat, minLon);
            mapView.zoomToBoundingBox(boundingBox, true);  // Zoom vào khu vực polygon
            mapView.setScrollableAreaLimitDouble(boundingBox);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); // Kích hoạt lại MapView khi quay lại màn hình
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause(); // Tạm dừng MapView khi thoát màn hình
    }
}

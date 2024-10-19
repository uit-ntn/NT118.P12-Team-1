package com.roadwatcher;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;  // Import dòng này
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class Map extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get API key from BuildConfig (make sure it is defined in build.gradle)
        String mapTilerApiKey = BuildConfig.MAPTILER_API_KEY;

        // Define the map style URL from MapTiler
        String mapId = "streets-v2"; // This can be any other MapTiler style
        String styleUrl = "https://api.maptiler.com/maps/" + mapId + "/style.json?key=" + mapTilerApiKey;

        // Initialize MapLibre SDK
        Mapbox.getInstance(this);

        // Set content view to the layout
        setContentView(R.layout.activity_map);

        // Initialize the MapView
        mapView = findViewById(R.id.mapView);

        // Set up the map
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            mapboxMap.setStyle(new Style.Builder().fromUri(styleUrl), style -> {
                // Set the initial camera position
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(0.0, 0.0)) // LatLng for the center of the map
                        .zoom(1.0) // Initial zoom level
                        .build());

                // Load dataset from MapTiler
                loadDataset(mapboxMap);
            });
        });
    }

    // Hàm load dataset từ MapTiler
    public void loadDataset(MapboxMap mapboxMap) {
        OkHttpClient client = new OkHttpClient();

        // Sửa lại URL đúng cách
        String datasetUrl = "https://api.maptiler.com/data/6ed61464-2e27-4eab-8ff6-1ce394758ad1/features.json?key=" + BuildConfig.MAPTILER_API_KEY;
        Request request = new Request.Builder()
                .url(datasetUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Xử lý khi yêu cầu thất bại
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Xử lý dữ liệu dataset ở đây
                    String jsonResponse = response.body().string();

                    // Chuyển jsonResponse thành GeoJSON
                    runOnUiThread(() -> {
                        try {
                            GeoJsonSource geoJsonSource = new GeoJsonSource("dataset-source-id", jsonResponse);
                            mapboxMap.getStyle(style -> {
                                style.addSource(geoJsonSource);

                                // Add layer để hiển thị dataset (ví dụ: SymbolLayer)
                                SymbolLayer symbolLayer = new SymbolLayer("dataset-layer-id", "dataset-source-id");
                                symbolLayer.withProperties(
                                        iconImage("marker-icon-id"),  // Đặt biểu tượng nếu là marker
                                        iconSize(1.5f)
                                );
                                style.addLayer(symbolLayer);

                                // Đặt camera tại vị trí trung tâm của dataset
                                LatLng datasetCenter = new LatLng(10.878602990014551, 106.79159714460091);  // Thay bằng tọa độ dataset
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(datasetCenter)
                                        .zoom(12)  // Độ zoom phù hợp với dataset của bạn
                                        .build();
                                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    // Các sự kiện vòng đời của MapView
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}

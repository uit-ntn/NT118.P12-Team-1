package com.roadwatcher.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.roadwatcher.R;
import com.roadwatcher.api.ApiClient;
import com.roadwatcher.api.PotholeApiService;
import com.roadwatcher.models.Pothole;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IMapController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private EditText ipSearchBar;
    private Button findRouteButton;
    private Polyline routeOverlay;
    private PotholeApiService potholeApiService;
    private IMapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Add MyLocation overlay to show and track user location
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Zoom vào vị trí hiện tại khi bản đồ được tải
        myLocationOverlay.runOnFirstFix(() -> {
            GeoPoint myLocation = myLocationOverlay.getMyLocation();
            if (myLocation != null) {
                runOnUiThread(() -> {
                    mapController.setCenter(myLocation);
                    mapController.setZoom(18.0); // Tùy chỉnh mức độ zoom theo nhu cầu
                });
            }
        });

        // Initialize Retrofit API client
        potholeApiService = ApiClient.getClient().create(PotholeApiService.class);

        // Fetch potholes from API
        fetchPotholesAndDisplay();

        // Setup search and route functionality
        ipSearchBar = findViewById(R.id.ip_search_bar);
        findRouteButton = findViewById(R.id.find_route);
        findRouteButton.setOnClickListener(v -> searchAndRoute());

        ipSearchBar.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                searchAndRoute();
                return true;
            }
            return false;
        });

        Button btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        btnCurrentLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                if (myLocationOverlay.getMyLocation() != null) {
                    mapController.setCenter(myLocationOverlay.getMyLocation());
                    mapController.setZoom(18.0); // Tùy chỉnh mức độ zoom theo nhu cầu
                    Toast.makeText(MapActivity.this, "Di chuyển đến vị trí hiện tại của bạn", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapActivity.this, "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                }
            }
        });



        Button goToUserButton = findViewById(R.id.go_to_user);
        Button goToHomeButton = findViewById(R.id.go_to_home);

        goToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

        goToUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchPotholesAndDisplay() {
        Call<List<Pothole>> call = potholeApiService.getAllPotholes();
        call.enqueue(new Callback<List<Pothole>>() {
            @Override
            public void onResponse(Call<List<Pothole>> call, retrofit2.Response<List<Pothole>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pothole> potholes = response.body();
                    Log.d("API Response", "Number of potholes: " + potholes.size());

                    runOnUiThread(() -> {
                        for (Pothole pothole : potholes) {
                            Log.d("Pothole Location", "Lat: " + pothole.getLatitude() + ", Lon: " + pothole.getLongitude());
                            addPotholeMarker(new GeoPoint(pothole.getLatitude(), pothole.getLongitude()), pothole.getSeverity());
                        }
                        // Làm mới mapView sau khi thêm tất cả các marker
                        mapView.invalidate();
                    });
                } else {
                    Log.e("API Response", "Response unsuccessful or body is null");
                    Toast.makeText(MapActivity.this, "Không thể tải dữ liệu ổ gà", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pothole>> call, Throwable t) {
                Log.e("API Error", "Error fetching potholes", t);
                Toast.makeText(MapActivity.this, "Lỗi khi kết nối với server", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addPotholeMarker(GeoPoint location, String severity) {
        Marker marker = new Marker(mapView);
        marker.setPosition(location);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Vị trí ổ gà");
        marker.setSnippet("Mức độ nghiêm trọng: " + severity);

        // Log để xác nhận tọa độ và icon
        Log.d("Marker", "Adding marker at Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());

        // Sử dụng icon custom_marker
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pothole_marker, null));

        // Thêm marker vào mapView nhưng không invalidate tại đây
        mapView.getOverlays().add(marker);
    }



    private void searchAndRoute() {
        String destinationName = ipSearchBar.getText().toString().trim();
        if (destinationName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên điểm đến", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(destinationName, 1);
            if (addresses == null || addresses.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy địa điểm", Toast.LENGTH_SHORT).show();
                return;
            }
            Address destination = addresses.get(0);
            GeoPoint destinationPoint = new GeoPoint(destination.getLatitude(), destination.getLongitude());

            // Zoom vào vị trí đã tìm kiếm
            mapController.setCenter(destinationPoint);
            mapController.setZoom(18.0); // Tùy chỉnh mức độ zoom theo nhu cầu

            GeoPoint startPoint = myLocationOverlay.getMyLocation();
            if (startPoint == null) {
                Toast.makeText(this, "Không thể xác định vị trí hiện tại", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "https://router.project-osrm.org/route/v1/driving/"
                    + startPoint.getLongitude() + "," + startPoint.getLatitude() + ";"
                    + destinationPoint.getLongitude() + "," + destinationPoint.getLatitude() + "?overview=full&geometries=geojson";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(MapActivity.this, "Không thể tìm đường", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (!response.isSuccessful() || response.body() == null) {
                        runOnUiThread(() -> Toast.makeText(MapActivity.this, "Lỗi khi tìm đường", Toast.LENGTH_SHORT).show());
                        return;
                    }
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray coordinates = json.getJSONArray("routes")
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONArray("coordinates");

                        List<GeoPoint> routePoints = new ArrayList<>();
                        for (int i = 0; i < coordinates.length(); i++) {
                            JSONArray coord = coordinates.getJSONArray(i);
                            routePoints.add(new GeoPoint(coord.getDouble(1), coord.getDouble(0)));
                        }

                        runOnUiThread(() -> {
                            if (routeOverlay != null) {
                                mapView.getOverlays().remove(routeOverlay);
                            }
                            routeOverlay = new Polyline();
                            routeOverlay.setPoints(routePoints);
                            routeOverlay.getOutlinePaint().setColor(Color.BLUE);
                            routeOverlay.getOutlinePaint().setStrokeWidth(10.0f);

                            mapView.getOverlays().add(routeOverlay);
                            mapView.invalidate();
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tìm kiếm địa điểm", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            findViewById(R.id.btnCurrentLocation).performClick();
        } else {
            Toast.makeText(this, "Cần cấp quyền vị trí để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
        }
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
}

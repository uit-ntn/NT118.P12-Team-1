package com.roadwatcher.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.view.KeyEvent;
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
import com.roadwatcher.https.GetAllPotholesResponse;
import com.roadwatcher.models.Pothole;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IMapController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

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

        // Zoom to current location
        myLocationOverlay.runOnFirstFix(() -> {
            GeoPoint myLocation = myLocationOverlay.getMyLocation();
            if (myLocation != null) {
                runOnUiThread(() -> {
                    mapController.setCenter(myLocation);
                    mapController.setZoom(18.0);
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
                    mapController.setZoom(18.0);
                    Toast.makeText(MapActivity.this, "Di chuyển đến vị trí hiện tại của bạn", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapActivity.this, "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button goToUserButton = findViewById(R.id.go_to_user);
        Button goToHomeButton = findViewById(R.id.go_to_home);

        goToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        goToUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, UserActivity.class);
            startActivity(intent);
        });
    }
    private void fetchPotholesAndDisplay() {
        Call<GetAllPotholesResponse> call = potholeApiService.getAllPotholes();
        call.enqueue(new Callback<GetAllPotholesResponse>() {
            @Override
            public void onResponse(Call<GetAllPotholesResponse> call, retrofit2.Response<GetAllPotholesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pothole> potholes = response.body().getPotholes();

                    runOnUiThread(() -> {
                        for (Pothole pothole : potholes) {
                            addPotholeMarker(new GeoPoint(pothole.getLatitude(), pothole.getLongitude()), pothole.getSeverity());
                        }
                        mapView.invalidate();
                    });
                } else {
                    Log.e("Fetch Error", "Failed to fetch potholes");
                    Toast.makeText(MapActivity.this, "Không thể tải dữ liệu ổ gà", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetAllPotholesResponse> call, Throwable t) {
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
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pothole_marker, null));
        mapView.getOverlays().add(marker);
    }

    private void searchAndRoute() {
        // Logic for search and routing (similar to your previous implementation)
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

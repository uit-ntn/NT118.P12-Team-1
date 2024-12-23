package com.roadwatcher.activities;

import static android.provider.Settings.System.DATE_FORMAT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.roadwatcher.R;
import com.roadwatcher.api.ApiClient;
import com.roadwatcher.api.PotholeApiService;
import com.roadwatcher.models.CreatePotholeResponse;
import com.roadwatcher.models.CreatePotholeRequest;
import com.roadwatcher.models.Pothole;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IMapController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private EditText ipSearchBar;
    private Button findRouteButton;
    private Polyline routeOverlay;
    private PotholeApiService potholeApiService;
    private IMapController mapController;
    private ListView suggestionsList;
    private ArrayAdapter<String> suggestionsAdapter;
    private List<String> suggestions = new ArrayList<>();

    // Danh sách Pothole để check cảnh báo
    // ------------------------------------------------------
    private List<Pothole> listOfPotholes = new ArrayList<>();

    private static final long SEARCH_DELAY = 500;
    private static final float WARNING_DISTANCE_METERS = 25.0f;
    private static final long WARNING_INTERVAL = 30_000;

    // Format thời gian: yyyy-MM-dd HH:mm:ss
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    // ------------------------------------------------------
    // Âm thanh cảnh báo
    // ------------------------------------------------------
    private MediaPlayer warningPlayer;
    private long lastWarningTime = 0;


    // ------------------------------------------------------
    // Accelerometer: Phát hiện rung lắc
    // ------------------------------------------------------
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime = 0;
    private static final long SHAKE_DEBOUNCE_MS = 2000;


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


        // Sử dụng GpsMyLocationProvider để theo dõi vị trí
        GpsMyLocationProvider locationProvider = new GpsMyLocationProvider(this);
        locationProvider.setLocationUpdateMinTime(200); // Thời gian tối thiểu giữa các lần cập nhật (ms)
        locationProvider.setLocationUpdateMinDistance(10); // Khoảng cách tối thiểu giữa các lần cập nhậ



        // Add MyLocation overlay to show and track user location
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Bắt đầu theo dõi vị trí thông qua GpsMyLocationProvider
        locationProvider.startLocationProvider((location, provider) -> {
            if (location != null) {
                // Gọi hàm kiểm tra ổ gà
                checkPotholeProximity(location);

                // Log vị trí hiện tại
                Log.d("LocationChanged", "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
            }
        });


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



        // Trong onCreate(), bạn chưa khởi tạo cảm biến
        initAccelerometer();

        // Initialize Retrofit API client
        potholeApiService = ApiClient.getClient().create(PotholeApiService.class);

        // Fetch potholes from API
        fetchPotholesAndDisplay();

        // Setup search and route functionality
        ipSearchBar = findViewById(R.id.ip_search_bar);
        ipSearchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TextWatcher", "User typing: " + s.toString());
                if (s.length() > 0) {
                    fetchSuggestions(s.toString()); // Lấy gợi ý theo nội dung nhập vào
                } else {
                    suggestions.clear();
                    suggestionsAdapter.notifyDataSetChanged();
                    suggestionsList.setVisibility(View.GONE); // Ẩn danh sách nếu không có gợi ý
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });


        suggestionsList = findViewById(R.id.suggestions_list);
        suggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        suggestionsList.setAdapter(suggestionsAdapter);
        // Kiểm tra Adapter
        Log.d("Adapter", "Adapter initialized with item count: " + suggestionsAdapter.getCount());


        // Xử lý sự kiện khi người dùng chọn một gợi ý
        suggestionsList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSuggestion = suggestions.get(position);
            Log.d("ListView", "Selected suggestion: " + selectedSuggestion);
            ipSearchBar.setText(selectedSuggestion); // Hiển thị gợi ý đã chọn vào thanh tìm kiếm
            suggestionsList.setVisibility(View.GONE); // Ẩn danh sách gợi ý
            searchAndRoute(selectedSuggestion); // Thực hiện tìm kiếm và tìm đường với gợi ý được chọn
        });





        // Chuẩn bị âm thanh cảnh báo
        warningPlayer = MediaPlayer.create(this, R.raw.warning_sound);

        findRouteButton = findViewById(R.id.find_route);
        findRouteButton.setOnClickListener(v -> {
            String destination = ipSearchBar.getText().toString().trim();
            searchAndRoute(destination);
        });

        ipSearchBar.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String destination = ipSearchBar.getText().toString().trim();
                searchAndRoute(destination);
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

    private void fetchSuggestions(String query) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(query, 3); // Lấy tối đa 3 gợi ý
            suggestions.clear();

            if (addresses != null && !addresses.isEmpty()) {
                for (Address address : addresses) {
                    String suggestion = address.getAddressLine(0); // Lấy địa chỉ đầy đủ
                    suggestions.add(suggestion);
                    Log.d("fetchSuggestions", "Suggestion: " + suggestion); // Log kiểm tra
                }
                suggestionsAdapter.notifyDataSetChanged();
                suggestionsList.setVisibility(View.VISIBLE); // Hiển thị danh sách gợi ý
            } else {
                Log.d("fetchSuggestions", "No suggestions found");
                suggestionsList.setVisibility(View.GONE); // Ẩn nếu không có gợi ý
            }
        } catch (IOException e) {
            Log.e("fetchSuggestions", "Error fetching suggestions", e);
            suggestionsList.setVisibility(View.GONE); // Ẩn nếu xảy ra lỗi
        }
    }




    // Kiểm tra khoảng cách tới ổ gà => âm thanh cảnh báo
    // ------------------------------------------------------
    private void checkPotholeProximity(Location currentLocation) {
        if (System.currentTimeMillis() - lastWarningTime < WARNING_INTERVAL) {
            Log.d("PotholeWarning", "Skipping warning due to interval limit.");
            return;
        }

        for (Pothole pothole : listOfPotholes) {
            Location potholeLocation = new Location("");
            potholeLocation.setLatitude(pothole.getLatitude());
            potholeLocation.setLongitude(pothole.getLongitude());

            float distance = currentLocation.distanceTo(potholeLocation);

            // Log thông tin chi tiết
            Log.d("PotholeCheck", String.format(
                    "Checking pothole at Lat: %.6f, Lon: %.6f, Severity: %s, Distance: %.2f meters",
                    pothole.getLatitude(), pothole.getLongitude(), pothole.getSeverity(), distance
            ));

            if (distance <= WARNING_DISTANCE_METERS) {
                Log.d("PotholeWarning", "Pothole detected within warning distance!");

                // Phát âm thanh cảnh báo
                if (warningPlayer != null && !warningPlayer.isPlaying()) {
                    warningPlayer.start();
                }

                // Hiển thị thông báo
                Toast.makeText(this, "Cảnh báo: Sắp tới ổ gà!", Toast.LENGTH_SHORT).show();

                // Cập nhật thời gian cảnh báo
                lastWarningTime = System.currentTimeMillis();

                // Thoát khỏi vòng lặp sau khi cảnh báo
                break;
            }
        }
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


    private void initAccelerometer() {
        try {
            // Khởi tạo SensorManager
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            if (sensorManager != null) {
                // Lấy cảm biến gia tốc
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                if (accelerometer != null) {
                    // Đăng ký lắng nghe sự kiện từ cảm biến
                    boolean isRegistered = sensorManager.registerListener(
                            sensorEventListener,
                            accelerometer,
                            SensorManager.SENSOR_DELAY_NORMAL
                    );

                    if (isRegistered) {
                        Log.d("Sensor", "Đã khởi tạo cảm biến thành công");
                    } else {
                        Log.e("Sensor", "Không thể đăng ký lắng nghe cảm biến");
                    }
                } else {
                    Log.e("Sensor", "Thiết bị không có cảm biến gia tốc");
                    Toast.makeText(this, "Thiết bị không có cảm biến gia tốc", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Sensor", "Không thể khởi tạo SensorManager");
            }
        } catch (Exception e) {
            Log.e("Sensor", "Lỗi khởi tạo cảm biến: " + e.getMessage());
            e.printStackTrace();
        }
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



    private void searchAndRoute(String destinationName) {
        if (destinationName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên điểm đến", Toast.LENGTH_SHORT).show();
            return;
        }
        runOnUiThread(() -> suggestionsList.setVisibility(View.VISIBLE));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(destinationName, 1);
            if (addresses == null || addresses.isEmpty()) {
                runOnUiThread(() -> {
                    Toast.makeText(MapActivity.this, "Không tìm thấy địa điểm", Toast.LENGTH_SHORT).show();
                    suggestionsList.setVisibility(View.GONE);
                });
                return;
            }
            Address destination = addresses.get(0);
            GeoPoint destinationPoint = new GeoPoint(destination.getLatitude(), destination.getLongitude());

            runOnUiThread(() -> {
                mapController.setCenter(destinationPoint);
                mapController.setZoom(18.0);
            });

            GeoPoint startPoint = myLocationOverlay.getMyLocation();
            if (startPoint == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Không thể xác định vị trí hiện tại", Toast.LENGTH_SHORT).show();
                    suggestionsList.setVisibility(View.GONE);
                });
                return;
            }

            String url = "https://router.project-osrm.org/route/v1/driving/"
                    + startPoint.getLongitude() + "," + startPoint.getLatitude() + ";"
                    + destinationPoint.getLongitude() + "," + destinationPoint.getLatitude()
                    + "?overview=full&geometries=geojson";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(MapActivity.this, "Không thể tìm đường", Toast.LENGTH_SHORT).show();
                        suggestionsList.setVisibility(View.GONE);
                    });
                }
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    if (!response.isSuccessful() || response.body() == null) {
                        runOnUiThread(() -> {
                            Toast.makeText(MapActivity.this, "Lỗi khi tìm đường", Toast.LENGTH_SHORT).show();
                            suggestionsList.setVisibility(View.GONE);
                        });
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

                            // Theo dõi và xóa các đoạn đã đi qua
                            trackAndRemovePassedSections(routePoints);

                            zoomToRoute(routePoints);
                            mapView.invalidate();
                            suggestionsList.setVisibility(View.GONE);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(MapActivity.this, "Lỗi khi xử lý dữ liệu đường", Toast.LENGTH_SHORT).show();
                            suggestionsList.setVisibility(View.GONE);
                        });
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(this, "Lỗi khi tìm kiếm địa điểm", Toast.LENGTH_SHORT).show();
                suggestionsList.setVisibility(View.GONE);
            });
        }
    }

    private Location geoPointToLocation(GeoPoint geoPoint) {
        Location location = new Location("");
        location.setLatitude(geoPoint.getLatitude());
        location.setLongitude(geoPoint.getLongitude());
        return location;
    }
    private void trackAndRemovePassedSections(List<GeoPoint> routePoints) {
        new Thread(() -> {
            while (!routePoints.isEmpty()) {
                GeoPoint currentGeoPoint = myLocationOverlay.getMyLocation();
                if (currentGeoPoint == null) {
                    continue;
                }

                // Chuyển đổi GeoPoint sang Location
                Location currentLocation = geoPointToLocation(currentGeoPoint);

                // Kiểm tra khoảng cách từ vị trí hiện tại đến điểm đầu tiên trong tuyến đường
                GeoPoint firstPoint = routePoints.get(0);
                float[] results = new float[1];
                Location.distanceBetween(
                        currentLocation.getLatitude(), currentLocation.getLongitude(),
                        firstPoint.getLatitude(), firstPoint.getLongitude(),
                        results
                );

                // Nếu khoảng cách nhỏ hơn ngưỡng (ví dụ: 20m), xóa điểm đầu tiên
                if (results[0] < 20) { // 20m là khoảng cách cho phép
                    runOnUiThread(() -> {
                        routePoints.remove(0);
                        if (!routePoints.isEmpty()) {
                            routeOverlay.setPoints(routePoints);
                            mapView.invalidate();
                        }
                    });
                }

                // Kiểm tra ổ gà gần vị trí hiện tại
                checkPotholeProximity(currentLocation);

                try {
                    Thread.sleep(2000); // Kiểm tra mỗi 2 giây
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void zoomToRoute(List<GeoPoint> routePoints) {
        if (routePoints == null || routePoints.isEmpty()) return;

        // Tìm BoundingBox chứa tất cả các điểm trong tuyến đường
        double minLat = Double.MAX_VALUE, minLon = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE, maxLon = Double.MIN_VALUE;

        for (GeoPoint point : routePoints) {
            minLat = Math.min(minLat, point.getLatitude());
            minLon = Math.min(minLon, point.getLongitude());
            maxLat = Math.max(maxLat, point.getLatitude());
            maxLon = Math.max(maxLon, point.getLongitude());
        }

        // Tạo BoundingBox
        BoundingBox boundingBox = new BoundingBox(maxLat, maxLon, minLat, minLon);

        // Đưa bản đồ về giữa và phóng to vừa với BoundingBox
        mapView.zoomToBoundingBox(boundingBox, true);
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




    //xử lý gia tốc kế
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double magnitude = Math.sqrt(x*x + y*y + z*z);
                if (magnitude <= 12.0) {
                    return;
                }

                String severity;
                if (magnitude > 25.0) {
                    severity = "high";
                } else if (magnitude > 17.5) {
                    severity = "medium";
                } else {
                    severity = "low";
                }

                Log.d("PotholeDetection", "Accelerometer values - X: " + x + ", Y: " + y + ", Z: " + z);
                Log.d("PotholeDetection", "Severity: " + severity);


                long now = System.currentTimeMillis();
                if (now - lastShakeTime > SHAKE_DEBOUNCE_MS) {
                    lastShakeTime = now;
                    onSignificantShakeDetected(x, y, z, severity);
                }
            }

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private void onSignificantShakeDetected(float ax, float ay, float az, String severity) {

        Log.d("PotholeDetection", "Shake detected!");
        Log.d("PotholeDetection", "Accelerometer values - X: " + ax + ", Y: " + ay + ", Z: " + az);
        Log.d("PotholeDetection", "Severity: " + severity);

        GeoPoint currentLocation = myLocationOverlay.getMyLocation();
        if (currentLocation == null) {
            Log.e("PotholeDetection", "Current location is null!");
            return;
        }

        Log.d("PotholeDetection", "Current location: " +
                currentLocation.getLatitude() + ", " + currentLocation.getLongitude());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Phát hiện rung lắc (" + severity + ")");
        builder.setMessage("Bạn có muốn lưu vị trí này là ổ gà?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            saveNewPotholeFullData(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    severity,
                    ax, ay, az,
                    0.0f
            );
        });
        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void saveNewPotholeFullData(
            double lat,
            double lon,
            String severity,
            double accelerationX,
            double accelerationY,
            double accelerationZ,
            float gpsAccuracy
    ) {
        String detectedTime = DATE_FORMAT.format(new Date());
        String potholeId = "unique_pothole_id_" + System.currentTimeMillis(); // Tạo ID duy nhất
        String detectedByUserId = "unique_user_id_7"; // ID người dùng hiện tại (cần thay thế bằng dữ liệu thực)

        // Tạo yêu cầu với `manualReports` là null
        CreatePotholeRequest request = new CreatePotholeRequest(
                potholeId,
                lon,
                lat,
                detectedTime,
                severity,
                (float) accelerationX,
                (float) accelerationY,
                (float) accelerationZ,
                gpsAccuracy,
                detectedByUserId,
                true,
                null // manualReports là null
        );
        Log.d("CreatePotholeRequest", "Request Details: " +
                "\nPothole ID: " + request.getPotholeId() +
                "\nLongitude: " + request.getLongitude() +
                "\nLatitude: " + request.getLatitude() +
                "\nDetected Time: " + request.getDetectedTime() +
                "\nSeverity: " + request.getSeverity() +
                "\nAcceleration X: " + request.getAccelerationX() +
                "\nAcceleration Y: " + request.getAccelerationY() +
                "\nAcceleration Z: " + request.getAccelerationZ() +
                "\nGPS Accuracy: " + request.getGpsAccuracy() +
                "\nDetected By User ID: " + request.getDetectedByUserId() +
                "\nConfirmed By User: " + request.isConfirmedByUser() +
                "\nManual Reports: " + request.getManualReports()
        );

        // Gửi yêu cầu API
        Call<CreatePotholeResponse> call = potholeApiService.createPothole(request);
        call.enqueue(new Callback<CreatePotholeResponse>() {
            @Override
            public void onResponse(Call<CreatePotholeResponse> call, retrofit2.Response<CreatePotholeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Success", "Pothole created with ID: " + response.body().getId());
                    Toast.makeText(MapActivity.this, "Đã lưu ổ gà thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API Error", "Response error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CreatePotholeResponse> call, Throwable t) {
                Log.e("API Failure", "Error creating pothole", t);
                Toast.makeText(MapActivity.this, "Kết nối server thất bại.", Toast.LENGTH_SHORT).show();
            }
        } );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        fetchPotholesAndDisplay();

        // Đăng ký lại cảm biến khi activity được resume
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(
                    sensorEventListener,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

        // Hủy đăng ký cảm biến khi activity bị pause
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }
}
package com.roadwatcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.roadwatcher.R;
import com.roadwatcher.activities.LoginActivity;
import com.roadwatcher.activities.MapActivity;
import com.roadwatcher.utils.SessionManager;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IMapController;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private Button button1, button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sessionManager = new SessionManager(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        if (!sessionManager.isLoggedIn()) {
            // Chưa đăng nhập, chuyển hướng tới LoginActivity
            navigateToLogin();
            return;
        }

        // Set click listeners for the buttons
        button1.setOnClickListener(v -> navigateToMapActivity("Map 1"));
        button2.setOnClickListener(v -> navigateToMapActivity("Map 2"));
    }

    private void navigateToMapActivity(String mapType) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra("map_type", mapType); // Optionally send data to MapActivity
        startActivity(intent);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

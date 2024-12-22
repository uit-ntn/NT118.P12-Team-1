package com.roadwatcher.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

public class SettingsActivity extends AppCompatActivity {

    private Spinner languageSpinner;
    private CheckBox notificationOption1, notificationOption2, notificationOption3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        ImageView backButton = findViewById(R.id.backButton);
        languageSpinner = findViewById(R.id.languageSpinner);
        notificationOption1 = findViewById(R.id.notificationOption1);
        notificationOption2 = findViewById(R.id.notificationOption2);
        notificationOption3 = findViewById(R.id.notificationOption3);

        // Back button click
        backButton.setOnClickListener(v -> finish());

        // Set up language spinner
        String[] languages = {"Tiếng Việt", "Tiếng Anh", "Tiếng Tây Ban Nha"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languages);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = languages[position];
                Toast.makeText(SettingsActivity.this, "Bạn đã chọn: " + selectedLanguage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Notification options click
        notificationOption1.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Thông báo 1: " + (isChecked ? "Bật" : "Tắt"), Toast.LENGTH_SHORT).show());
        notificationOption2.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Thông báo 2: " + (isChecked ? "Bật" : "Tắt"), Toast.LENGTH_SHORT).show());
        notificationOption3.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Thông báo 3: " + (isChecked ? "Bật" : "Tắt"), Toast.LENGTH_SHORT).show());
    }
}

package com.roadwatcher.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

public class AddPotholeActivity extends AppCompatActivity {

    private ImageView backButton, profilePicture;
    private EditText dangerLevelInput, quantityInput, conditionInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pothole);

        // Initialize Views
        backButton = findViewById(R.id.backButton);
        profilePicture = findViewById(R.id.profilePicture);
        dangerLevelInput = findViewById(R.id.dangerLevelInput);
        quantityInput = findViewById(R.id.quantityInput);
        conditionInput = findViewById(R.id.conditionInput);
        saveButton = findViewById(R.id.saveButton);

        // Set Back Button Click Listener
        backButton.setOnClickListener(v -> finish());

        // Save Button Click Listener
        saveButton.setOnClickListener(v -> {
            String dangerLevel = dangerLevelInput.getText().toString().trim();
            String quantity = quantityInput.getText().toString().trim();
            String condition = conditionInput.getText().toString().trim();

            if (dangerLevel.isEmpty() || quantity.isEmpty() || condition.isEmpty()) {
                Toast.makeText(AddPotholeActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                // Perform save logic here
                Toast.makeText(AddPotholeActivity.this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
                // Clear inputs
                dangerLevelInput.setText("");
                quantityInput.setText("");
                conditionInput.setText("");
            }
        });
    }
}

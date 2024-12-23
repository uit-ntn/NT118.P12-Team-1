package com.roadwatcher.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

public class SupportContactActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView contactPhone, contactEmail, contactFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_contact); // Liên kết với tệp XML bạn đã cung cấp

        // Khởi tạo các View
        backButton = findViewById(R.id.backButton);
        contactPhone = findViewById(R.id.contactPhone);
        contactEmail = findViewById(R.id.contactEmail);
        contactFacebook = findViewById(R.id.contactFacebook);

        // Thiết lập dữ liệu (có thể thay đổi động nếu cần)
        contactPhone.setText("090xxxxxxx"); // Đặt số điện thoại
        contactEmail.setText("RoadWatcher@gmail.com"); // Đặt email
        contactFacebook.setText("Road Watcher"); // Đặt Facebook

        // Xử lý sự kiện nút "Back"
        backButton.setOnClickListener(v -> finish()); // Quay lại màn hình trước đó
    }
}

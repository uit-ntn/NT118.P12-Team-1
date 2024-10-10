package com.roadwatcher.ultis;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelpers {

    private DatabaseReference mDatabase;

    public FirebaseHelpers() {
        // Khởi tạo Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // Ví dụ về phương thức ghi dữ liệu
    public void writeMessage(String message) {
        mDatabase.child("message").setValue(message);
    }

    // Thêm các phương thức khác tùy theo yêu cầu của bạn
    public DatabaseReference getDatabaseReference() {
        return mDatabase;
    }
}

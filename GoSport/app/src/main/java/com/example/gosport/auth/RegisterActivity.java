package com.example.gosport.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gosport.R;
import com.example.gosport.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText edtFullName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView tvLoginLink;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        edtFullName = findViewById(R.id.edtRegFullName);
        edtEmail = findViewById(R.id.edtRegEmail);
        edtPhone = findViewById(R.id.edtRegPhone);
        edtPassword = findViewById(R.id.edtRegPassword);
        edtConfirmPassword = findViewById(R.id.edtRegConfirmPassword);
        btnRegister = findViewById(R.id.btnRegisterSubmit);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(v -> handleRegister());

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate ho ten
        if (fullName.isEmpty()) {
            edtFullName.setError("Vui lòng nhập họ tên");
            edtFullName.requestFocus();
            return;
        }
        if (fullName.length() < 2) {
            edtFullName.setError("Họ tên phải có ít nhất 2 ký tự");
            edtFullName.requestFocus();
            return;
        }

        // Validate email
        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        // Validate phone
        if (phone.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
            return;
        }
        if (!phone.matches("^0\\d{9}$")) {
            edtPhone.setError("Số điện thoại phải có 10 chữ số, bắt đầu bằng 0");
            edtPhone.requestFocus();
            return;
        }

        // Validate password
        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtPassword.requestFocus();
            return;
        }
        if (!password.matches(".*[A-Z].*")) {
            edtPassword.setError("Mật khẩu phải có ít nhất 1 chữ hoa");
            edtPassword.requestFocus();
            return;
        }
        if (!password.matches(".*\\d.*")) {
            edtPassword.setError("Mật khẩu phải có ít nhất 1 chữ số");
            edtPassword.requestFocus();
            return;
        }

        // Validate confirm password
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            edtConfirmPassword.requestFocus();
            return;
        }

        // Check trung lap
        if (dbHelper.isEmailExists(email)) {
            edtEmail.setError("Email này đã được đăng ký");
            edtEmail.requestFocus();
            return;
        }
        if (dbHelper.isPhoneExists(phone)) {
            edtPhone.setError("Số điện thoại này đã được đăng ký");
            edtPhone.requestFocus();
            return;
        }

        // Insert user
        long result = dbHelper.insertUser(fullName, email, phone, password);
        if (result != -1) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.gosport.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.gosport.MainActivity;
import com.example.gosport.R;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.utils.SessionManager;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SessionManager sessionManager = new SessionManager(requireContext());
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);
        Button btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        tvName.setText(sessionManager.getFullName());
        tvEmail.setText(sessionManager.getEmail());
        tvPhone.setText(sessionManager.getPhone());

        // editProfile
        btnEditProfile.setOnClickListener(v -> {
            LinearLayout layout = new LinearLayout(requireContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 20, 50, 10);

            TextView labelName = new TextView(requireContext());
            labelName.setText("Họ tên");
            labelName.setTextSize(12);
            layout.addView(labelName);

            EditText edtName = new EditText(requireContext());
            edtName.setHint("Họ tên");
            edtName.setText(sessionManager.getFullName());
            layout.addView(edtName);

            TextView labelPhone = new TextView(requireContext());
            labelPhone.setText("Số điện thoại");
            labelPhone.setTextSize(12);
            labelPhone.setPadding(0, 16, 0, 0);
            layout.addView(labelPhone);

            EditText edtPhone = new EditText(requireContext());
            edtPhone.setHint("Số điện thoại");
            edtPhone.setText(sessionManager.getPhone());
            edtPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
            layout.addView(edtPhone);


            new AlertDialog.Builder(requireContext())
                    .setTitle("Chỉnh sửa thông tin")
                    .setView(layout)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        String newName = edtName.getText().toString().trim();
                        String newPhone = edtPhone.getText().toString().trim();

                        if (newName.isEmpty()) {
                            Toast.makeText(requireContext(), "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (newPhone.isEmpty() || !newPhone.matches("0[0-9]{9}")) {
                            Toast.makeText(requireContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int userId = sessionManager.getUserId();
                        if (dbHelper.isPhoneExistsExcluding(newPhone, userId)) {
                            Toast.makeText(requireContext(), "Số điện thoại đã được sử dụng", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        dbHelper.updateProfile(userId, newName, newPhone);
                        sessionManager.updateSession(newName, newPhone);
                        tvName.setText(newName);
                        tvPhone.setText(newPhone);
                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // deleteAccount
        btnDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa tài khoản")
                    .setMessage("Tài khoản của bạn sẽ bị xóa vĩnh viễn và không thể đăng nhập lại. Bạn có chắc chắn không?")
                    .setPositiveButton("Xóa tài khoản", (dialog, which) -> {
                        int userId = sessionManager.getUserId();
                        dbHelper.deleteAccount(userId);
                        sessionManager.logout();
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        return view;
    }
}

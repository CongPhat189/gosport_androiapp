package com.example.gosport.admin;

import static com.example.gosport.R.*;

import android.os.Bundle;
import android.view.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gosport.R;



import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gosport.R;
import com.example.gosport.adapter.UserAdapter;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnAddUser;

    private UserAdapter adapter;
    private List<User> userList;

    private DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        recyclerView = view.findViewById(R.id.recyclerUsers);
        btnAddUser = view.findViewById(R.id.btnAddUser);

        db = new DatabaseHelper(getContext());
        userList = new ArrayList<>();

        loadUsers();

        adapter = new UserAdapter(userList, new UserAdapter.OnUserAction() {
            @Override
            public void onEdit(User user) {
                showEditDialog(user);
            }

            @Override
            public void onDelete(User user) {
                confirmDelete(user);
            }

            @Override
            public void onLock(User user) {
                toggleLock(user);
            }

            @Override
            public void onChangePassword(User user) {
                showChangePasswordDialog(user);
            }

            @Override
            public void onChangeRole(User user) {
                showChangeRoleDialog(user);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnAddUser.setOnClickListener(v -> showAddDialog());

        return view;
    }

    // ================= LOAD =================
    private void loadUsers() {
        userList.clear();
        userList.addAll(db.getAllUsers());
    }

    private void reload() {
        loadUsers();
        adapter.notifyDataSetChanged();
    }

    // ================= ADD =================
    private void showAddDialog() {
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_user, null);

        EditText edName = v.findViewById(R.id.edName);
        EditText edEmail = v.findViewById(R.id.edEmail);
        EditText edPhone = v.findViewById(R.id.edPhone);
        EditText edPassword = v.findViewById(R.id.edPassword);

        new AlertDialog.Builder(getContext())
                .setTitle("Thêm User")
                .setView(v)
                .setPositiveButton("Lưu", (dialog, which) -> {

                    String name = edName.getText().toString().trim();
                    String email = edEmail.getText().toString().trim();
                    String phone = edPhone.getText().toString().trim();
                    String password = edPassword.getText().toString().trim();

                    // validate cơ bản
                    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(getContext(),
                                "Vui lòng nhập đầy đủ thông tin",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long result = db.insertUser(name, email, phone, password);

                    if (result > 0) {
                        Toast.makeText(getContext(),
                                "Thêm user thành công",
                                Toast.LENGTH_SHORT).show();
                        reload();
                    } else {
                        Toast.makeText(getContext(),
                                "Thêm thất bại",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ================= EDIT =================
    private void showEditDialog(User user) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user, null);

        EditText edName = v.findViewById(R.id.edName);
        EditText edEmail = v.findViewById(R.id.edEmail);
        EditText edPhone = v.findViewById(R.id.edPhone);

        edName.setText(user.getFullName());
        edEmail.setText(user.getEmail());
        edPhone.setText(user.getPhone());

        new AlertDialog.Builder(getContext())
                .setTitle("Cập nhật User")
                .setView(v)
                .setPositiveButton("Lưu", (d, i) -> {
                    db.updateUserInfo(
                            user.getId(),
                            edName.getText().toString(),
                            edPhone.getText().toString(),
                            edEmail.getText().toString()
                    );
                    reload();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ================= DELETE =================
    private void confirmDelete(User user) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa user?")
                .setMessage("Bạn chắc chắn muốn xóa?")
                .setPositiveButton("Xóa", (d, i) -> {
                    db.deleteUser(user.getId());
                    reload();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ================= LOCK =================
    private void toggleLock(User user) {
        if (user.getIsActive() == 1)
            db.lockUser(user.getId());
        else
            db.unlockUser(user.getId());

        reload();
    }

    // ================= PASSWORD =================
    private void showChangePasswordDialog(User user) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_change_password, null);

        EditText edtPassword = view.findViewById(R.id.edtPassword);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Đổi mật khẩu")
                .setView(view)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnSave.setOnClickListener(v -> {

                String newPass = edtPassword.getText().toString().trim();

                if (newPass.isEmpty()) {
                    edtPassword.setError("Không được để trống");
                    return;
                }

                db.updatePassword(user.getId(), newPass);

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // ================= ROLE =================
    private void showChangeRoleDialog(User user) {
        String[] roles = {"USER", "ADMIN"};

        new AlertDialog.Builder(getContext())
                .setTitle("Chọn role")
                .setItems(roles, (d, which) -> {
                    db.updateUserRole(user.getId(), roles[which]);
                    reload();
                })
                .show();
    }
}
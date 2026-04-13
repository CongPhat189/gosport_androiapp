package com.example.gosport.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gosport.R;
import com.example.gosport.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    // ================= INTERFACE =================
    public interface OnUserAction {
        void onEdit(User user);
        void onDelete(User user);
        void onLock(User user);
        void onChangePassword(User user);
        void onChangeRole(User user);
    }

    private List<User> userList;
    private OnUserAction listener;

    public UserAdapter(List<User> userList, OnUserAction listener) {
        this.userList = userList;
        this.listener = listener;
    }

    // ================= VIEW HOLDER =================
    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtEmail, txtPhone, txtRole, txtStatus;
        Button btnEdit, btnDelete, btnLock, btnPassword, btnRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtRole = itemView.findViewById(R.id.txtRole);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnLock = itemView.findViewById(R.id.btnLock);
            btnPassword = itemView.findViewById(R.id.btnPassword);
            btnRole = itemView.findViewById(R.id.btnRole);
        }
    }

    // ================= CREATE VIEW =================
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new UserViewHolder(view);
    }

    // ================= BIND DATA =================
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user = userList.get(position);

        // ================= DATA SAFE =================
        holder.txtName.setText(
                user.getFullName() != null ? user.getFullName() : "N/A"
        );

        holder.txtEmail.setText(
               "Email: " + (user.getEmail() != null ? user.getEmail() : "N/A")
        );

        holder.txtPhone.setText(
                "Số điện thoại: " + (user.getPhone() != null ? user.getPhone() : "N/A")
        );

        holder.txtRole.setText(
                "Vai trò: " + (user.getRole() != null ? user.getRole() : "N/A")
        );

        // ================= STATUS =================
        if (user.getIsActive() == 1) {
            holder.txtStatus.setText("Trạng thái: Đang hoạt động");
            holder.txtStatus.setTextColor(Color.parseColor("#2E7D32")); // xanh
            holder.btnLock.setText("Khóa");
            holder.btnLock.setBackgroundColor(Color.RED);
        } else {
            holder.txtStatus.setText("Trạng thái: Bị khóa");
            holder.txtStatus.setTextColor(Color.parseColor("#C62828")); // đỏ
            holder.btnLock.setText("Mở khóa");
            holder.btnLock.setBackgroundColor(Color.GREEN);
        }

        // ================= ACTION =================
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(user);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(user);
        });

        holder.btnLock.setOnClickListener(v -> {
            if (listener != null) listener.onLock(user);
        });

        holder.btnPassword.setOnClickListener(v -> {
            if (listener != null) listener.onChangePassword(user);
        });

        holder.btnRole.setOnClickListener(v -> {
            if (listener != null) listener.onChangeRole(user);
        });
    }

    // ================= SIZE =================
    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    // ================= UPDATE DATA =================
    public void updateData(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }
}
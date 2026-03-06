package com.example.gosport.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.example.gosport.R;
import com.example.gosport.adapter.FieldAdapter;
import com.example.gosport.adapter.FieldAdapterUser;
import com.example.gosport.database.DatabaseHelper;
import com.example.gosport.model.FieldModel;

import java.util.ArrayList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FieldManagementFragment extends Fragment {

    DatabaseHelper db;
    ListView listView;
    FloatingActionButton btnAdd;
    Spinner spFilterCategory;

    ArrayList<FieldModel> fieldList;
    FieldAdapter adapter;

    Uri selectedImageUri;
    private static final int PICK_IMAGE = 1;

    int selectedFilterCategoryId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_field_management, container, false);

        // Init database FIRST
        db = new DatabaseHelper(getContext());

        listView = view.findViewById(R.id.listViewField);
        btnAdd = view.findViewById(R.id.btnAddField);
        spFilterCategory = view.findViewById(R.id.spFilterCategory);

        loadFilterCategorySpinner();
        loadFields();

        btnAdd.setOnClickListener(v -> showAddDialog());

        listView.setOnItemClickListener((parent, v, position, id) ->
                showUpdateDialog(fieldList.get(position)));

        listView.setOnItemLongClickListener((parent, v, position, id) -> {

            new AlertDialog.Builder(getContext())
                    .setTitle("Xóa sân")
                    .setMessage("Bạn có chắc muốn xóa?")
                    .setPositiveButton("Xóa", (d, w) -> {
                        db.deleteField(fieldList.get(position).getFieldId());
                        loadFields();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();

            return true;
        });

        return view;
    }

    // ================= LOAD FIELDS =================
    private void loadFields() {

        fieldList = new ArrayList<>();

        Cursor cursor;

        if (selectedFilterCategoryId == -1) {
            cursor = db.getAllFields();
        } else {
            cursor = db.getFieldsByCategory(selectedFilterCategoryId);
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                fieldList.add(new FieldModel(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getDouble(6),
                        cursor.getString(7),
                        cursor.getString(8)
                ));
            } while (cursor.moveToNext());

            cursor.close();
        }

        if (adapter == null) {
            adapter = new FieldAdapter(getContext(), fieldList);
            listView.setAdapter(adapter);
        } else {
            adapter.updateData(fieldList);
        }
    }

    // ================= LOAD FILTER SPINNER =================
    private void loadFilterCategorySpinner() {

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        names.add("Tất cả");
        ids.add(-1);

        Cursor cursor = db.getAllCategories();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0));
                names.add(cursor.getString(1));
            } while (cursor.moveToNext());

            cursor.close();
        }

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item,
                        names);

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spFilterCategory.setAdapter(spinnerAdapter);
        spFilterCategory.setTag(ids);

        spFilterCategory.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {

                        ArrayList<Integer> listIds =
                                (ArrayList<Integer>) spFilterCategory.getTag();

                        selectedFilterCategoryId = listIds.get(position);
                        loadFields();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
    }

    // ================= LOAD CATEGORY FOR ADD/EDIT =================
    private void loadCategoryToSpinner(Spinner spinner, int selectedCategoryId) {

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        Cursor cursor = db.getAllCategories();

        int selectedPosition = 0;
        int index = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);

                ids.add(id);
                names.add(name);

                if (id == selectedCategoryId) {
                    selectedPosition = index;
                }

                index++;
            } while (cursor.moveToNext());

            cursor.close();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item,
                        names);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setSelection(selectedPosition);
        spinner.setTag(ids);
    }

    // ================= ADD FIELD =================
    private void showAddDialog() {

        selectedImageUri = null;

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_field, null);

        Spinner spCategory = dialogView.findViewById(R.id.spCategory);
        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        EditText edtDesc = dialogView.findViewById(R.id.edtDesc);
        EditText edtPrice = dialogView.findViewById(R.id.edtPrice);
        ImageView img = dialogView.findViewById(R.id.imgPreview);

        loadCategoryToSpinner(spCategory, -1);

        img.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Thêm sân")
                .setView(dialogView)
                .setPositiveButton("Thêm", (d, w) -> {

                    int position = spCategory.getSelectedItemPosition();
                    ArrayList<Integer> ids =
                            (ArrayList<Integer>) spCategory.getTag();

                    int categoryId = ids.get(position);

                    db.insertField(
                            categoryId,
                            edtName.getText().toString(),
                            edtAddress.getText().toString(),
                            edtDesc.getText().toString(),
                            Double.parseDouble(edtPrice.getText().toString()),
                            "Available",
                            selectedImageUri != null ?
                                    selectedImageUri.toString() : null
                    );

                    loadFields();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ================= UPDATE FIELD =================
    private void showUpdateDialog(FieldModel field) {

        selectedImageUri = null;

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_field, null);

        Spinner spCategory = dialogView.findViewById(R.id.spCategory);
        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        EditText edtDesc = dialogView.findViewById(R.id.edtDesc);
        EditText edtPrice = dialogView.findViewById(R.id.edtPrice);
        ImageView img = dialogView.findViewById(R.id.imgPreview);

        edtName.setText(field.getFieldName());
        edtAddress.setText(field.getAddress());
        edtDesc.setText(field.getDescription());
        edtPrice.setText(String.valueOf(field.getPricePerHour()));

        loadCategoryToSpinner(spCategory, field.getCategoryId());

        img.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Cập nhật sân")
                .setView(dialogView)
                .setPositiveButton("Lưu", (d, w) -> {

                    int position = spCategory.getSelectedItemPosition();
                    ArrayList<Integer> ids =
                            (ArrayList<Integer>) spCategory.getTag();

                    int categoryId = ids.get(position);

                    db.updateField(
                            field.getFieldId(),
                            categoryId,
                            edtName.getText().toString(),
                            edtAddress.getText().toString(),
                            edtDesc.getText().toString(),
                            Double.parseDouble(edtPrice.getText().toString()),
                            field.getStatus(),
                            selectedImageUri != null ?
                                    selectedImageUri.toString() :
                                    field.getImageUrl()
                    );

                    loadFields();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE &&
                resultCode == Activity.RESULT_OK &&
                data != null) {

            selectedImageUri = data.getData();
        }
    }
}
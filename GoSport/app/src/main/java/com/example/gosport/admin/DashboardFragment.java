package com.example.gosport.admin;

import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.example.gosport.R;

public class DashboardFragment extends Fragment {

    public DashboardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        view.findViewById(R.id.btnUser)
                .setOnClickListener(v ->
                        ((AdminActivity)getActivity())
                                .loadFragment(new UserManagementFragment()));

        view.findViewById(R.id.btnCategory)
                .setOnClickListener(v ->
                        ((AdminActivity)getActivity())
                                .loadFragment(new CategoryManagementFragment()));

        view.findViewById(R.id.btnField)
                .setOnClickListener(v ->
                        ((AdminActivity)getActivity())
                                .loadFragment(new FieldManagementFragment()));

        view.findViewById(R.id.btnBooking)
                .setOnClickListener(v ->
                        ((AdminActivity)getActivity())
                                .loadFragment(new BookingManagementFragment()));

        view.findViewById(R.id.btnReport)
                .setOnClickListener(v ->
                        ((AdminActivity)getActivity())
                                .loadFragment(new ReportFragment()));

        return view;
    }
}
package com.example.gosport.admin;

import android.os.Bundle;
import android.view.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gosport.R;

public class BookingManagementFragment extends Fragment {

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_booking_management, container, false);

        recyclerView = view.findViewById(R.id.recyclerBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
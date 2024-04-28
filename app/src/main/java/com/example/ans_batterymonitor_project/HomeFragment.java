package com.example.ans_batterymonitor_project;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ans_batterymonitor_project.bt.BluetoothLeService;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    public BluetoothLeService bluetoothLeService;
    private final String TAG = "HomeFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(View view) {
        TextView connectButton = view.findViewById(R.id.connectButton);
        TextView connectionTextView = view.findViewById(R.id.connectionTextView);
        ImageView connectionImageView = view.findViewById(R.id.connectionImageView);

        if (bluetoothLeService.isConnected()) {
            connectButton.setVisibility(View.INVISIBLE);
            connectionTextView.setText("Connected");
            connectionImageView.setImageResource(R.drawable.ic_yes_512);
        } else {
            connectButton.setVisibility(View.VISIBLE);
            connectionTextView.setText("No connection");
            connectionImageView.setImageResource(R.drawable.ic_no_512);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bluetoothLeService = MainActivity.bluetoothLeService;

        Button connectButton = view.findViewById(R.id.connectButton);
        connectButton.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).goToSetting();
        });

        Button startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            if(!bluetoothLeService.isConnected()) {
                showToast("Device Not Connected!");
                return;
            }
            // TODO
            bluetoothLeService.sendMeasurementRequestStart();
        });

        updateUI(view);
        return view;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
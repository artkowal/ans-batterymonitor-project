package com.example.ans_batterymonitor_project;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ans_batterymonitor_project.measurement.Measurement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeasurementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeasurementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String TAG = "MeasurementFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MeasurementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeasurementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeasurementFragment newInstance(String param1, String param2) {
        MeasurementFragment fragment = new MeasurementFragment();
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measurement, container, false);

        Measurement measurement = ((MainActivity) requireActivity()).measurement;
        Button pauseButton = view.findViewById(R.id.pauseButton);

        if (measurement != null) {
            if (measurement.isPaused()) {
                pauseButton.setText("RESUME");
            } else {
                pauseButton.setText("PAUSE");
            }
        }

        pauseButton.setOnClickListener(v -> {
            if (measurement != null) {
                if (measurement.isPaused()) {
                    measurement.resumeMeasurement();
                    pauseButton.setText("PAUSE");
                } else {
                    measurement.pauseMeasurement();
                    pauseButton.setText("RESUME");
                }
            }
        });

        // TODO przycisk stop (kończenie pomiaru)

        return view;
    }

    public void handleMeasurementData(String duration, String minVoltage, String maxVoltage, String avgVoltage) {
        TextView durationTextView = requireView().findViewById(R.id.durationTextView);
        TextView minVolTextView = requireView().findViewById(R.id.minVolTextView);
        TextView maxVolTextView = requireView().findViewById(R.id.maxVolTextView);
        TextView mediumVolTextView = requireView().findViewById(R.id.mediumVolTextView);

        requireActivity().runOnUiThread(() -> {
            durationTextView.setText(String.valueOf(duration));
            minVolTextView.setText(String.valueOf(minVoltage));
            maxVolTextView.setText(String.valueOf(maxVoltage));
            mediumVolTextView.setText(String.valueOf(avgVoltage));
        });
    }

    public void handleDataInFragment(float number) {
//        Log.d(TAG, String.valueOf(number));
        TextView voltageTextView = requireView().findViewById(R.id.voltageTextView);
        requireActivity().runOnUiThread(() -> {
            voltageTextView.setText(String.valueOf(number));
        });
    }
}
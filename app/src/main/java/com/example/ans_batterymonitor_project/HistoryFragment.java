package com.example.ans_batterymonitor_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ans_batterymonitor_project.measurement.MeasurementHistoryManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        ///////// POBIERANIE DANYCH Z PLIKU JSON //////////////
        JSONArray historyJson = MeasurementHistoryManager.loadHistory(requireContext());
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        try {
            for (int i = 0; i < historyJson.length(); i++) {
                JSONObject item = historyJson.getJSONObject(i);
                String dateString = (String) item.get("date");
                Date date = inputFormat.parse(dateString);

                String formattedDate = outputFormat.format(date);
                String duration = (String) item.get("duration");
                String minVoltage = (String) item.get("minVoltage");
                String maxVoltage = (String) item.get("maxVoltage");
                String movingAverage = (String) item.get("movingAverage");

                ///////// Wyświetlanie do usunięcie później //////////
                Log.d("History Fragment", "Formatted Date: " + formattedDate);
                Log.d("History Fragment", "Duration: " + duration);
                Log.d("History Fragment", "MinVoltage: " + minVoltage);
                Log.d("History Fragment", "MaxVoltage: " + maxVoltage);
                Log.d("History Fragment", "MovingAverage: " + movingAverage);
            }
        } catch (JSONException | ParseException e) {
            throw new RuntimeException(e);
        }
        ///////// POBIERANIE DANYCH Z PLIKU JSON //////////////


        return view;
    }
}
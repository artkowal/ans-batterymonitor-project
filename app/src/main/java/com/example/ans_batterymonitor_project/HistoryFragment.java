package com.example.ans_batterymonitor_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ans_batterymonitor_project.measurement.MeasurementHistoryManager;

import org.json.JSONArray;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private MeasurementAdapter adapter;
    private TextView emptyTextView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle arguments if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyTextView = view.findViewById(R.id.emptyTextView);

        JSONArray historyJson = MeasurementHistoryManager.loadHistory(requireContext());
        adapter = new MeasurementAdapter(historyJson);
        recyclerView.setAdapter(adapter);

        updateEmptyView(historyJson);

        Button clearButton = view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasurementHistoryManager.clearHistory(requireContext());
                JSONArray emptyArray = new JSONArray();
                adapter.updateData(emptyArray);
                updateEmptyView(emptyArray);
            }
        });

        return view;
    }

    private void updateEmptyView(JSONArray historyJson) {
        if (historyJson.length() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}

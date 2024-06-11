package com.example.ans_batterymonitor_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder> {
    private JSONArray measurements;

    public MeasurementAdapter(JSONArray measurements) {
        this.measurements = measurements;
    }

    @NonNull
    @Override
    public MeasurementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_measurement, parent, false);
        return new MeasurementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeasurementViewHolder holder, int position) {
        try {
            JSONObject item = measurements.getJSONObject(position);
            String dateString = item.getString("date");
            String duration = item.getString("duration");
            String minVoltage = item.getString("minVoltage");
            String maxVoltage = item.getString("maxVoltage");
            String movingAverage = item.getString("movingAverage");

            // Formatuj datę i czas
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
            SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            Date date = inputFormat.parse(dateString);
            String formattedDate = outputDateFormat.format(date);
            String formattedTime = outputTimeFormat.format(date);

            // Ustaw wartości dla widoków tekstowych
            holder.formattedDateTextView.setText(formattedDate);
            holder.formattedTimeTextView.setText(duration); // Użyj duration jako czas pomiaru
            holder.minVoltageTextView.setText(minVoltage + "v");
            holder.movingAverageTextView.setText(movingAverage + "v");
            holder.maxVoltageTextView.setText(maxVoltage + "v");

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return measurements.length();
    }

    public void updateData(JSONArray newMeasurements) {
        this.measurements = newMeasurements;
        notifyDataSetChanged();
    }

    public static class MeasurementViewHolder extends RecyclerView.ViewHolder {
        TextView formattedDateTextView;
        TextView formattedTimeTextView;
        TextView minVoltageTextView;
        TextView maxVoltageTextView;
        TextView movingAverageTextView;

        public MeasurementViewHolder(@NonNull View itemView) {
            super(itemView);
            formattedDateTextView = itemView.findViewById(R.id.formattedDateTextView);
            formattedTimeTextView = itemView.findViewById(R.id.formattedTimeTextView);
            minVoltageTextView = itemView.findViewById(R.id.minVoltageTextView);
            maxVoltageTextView = itemView.findViewById(R.id.maxVoltageTextView);
            movingAverageTextView = itemView.findViewById(R.id.movingAverageTextView);
        }
    }
}

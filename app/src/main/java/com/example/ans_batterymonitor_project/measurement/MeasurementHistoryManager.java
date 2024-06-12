package com.example.ans_batterymonitor_project.measurement;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeasurementHistoryManager {
    private static final String HISTORY_FILE = "measurementHistory.json";

    public static void addToHistory(JSONObject measurementJSON, Context context) {
        JSONArray history = loadHistory(context);
        history.put(measurementJSON);
        saveHistory(history, context);
    }

    public static JSONArray loadHistory(Context context) {
        File file = new File(context.getFilesDir(), HISTORY_FILE);
        JSONArray history = new JSONArray();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            String response = stringBuilder.toString();

            Pattern pattern = Pattern.compile("\\{.*?\\}");
            Matcher matcher = pattern.matcher(response);
            while (matcher.find()) {
                String jsonStr = matcher.group();
                JSONObject jsonObject = new JSONObject(jsonStr);
                history.put(jsonObject);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return history;
    }

    public static void saveHistory(JSONArray history, Context context) {
        try {
            String json = history.toString();
            Log.d("saveHistory", json);
            File file = new File(context.getFilesDir(), HISTORY_FILE);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(json);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearHistory(Context context) {
        saveHistory(new JSONArray(), context);
    }
}
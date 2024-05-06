package com.example.ans_batterymonitor_project.measurement;

import java.util.concurrent.TimeUnit;

public class Measurement {
    private float minVoltage;
    private float maxVoltage;
    private float totalVoltage;
    private int count;
    private long startTime;
    private long pauseStartTime;
    private long pausedDuration;
    private float movingAverage;

    public Measurement() {
        this.minVoltage = Float.MAX_VALUE;
        this.maxVoltage = Float.MIN_VALUE;
        this.totalVoltage = 0;
        this.count = 0;
        this.startTime = System.currentTimeMillis();
        this.pauseStartTime = 0;
        this.pausedDuration = 0;
        this.movingAverage = 0;
    }

    public void addMeasurement(float voltage) {
        if (isPaused()) {
            return;
        }
        if (voltage < minVoltage) {
            minVoltage = voltage;
        }
        if (voltage > maxVoltage) {
            maxVoltage = voltage;
        }
        totalVoltage += voltage;
        count++;
        calculateMovingAverage();
    }

    private void calculateMovingAverage() {
        movingAverage = totalVoltage / count;
    }

    public String getMinVoltage() {
        return String.format("%.2f", minVoltage);
    }

    public String getMaxVoltage() {
        return String.format("%.2f", maxVoltage);
    }

    public String getMovingAverage() {
        return String.format("%.2f", movingAverage);
    }

    public String getDuration() {
        long durationMillis;
        if (isPaused()) {
            durationMillis = pauseStartTime - startTime - pausedDuration;
        } else {
            durationMillis = System.currentTimeMillis() - startTime - pausedDuration;
        }

        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void pauseMeasurement() {
        if (!isPaused()) {
            pauseStartTime = System.currentTimeMillis();
        }
    }

    public void resumeMeasurement() {
        if (isPaused()) {
            pausedDuration += System.currentTimeMillis() - pauseStartTime;
            pauseStartTime = 0;
        }
    }

    public boolean isPaused() {
        return pauseStartTime != 0;
    }
}

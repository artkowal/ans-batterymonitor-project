package com.example.ans_batterymonitor_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.ans_batterymonitor_project.bt.BluetoothLeService;
import com.example.ans_batterymonitor_project.bt.DataListener;
import com.example.ans_batterymonitor_project.databinding.ActivityMainBinding;
import com.example.ans_batterymonitor_project.measurement.Measurement;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements DataListener {

    ActivityMainBinding binding;

    public final static BluetoothLeService bluetoothLeService = new BluetoothLeService();
    private boolean firstTime = true;
    public Measurement measurement = null;
    private boolean measurementIsActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.measurement) {
                replaceFragment(new MeasurementFragment());
            } else if (id == R.id.history) {
                replaceFragment(new HistoryFragment());
            } else if (id == R.id.setting) {
                replaceFragment(new SettingsFragment());
            }

            return true;
        });

        bluetoothLeService.setDataListener(this);

        // Sprawdź, czy aplikacja została uruchomiona po raz pierwszy
        SharedPreferences prefs = getSharedPreferences("com.example.ans_batterymonitor_project", MODE_PRIVATE);
        firstTime = prefs.getBoolean("firstTime", true);

        if (firstTime) {
            // Uruchom SplashScreen po raz pierwszy
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(intent);
            // Ustaw flagę firstTime na false, aby nie pokazać SplashScreena ponownie
            prefs.edit().putBoolean("firstTime", false).apply();
        }
    }

    @Override
    public void onBackPressed() {
        // Wyjście z aplikacji po naciśnięciu przycisku wstecz
        moveTaskToBack(true);
    }

    @Override
    public void onDataReceived(float data) {
        // Obsługa odebranych danych
        handleData(data);
    }

    public void handleData(float number) {
//        Log.d("main", String.valueOf(number));
        // Aktualizacja aktualnego pomiaru
        if (measurementIsActive) {
            measurement.addMeasurement(number);
        }

        // Aktualizacja UI
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof HomeFragment) {
            HomeFragment homeFragment = (HomeFragment) currentFragment;
            homeFragment.handleDataInFragment(number);
        }

        if (currentFragment instanceof MeasurementFragment) {
            MeasurementFragment measurementFragment = (MeasurementFragment) currentFragment;
            measurementFragment.handleDataInFragment(number);

            if (measurementIsActive) {
                measurementFragment.handleMeasurementData(measurement.getDuration(), measurement.getMinVoltage(), measurement.getMaxVoltage(), measurement.getMovingAverage());
            }
        }
    }

    public void stopMeasurement() throws JSONException {
        try {
            if (measurement != null) {
                measurement.finishMeasurement(this);
            }

            measurement = null;
            measurementIsActive = false;
        } catch (JSONException e) {
            Log.e("MainActivity", e.toString());
        }

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void goToSetting() {
        replaceFragment(new SettingsFragment());
        binding.bottomNavigationView.setSelectedItemId(R.id.setting);
    }

    public void goToMeasurementAndStart() {
        replaceFragment(new MeasurementFragment());
        binding.bottomNavigationView.setSelectedItemId(R.id.measurement);

        // Rozpoczęcie pomiaru
        measurement = new Measurement();
        measurementIsActive = true;
    }
}

package com.example.ans_batterymonitor_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.ans_batterymonitor_project.bt.BluetoothLeService;
import com.example.ans_batterymonitor_project.bt.DataListener;
import com.example.ans_batterymonitor_project.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements DataListener {

    ActivityMainBinding binding;

    public final static BluetoothLeService bluetoothLeService = new BluetoothLeService();

    private int ACTIVE_FRAGMENT = 0;
    private final int FRAGMENT_HOME = 1;
    private final int FRAGMENT_MEASUREMENT = 2;
    private final int FRAGMENT_HISTORY = 3;
    private final int FRAGMENT_SETTINGS = 4;


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
    }

    @Override
    public void onDataReceived(float data) {
        // Obsługa odebranych danych
        handleData(data);
    }

    public void handleData(float number) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof HomeFragment) {
            HomeFragment homeFragment = (HomeFragment) currentFragment;
            homeFragment.handleDataInFragment(number);
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
}
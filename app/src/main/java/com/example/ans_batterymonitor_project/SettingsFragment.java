package com.example.ans_batterymonitor_project;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ans_batterymonitor_project.bt.BluetoothLeService;
import com.example.ans_batterymonitor_project.databinding.ActivityMainBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    public BluetoothLeService bluetoothLeService;
    private final String TAG = "SettingsFragment";
    private static final long SCAN_PERIOD = 10000;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private Handler handler = new Handler();
    private ArrayList<String> deviceList = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
    private void updateUI(View view, boolean connected) {
        TextView connectButton = view.findViewById(R.id.connectButton);
        TextView connectionTextView = view.findViewById(R.id.connectionTextView);
        ImageView connectionImageView = view.findViewById(R.id.connectionImageView);

        if (connected || bluetoothLeService.isConnected()) {
            connectButton.setText("DISCONNECT");
            connectButton.setVisibility(View.VISIBLE);
            connectionTextView.setText("Connected successfully");
            connectionImageView.setImageResource(R.drawable.ic_yes_512);
        } else {
            connectButton.setVisibility(View.INVISIBLE);
            connectionTextView.setText("Connection failed");
            connectionImageView.setImageResource(R.drawable.ic_no_512);
        }
    }

    private void connectOrDisconnect(View view) {
        Log.d(TAG, "conn button clicked");
        if(bluetoothLeService.isConnected()) {
            if (bluetoothLeService.disconnect()) {
                updateUI(view, false);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        bluetoothLeService = MainActivity.bluetoothLeService;

        updateUI(view, false);

        Button scanButton = view.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v -> {
            startBleScan();
        });

        Button connectButton = view.findViewById(R.id.connectButton);
        connectButton.setOnClickListener(v -> {
            connectOrDisconnect(view);
        });

        ListView deviceListView = view.findViewById(R.id.deviceListView);
        adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, deviceList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);

                String deviceInfo = deviceList.get(position);
                textView.setText(deviceInfo); // Nazwa urządzenia - Adres MAC

                return view;
            }
        };
        deviceListView.setAdapter(adapter);

        deviceListView.setOnItemClickListener((parent, view1, position, id) -> {
            String deviceInfo = deviceList.get(position);
            String[] deviceParts = deviceInfo.split("-");
            if (deviceParts.length >= 2) {
                String deviceName = deviceParts[0].trim(); // Nazwa
                String deviceAddress = deviceParts[1].trim(); // Adres MAC
//                showToast("Device: " + deviceName);

                if (scanning) {
                    stopBleScan();
                }

                if (bluetoothLeService.isConnected()) {
                    boolean ok = bluetoothLeService.disconnect();
                }

                if (!bluetoothLeService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                    return;
                }

                if (bluetoothLeService.connect(deviceAddress)) {
                    showToast("Connected to device: " + deviceName);
                    // Połączenie działa
                    updateUI(view, true);
                    //
                } else {
                    updateUI(view, false);
                    showToast("Failed to connect to device: " + deviceName);
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopBleScan();
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return false;
    }

    private void requestLocationEnabled() {
        Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(locationSettingsIntent);
    }

    private void startBleScan() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Urządzenie nie obsługuje Bluetootha
            showToast("Device does not support Bluetooth");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth jest wyłączony
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        if (!isLocationEnabled()) {
            requestLocationEnabled();
            return;
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Brakuje uprawnień lokalizacyjnych
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }

        // Wszystko działa, można skanować
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (!scanning) {
            showToast("BLE Scan Started");
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopBleScan();
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            stopBleScan();
        }
    }

    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    String deviceInfo = device.getName() + " - " + device.getAddress();
                    if (device.getName() != null && !deviceList.contains(deviceInfo)) {
                        deviceList.add(deviceInfo);
                        adapter.notifyDataSetChanged();
                    }
                    Log.d("SCANNING", "Device found: " + deviceInfo);
                }
            };

    private void stopBleScan() {
        if (bluetoothLeScanner != null && scanning) {
            bluetoothLeScanner.stopScan(leScanCallback);
            scanning = false;
            Log.d(TAG, "BLE Scan Stopped");
        }
    }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}

package com.example.ans_batterymonitor_project.bt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";

    public final static UUID UUID_HM10_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_HM10_CHARACTERISTIC = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;

    private int connectionState;
    public static final String TAG = "BluetoothLeService";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    BluetoothGattService service = null;
    BluetoothGattCharacteristic characteristic = null;

    private DataListener dataListener;

    public void setDataListener(DataListener listener) {
        this.dataListener = listener;
    }

    public boolean initialize() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        try {
            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            // connect to the GATT server on the device
            bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
            return true;
        } catch (IllegalArgumentException exception) {
            Log.w(TAG, "Device not found with provided address.  Unable to connect.");
            return false;
        }
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                connectionState = STATE_CONNECTED;
                // Attempts to discover services after successful connection.
                bluetoothGatt.discoverServices();
                broadcastUpdate(ACTION_GATT_CONNECTED);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                connectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(UUID_HM10_SERVICE);
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_HM10_CHARACTERISTIC);
                    if (characteristic != null) {
                        // Ustawiamy właściwości charakterystyki na NOTIFY, aby umożliwić odbiór danych
                        gatt.setCharacteristicNotification(characteristic, true);
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_HM10_CHARACTERISTIC);
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
                    } else {
                        Log.e(TAG, "Characteristic not found");
                    }
                } else {
                    Log.e(TAG, "Service not found");
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                try {
                    float f = Float.parseFloat(new String(data));
                    if(dataListener != null) {
                        dataListener.onDataReceived(f);
                    }
                } catch (Exception ignored) {}
            }
        }
    };

    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt == null) return null;
        return bluetoothGatt.getServices();
    }

    private void sendData(String data) {
        if (characteristic == null) {
            if (bluetoothGatt == null) {
                Log.e(TAG, "BluetoothGatt is null");
                return;
            }
            service = bluetoothGatt.getService(UUID_HM10_SERVICE);
            if (service == null) {
                Log.e(TAG, "Service not found");
                return;
            }
            characteristic = service.getCharacteristic(UUID_HM10_CHARACTERISTIC);
            if (characteristic == null) {
                Log.e(TAG, "Characteristic not found");
                return;
            }
        }

        characteristic.setValue(data.getBytes(Charset.forName("UTF-8")));
        bluetoothGatt.writeCharacteristic(characteristic);
    }

    public void sendMeasurementRequestStart() {
        sendData("s");
    }

    public void sendMeasurementRequestStop() {
        sendData("p");
    }

    public boolean isConnected() {
        return connectionState == STATE_CONNECTED;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean disconnect() {
        sendMeasurementRequestStop();
        return close();
    }

    private boolean close() {
        if (bluetoothGatt == null) {
            return true;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
        connectionState = STATE_DISCONNECTED; // "HACK."
        return true;
    }

    private Binder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}

package com.github.pwittchen.reactivebeacons.serviceapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.pwittchen.reactivebeacons.library.Beacon;
import com.github.pwittchen.reactivebeacons.library.ReactiveBeacons;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BeaconService extends Service {
    private static final String TAG = "BeaconService";
    private ReactiveBeacons reactiveBeacons;
    private Disposable subscription;
    private Map<String, Beacon> beacons; // keeps the most recent list of beacons

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "service started");
        reactiveBeacons = new ReactiveBeacons(this);
        beacons = new HashMap<>();

        if (!canObserveBeacons()) {
            return;
        }

        subscription = reactiveBeacons.observe()
                .subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<Beacon>() {
                    @Override
                    public void accept(Beacon beacon) {
                        beacons.put(beacon.device.getAddress(), beacon);
                        String message = String.format("beacon updated: %s", beacon.toString());
                        Log.d(TAG, message);
                    }
                });
    }

    private boolean canObserveBeacons() {
        if (!reactiveBeacons.isBleSupported()) {
            Log.d(TAG, "BLE is not supported on this device");
            return false;
        }

        if (!reactiveBeacons.isBluetoothEnabled()) {
            Log.d(TAG, "Bluetooth is disabled");
            return false;
        } else if (!reactiveBeacons.isLocationEnabled(this)) {
            Log.d(TAG, "Location is disabled");
            return false;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "service stopped");
        safelyUnsubscribe(subscription);
    }

    private void safelyUnsubscribe(Disposable subscription) {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

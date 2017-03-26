/*
 * Copyright (C) 2015 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pwittchen.reactivebeacons.app;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.pwittchen.reactivebeacons.R;
import com.github.pwittchen.reactivebeacons.library.Beacon;
import com.github.pwittchen.reactivebeacons.library.Proximity;
import com.github.pwittchen.reactivebeacons.library.ReactiveBeacons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MainActivity extends Activity {
  private static final boolean IS_AT_LEAST_ANDROID_M = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1000;
  private static final String ITEM_FORMAT = "MAC: %s, RSSI: %d\ndistance: %.2fm, proximity: %s\n%s";
  private ReactiveBeacons reactiveBeacons;
  private Disposable subscription;
  private ListView lvBeacons;
  private Map<String, Beacon> beacons;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    lvBeacons = (ListView) findViewById(R.id.lv_beacons);
    reactiveBeacons = new ReactiveBeacons(this);
    beacons = new HashMap<>();
  }

  @Override protected void onResume() {
    super.onResume();

    if (!canObserveBeacons()) {
      return;
    }

    startSubscription();
  }

  private void startSubscription() {
    subscription = reactiveBeacons.observe()
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Beacon>() {
          @Override public void accept(@NonNull Beacon beacon) throws Exception {
            beacons.put(beacon.device.getAddress(), beacon);
            refreshBeaconList();
          }
        });
  }

  private boolean canObserveBeacons() {
    if (!reactiveBeacons.isBleSupported()) {
      Toast.makeText(this, "BLE is not supported on this device", Toast.LENGTH_SHORT).show();
      return false;
    }

    if (!reactiveBeacons.isBluetoothEnabled()) {
      reactiveBeacons.requestBluetoothAccess(this);
      return false;
    } else if (!reactiveBeacons.isLocationEnabled(this)) {
      reactiveBeacons.requestLocationAccess(this);
      return false;
    } else if (!isFineOrCoarseLocationPermissionGranted() && IS_AT_LEAST_ANDROID_M) {
      requestCoarseLocationPermission();
      return false;
    }

    return true;
  }

  private void refreshBeaconList() {
    List<String> list = new ArrayList<>();

    for (Beacon beacon : beacons.values()) {
      list.add(getBeaconItemString(beacon));
    }

    int itemLayoutId = android.R.layout.simple_list_item_1;
    lvBeacons.setAdapter(new ArrayAdapter<>(this, itemLayoutId, list));
  }

  private String getBeaconItemString(Beacon beacon) {
    String mac = beacon.device.getAddress();
    int rssi = beacon.rssi;
    double distance = beacon.getDistance();
    Proximity proximity = beacon.getProximity();
    String name = beacon.device.getName();
    return String.format(ITEM_FORMAT, mac, rssi, distance, proximity, name);
  }

  @Override protected void onPause() {
    super.onPause();
    if (subscription != null && !subscription.isDisposed()) {
      subscription.dispose();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    final boolean isCoarseLocation = requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION;
    final boolean permissionGranted = grantResults[0] == PERMISSION_GRANTED;

    if (isCoarseLocation && permissionGranted && subscription == null) {
      startSubscription();
    }
  }

  private void requestCoarseLocationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(new String[] { ACCESS_COARSE_LOCATION },
          PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
    }
  }

  private boolean isFineOrCoarseLocationPermissionGranted() {
    boolean isAndroidMOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    boolean isFineLocationPermissionGranted = isGranted(ACCESS_FINE_LOCATION);
    boolean isCoarseLocationPermissionGranted = isGranted(ACCESS_COARSE_LOCATION);

    return isAndroidMOrHigher && (isFineLocationPermissionGranted
        || isCoarseLocationPermissionGranted);
  }

  private boolean isGranted(String permission) {
    return ActivityCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED;
  }
}

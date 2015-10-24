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
import android.os.Bundle;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {
  private static final String ITEM_FORMAT = "MAC: %s, RSSI: %d\ndistance: %.2fm, proximity: %s\n%s";
  private ReactiveBeacons reactiveBeacons;
  private Subscription subscription;
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

    subscription = reactiveBeacons.observe()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Beacon>() {
          @Override public void call(Beacon beacon) {
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
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }
}

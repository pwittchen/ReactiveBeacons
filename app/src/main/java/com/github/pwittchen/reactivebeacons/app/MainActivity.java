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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.github.pwittchen.reactivebeacons.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pwittchen.github.com.library.Beacon;
import pwittchen.github.com.library.ReactiveBeacons;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
  public static final String BEACON_ITEM_FORMAT = "MAC: %s, RSSI: %s";
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
    reactiveBeacons.requestBluetoothAccessIfDisabled(this);

    subscription = reactiveBeacons.observe()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<Beacon>() {
          @Override public void call(Beacon beacon) {
            beacons.put(beacon.device.getAddress(), beacon);
            refreshBeaconList();
          }
        });
  }

  private void refreshBeaconList() {
    List<String> list = new ArrayList<>();

    for (Beacon beacon : beacons.values()) {
      String item = String.format(BEACON_ITEM_FORMAT, beacon.device.getAddress(), beacon.rssi);
      list.add(item);
    }

    int itemLayoutId = android.R.layout.simple_list_item_1;
    lvBeacons.setAdapter(new ArrayAdapter<>(this, itemLayoutId, list));
  }

  @Override protected void onPause() {
    super.onPause();
    subscription.unsubscribe();
  }
}

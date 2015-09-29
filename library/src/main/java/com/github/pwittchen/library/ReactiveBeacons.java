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
package com.github.pwittchen.library;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import rx.Observable;
import rx.functions.Action0;

/**
 * An Android library monitoring Bluetooth Low Energy (BLE) beacons with RxJava
 */
public class ReactiveBeacons {
  private BluetoothAdapter bluetoothAdapter;
  private LeScanCallbackAdapter leScanCallbackAdapter;

  /**
   * Initializes ReactiveBeacons object
   *
   * @param context context of the activity or application
   */
  public ReactiveBeacons(Context context) {
    String bluetoothService = Context.BLUETOOTH_SERVICE;
    BluetoothManager manager = (BluetoothManager) context.getSystemService(bluetoothService);
    bluetoothAdapter = manager.getAdapter();
  }

  /**
   * starts intent requesting Bluetooth connection, which can be enabled by user
   * if it's not enabled already
   *
   * @param activity current Activity
   */
  public void requestBluetoothAccessIfDisabled(Activity activity) {
    boolean isBluetoothEnabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled();

    if (isBluetoothEnabled) {
      return;
    }

    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    activity.startActivityForResult(intent, Activity.RESULT_FIRST_USER);
  }

  /**
   * Creates an observable stream of BLE beacons, which can be subscribed with RxJava
   *
   * @return Observable stream of beacons
   */
  @SuppressWarnings("deprecation") public Observable<Beacon> observe() {
    leScanCallbackAdapter = new LeScanCallbackAdapter();
    bluetoothAdapter.startLeScan(leScanCallbackAdapter);
    return leScanCallbackAdapter.toObservable().repeat().distinct().doOnUnsubscribe(new Action0() {
      @Override public void call() {
        bluetoothAdapter.stopLeScan(leScanCallbackAdapter);
      }
    });
  }
}

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
package com.github.pwittchen.reactivebeacons.library;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import rx.Observable;
import rx.functions.Action0;

/**
 * An Android library monitoring Bluetooth Low Energy (BLE) beacons with RxJava
 */
public class ReactiveBeacons {
  private BluetoothAdapter bluetoothAdapter;
  private LeScanCallbackAdapter leScanCallbackAdapter;
  private AccessRequester accessRequester;

  /**
   * Initializes ReactiveBeacons object
   *
   * @param context context of the activity or application
   */
  @SuppressLint("NewApi") public ReactiveBeacons(Context context) {
    String bluetoothService = Context.BLUETOOTH_SERVICE;

    if (isBleSupported()) {
      BluetoothManager manager = (BluetoothManager) context.getSystemService(bluetoothService);
      bluetoothAdapter = manager.getAdapter();
      accessRequester = new AccessRequester(bluetoothAdapter);
    }
  }

  /**
   * Checks if Bluetooth is enabled
   *
   * @return boolean true if enabled
   */
  public boolean isBluetoothEnabled() {
    checkAccessRequesterIsNotNull();
    return accessRequester.isBluetoothEnabled();
  }

  /**
   * Checks if location provider is enabled
   *
   * @param context current Context
   * @return boolean true if enabled
   */
  public boolean isLocationEnabled(Context context) {
    checkAccessRequesterIsNotNull();
    return accessRequester.isLocationEnabled(context);
  }

  /**
   * starts intent requesting Bluetooth connection, which can be enabled by user
   * if it's not enabled already
   *
   * @param activity current Activity
   */
  public void requestBluetoothAccess(Activity activity) {
    checkAccessRequesterIsNotNull();
    accessRequester.requestBluetoothAccess(activity);
  }

  /**
   * starts dialog, which can navigate user to location setting, where user can enable location
   * if it's not enabled already
   *
   * @param activity current Activity
   */
  public void requestLocationAccess(final Activity activity) {
    checkAccessRequesterIsNotNull();
    accessRequester.requestLocationAccess(activity);
  }

  private void checkAccessRequesterIsNotNull() {
    if (accessRequester == null) {
      throw new IllegalStateException("BLE is not supported, so cannot create AccessRequester");
    }
  }

  /**
   * Creates an observable stream of BLE beacons, which can be subscribed with RxJava
   *
   * @return Observable stream of beacons
   */
  @SuppressWarnings("deprecation") @SuppressLint("NewApi") public Observable<Beacon> observe() {
    if (!isBleSupported()) {
      return Observable.empty();
    }

    leScanCallbackAdapter = new LeScanCallbackAdapter();
    bluetoothAdapter.startLeScan(leScanCallbackAdapter);

    return leScanCallbackAdapter.toObservable()
        .repeat()
        .distinctUntilChanged()
        .doOnUnsubscribe(new Action0() {
          @Override public void call() {
            bluetoothAdapter.stopLeScan(leScanCallbackAdapter);
          }
        });
  }

  /**
   * Checks if Bluetooth Low Energy is enabled in the current Android version
   *
   * @return boolean
   */
  public boolean isBleSupported() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
  }
}

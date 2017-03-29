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
package com.github.pwittchen.reactivebeacons.library.rx2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import com.github.pwittchen.reactivebeacons.library.rx2.scan.strategy.ScanStrategy;
import com.github.pwittchen.reactivebeacons.library.rx2.scan.strategy.lollipop.LollipopScanStrategy;
import com.github.pwittchen.reactivebeacons.library.rx2.scan.strategy.prelollipop.PreLollipopScanStrategy;
import io.reactivex.Observable;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * An Android library monitoring Bluetooth Low Energy (BLE) beacons with RxJava
 */
public class ReactiveBeacons {
  private static final String MSG_BLE_NOT_SUPPORTED = "accessRequester is null, BLE not supported";
  private static final String MSG_SCAN_STRATEGY_CANNOT_BE_NULL = "scanStrategy cannot be null";

  private BluetoothAdapter bluetoothAdapter;
  private ScanStrategy scanStrategy;
  private AccessRequester accessRequester;

  /**
   * Initializes ReactiveBeacons object
   *
   * @param context context of the activity or application
   */
  @SuppressLint("NewApi") public ReactiveBeacons(Context context) {
    if (isBleSupported()) {
      String bluetoothService = Context.BLUETOOTH_SERVICE;
      BluetoothManager manager = (BluetoothManager) context.getSystemService(bluetoothService);
      bluetoothAdapter = manager.getAdapter();
      accessRequester = new AccessRequester(bluetoothAdapter);
    }
  }

  /**
   * Checks if Bluetooth Low Energy is enabled in the current Android version
   *
   * @return boolean
   */
  public boolean isBleSupported() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
  }

  /**
   * Checks if Bluetooth is enabled
   *
   * @return boolean true if enabled
   */
  public boolean isBluetoothEnabled() {
    checkNotNull(accessRequester, MSG_BLE_NOT_SUPPORTED);
    return accessRequester.isBluetoothEnabled();
  }

  /**
   * Checks if location provider is enabled
   *
   * @param context current Context
   * @return boolean true if enabled
   */
  public boolean isLocationEnabled(Context context) {
    checkNotNull(accessRequester, MSG_BLE_NOT_SUPPORTED);
    return accessRequester.isLocationEnabled(context);
  }

  /**
   * Starts intent requesting Bluetooth connection, which can be enabled by user
   * if it's not enabled already
   *
   * @param activity current Activity
   */
  public void requestBluetoothAccess(Activity activity) {
    checkNotNull(accessRequester, MSG_BLE_NOT_SUPPORTED);
    accessRequester.requestBluetoothAccess(activity);
  }

  /**
   * Creates an observable stream of BLE beacons, which can be subscribed with RxJava
   * Uses appropriate BLE scan strategy according to Android version installed on a device
   *
   * @return Observable stream of beacons
   */
  @RequiresPermission(anyOf = {
      ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION
  }) public Observable<Beacon> observe() {
    if (!isBleSupported()) {
      return Observable.empty();
    }

    if (isAtLeastAndroidLollipop()) {
      scanStrategy = new LollipopScanStrategy(bluetoothAdapter);
    } else {
      scanStrategy = new PreLollipopScanStrategy(bluetoothAdapter);
    }

    try {
      return observe(scanStrategy);
    } catch (SecurityException e) {
      return Observable.empty();
    }
  }

  /**
   * Creates an observable stream of BLE beacons, which can be subscribed with RxJava.
   * This method can scan beacons with provided scan strategy, which can be one of the existing
   * strategies like {@link LollipopScanStrategy} or {@link PreLollipopScanStrategy} or any
   * custom scan strategy implementing {@link ScanStrategy} interface
   *
   * @param scanStrategy BLE scan strategy
   * @return Observable stream of beacons
   */
  @RequiresPermission(anyOf = {
      ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION
  }) public Observable<Beacon> observe(ScanStrategy scanStrategy) {
    if (!isBleSupported()) {
      return Observable.empty();
    }

    checkNotNull(scanStrategy, MSG_SCAN_STRATEGY_CANNOT_BE_NULL);
    return scanStrategy.observe();
  }

  /**
   * Checks is device has installed at least Lollipop Android version
   *
   * @return true if has installed at least Lollipop and false in opposite case
   */
  public boolean isAtLeastAndroidLollipop() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
  }

  /**
   * starts dialog, which can navigate user to location setting, where user can enable location
   * if it's not enabled already
   *
   * @param activity current Activity
   */
  public void requestLocationAccess(final Activity activity) {
    checkNotNull(accessRequester, MSG_BLE_NOT_SUPPORTED);
    accessRequester.requestLocationAccess(activity);
  }

  private void checkNotNull(Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }
}

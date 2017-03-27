/*
 * Copyright (C) 2016 Piotr Wittchen
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

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import com.github.pwittchen.reactivebeacons.library.R;

class AccessRequester {
  private final BluetoothAdapter bluetoothAdapter;

  public AccessRequester(BluetoothAdapter bluetoothAdapter) {
    this.bluetoothAdapter = bluetoothAdapter;
  }

  public boolean isBluetoothEnabled() {
    return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
  }

  public boolean isLocationEnabled(Context context) {
    LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    boolean isGpsProviderEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    boolean isNetworkProviderEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    return isGpsProviderEnabled || isNetworkProviderEnabled;
  }

  public void requestBluetoothAccess(Activity activity) {
    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    activity.startActivityForResult(intent, Activity.RESULT_FIRST_USER);
  }

  public void requestLocationAccess(final Activity activity) {
    buildLocationAccessDialog(activity, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivity(intent);
      }
    }).show();
  }

  private AlertDialog.Builder buildLocationAccessDialog(Activity activity,
      DialogInterface.OnClickListener onOkClickListener) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setTitle(R.string.requesting_location_access);
    builder.setMessage(R.string.do_you_want_to_open_location_settings);
    builder.setPositiveButton(android.R.string.ok, onOkClickListener);
    builder.setNegativeButton(android.R.string.no, null);
    builder.setCancelable(true);
    return builder;
  }
}

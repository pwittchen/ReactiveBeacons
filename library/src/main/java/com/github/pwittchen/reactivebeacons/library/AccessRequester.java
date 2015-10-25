package com.github.pwittchen.reactivebeacons.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

class AccessRequester {
  private static final String EMPTY_STRING = "";
  private final BluetoothAdapter bluetoothAdapter;

  public AccessRequester(BluetoothAdapter bluetoothAdapter) {
    this.bluetoothAdapter = bluetoothAdapter;
  }

  public boolean isBluetoothEnabled() {
    return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
  }

  @SuppressWarnings("deprecation") public boolean isLocationEnabled(Context context) {
    String name = Settings.Secure.LOCATION_PROVIDERS_ALLOWED;
    ContentResolver contentResolver = context.getContentResolver();
    String providers = Settings.Secure.getString(contentResolver, name);
    return providers != null && !providers.equals(EMPTY_STRING);
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

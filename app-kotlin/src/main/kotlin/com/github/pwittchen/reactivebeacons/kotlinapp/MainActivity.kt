package com.github.pwittchen.reactivebeacons.kotlinapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import android.widget.ArrayAdapter
import android.widget.Toast
import com.github.pwittchen.reactivebeacons.library.rx2.Beacon
import com.github.pwittchen.reactivebeacons.library.rx2.ReactiveBeacons
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.lv_beacons
import java.util.HashMap

class MainActivity : Activity() {
  private val IS_AT_LEAST_ANDROID_M = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
  private val PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1000
  private var reactiveBeacons: ReactiveBeacons? = null
  private var subscription: Disposable? = null
  private var beacons: MutableMap<String, Beacon> = HashMap()

  companion object {
    private val BEACON = "MAC: %s, RSSI: %d\ndistance: %.2fm, proximity: %s\n%s"
    private val BLE_NOT_SUPPORTED = "BLE is not supported on this device";
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun onResume() {
    super.onResume()
    reactiveBeacons = ReactiveBeacons(this)

    if (!canObserveBeacons()) {
      return
    }

    startSubscription()
  }

  private fun startSubscription() {
    if (reactiveBeacons != null) {
      subscription = (reactiveBeacons as ReactiveBeacons).observe()
          .subscribeOn(Schedulers.computation())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { beacon -> beacons.put(beacon.device.address, beacon); refreshBeacons() }
    }
  }

  private fun canObserveBeacons(): Boolean {

    if (reactiveBeacons != null) {

      if (!(reactiveBeacons as ReactiveBeacons).isBleSupported) {
        Toast.makeText(this, BLE_NOT_SUPPORTED, Toast.LENGTH_SHORT).show()
        return false
      }

      if (!(reactiveBeacons as ReactiveBeacons).isBluetoothEnabled) {
        (reactiveBeacons as ReactiveBeacons).requestBluetoothAccess(this)
        return false
      } else if (!(reactiveBeacons as ReactiveBeacons).isLocationEnabled(this)) {
        (reactiveBeacons as ReactiveBeacons).requestLocationAccess(this)
        return false
      } else if (!isFineOrCoarseLocationPermissionGranted() && IS_AT_LEAST_ANDROID_M) {
        requestCoarseLocationPermission()
        return false
      }

      return true
    }

    return false
  }

  private fun refreshBeacons() {
    val list = beacons.values.map {
      BEACON.format(it.device.address, it.rssi, it.distance, it.proximity, it.device.name)
    }

    lv_beacons.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
  }

  override fun onPause() {
    super.onPause()
    safelyUnsubscribe(subscription)
  }

  private fun safelyUnsubscribe(subscription: Disposable?) {
    if (subscription != null && !subscription.isDisposed) {
      subscription.dispose()
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>,
      @NonNull grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    val isCoarseLocation = requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
    val permissionGranted = grantResults[0] == PERMISSION_GRANTED

    if (isCoarseLocation && permissionGranted && subscription == null) {
      startSubscription()
    }
  }

  private fun requestCoarseLocationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(arrayOf<String>(ACCESS_COARSE_LOCATION),
          PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION)
    }
  }

  private fun isFineOrCoarseLocationPermissionGranted(): Boolean {
    val isAndroidMOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    val isFineLocationPermissionGranted = isGranted(ACCESS_FINE_LOCATION)
    val isCoarseLocationPermissionGranted = isGranted(ACCESS_COARSE_LOCATION)

    return isAndroidMOrHigher && (isFineLocationPermissionGranted || isCoarseLocationPermissionGranted)
  }

  private fun isGranted(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
  }
}

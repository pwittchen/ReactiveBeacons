package com.github.pwittchen.reactivebeacons.kotlinapp

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.github.pwittchen.reactivebeacons.library.Beacon
import com.github.pwittchen.reactivebeacons.library.ReactiveBeacons
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.lv_beacons
import java.util.ArrayList
import java.util.HashMap

class MainActivity : Activity() {
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
        val reactiveBeacons: ReactiveBeacons = ReactiveBeacons(this)

        if (!canObserveBeacons(reactiveBeacons)) {
            return
        }

        subscription = reactiveBeacons.observe()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { beacon -> beacons.put(beacon.device.address, beacon); refreshBeacons() }
    }

    private fun canObserveBeacons(reactiveBeacons: ReactiveBeacons): Boolean {
        if (!reactiveBeacons.isBleSupported) {
            Toast.makeText(this, BLE_NOT_SUPPORTED, Toast.LENGTH_SHORT).show()
            return false
        }

        if (!reactiveBeacons.isBluetoothEnabled) {
            reactiveBeacons.requestBluetoothAccess(this)
            return false
        } else if (!reactiveBeacons.isLocationEnabled(this)) {
            reactiveBeacons.requestLocationAccess(this)
            return false
        }

        return true
    }

    private fun refreshBeacons() {
        val list = ArrayList<String>()

        for (b in beacons.values) {
            list.add(BEACON.format(b.device.address, b.rssi, b.distance, b.proximity, b.device.name))
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
}

package com.github.pwittchen.reactivebeacons.kotlinapp

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.github.pwittchen.reactivebeacons.library.Beacon
import com.github.pwittchen.reactivebeacons.library.ReactiveBeacons
import kotlinx.android.synthetic.activity_main.lv_beacons
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class MainActivity : Activity() {
    private var subscription: Subscription? = null
    private var beacons: MutableMap<String, Beacon> = HashMap()

    companion object {
        private val BEACON = "MAC: %s, RSSI: %d\ndistance: %.2fm, proximity: %s\n%s"
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { beacon -> beacons.put(beacon.device.address, beacon); refreshBeacons() }
    }

    private fun canObserveBeacons(reactiveBeacons: ReactiveBeacons): Boolean {
        if (!reactiveBeacons.isBleSupported) {
            Toast.makeText(this, "BLE is not supported on this device", Toast.LENGTH_SHORT).show()
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

    private fun safelyUnsubscribe(subscription: Subscription?) {
        if (subscription != null && !subscription.isUnsubscribed) {
            subscription.unsubscribe()
        }
    }
}

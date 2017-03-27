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
package com.github.pwittchen.reactivebeacons.library.rx2.scan.strategy.prelollipop;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;

import com.github.pwittchen.reactivebeacons.library.rx2.Beacon;
import com.github.pwittchen.reactivebeacons.library.rx2.FutureAdapter;

import io.reactivex.Observable;


@SuppressLint("NewApi")
public class LeScanCallbackAdapter implements LeScanCallback {
    private final FutureAdapter futureAdapter = new FutureAdapter();

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        futureAdapter.setBeacon(Beacon.create(device, rssi, scanRecord));
    }

    public Observable<Beacon> toObservable() {
        return Observable.fromFuture(futureAdapter);
    }
}

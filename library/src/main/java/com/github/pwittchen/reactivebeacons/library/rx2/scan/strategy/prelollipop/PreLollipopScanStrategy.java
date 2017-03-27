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
package com.github.pwittchen.reactivebeacons.library.rx2.scan.strategy.prelollipop;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;

import com.github.pwittchen.reactivebeacons.library.rx2.Beacon;
import com.github.pwittchen.reactivebeacons.library.rx2.scan.strategy.ScanStrategy;

import io.reactivex.Observable;
import io.reactivex.functions.Action;


public class PreLollipopScanStrategy implements ScanStrategy {
    private final BluetoothAdapter bluetoothAdapter;
    private final LeScanCallbackAdapter leScanCallbackAdapter;

    public PreLollipopScanStrategy(final BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.leScanCallbackAdapter = new LeScanCallbackAdapter();
    }

    @Override
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public Observable<Beacon> observe() {
        bluetoothAdapter.startLeScan(leScanCallbackAdapter);

        return leScanCallbackAdapter.toObservable()
                .repeat()
                .distinctUntilChanged()
                .doOnTerminate(new Action() {
                    @Override
                    public void run() {
                        bluetoothAdapter.stopLeScan(leScanCallbackAdapter);
                    }
                });
    }
}

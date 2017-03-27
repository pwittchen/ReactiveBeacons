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
package com.github.pwittchen.reactivebeacons.library.rx2.scan.strategy.lollipop;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;

import com.github.pwittchen.reactivebeacons.library.rx2.Beacon;
import com.github.pwittchen.reactivebeacons.library.rx2.scan.strategy.ScanStrategy;

import io.reactivex.Observable;
import io.reactivex.functions.Action;


public class LollipopScanStrategy implements ScanStrategy {
    private final BluetoothLeScanner bluetoothLeScanner;
    private final ScanCallbackAdapter scanCallbackAdapter;

    @SuppressLint("NewApi")
    public LollipopScanStrategy(final BluetoothAdapter bluetoothAdapter) {
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        this.scanCallbackAdapter = new ScanCallbackAdapter();
    }

    @Override
    @SuppressLint("NewApi")
    public Observable<Beacon> observe() {
        bluetoothLeScanner.startScan(scanCallbackAdapter);

        return scanCallbackAdapter.toObservable()
                .repeat()
                .distinctUntilChanged()
                .doOnTerminate(new Action() {
                    @Override
                    public void run() {
                        bluetoothLeScanner.stopScan(scanCallbackAdapter);
                    }
                });
    }
}

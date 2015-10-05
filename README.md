# ReactiveBeacons

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ReactiveBeacons-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/2576)
[![Build Status](https://travis-ci.org/pwittchen/ReactiveBeacons.svg)](https://travis-ci.org/pwittchen/ReactiveBeacons)
![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivebeacons.svg?style=flat)

Android library scanning BLE (Bluetooth Low Energy) beacons nearby with RxJava

Library was tested with Estimote and Kontakt beacons.

This library has limited functionality, but its API is very simple and has just three methods:

```java
ReactiveBeacons(context)
void requestBluetoothAccessIfDisabled(activity)
Observable<Beacon> observe()
```

JavaDoc is available at: http://pwittchen.github.io/ReactiveBeacons/

Contents
--------
- [Usage](#usage)
- [Example](#example)
- [Beacon class](#beacon-class)
- [Filter class](#filter-class)
- [Download](#download)
- [Code style](#code-style)
- [References](#references)
- [License](#license)

Usage
-----

### Step 1

Initialize `ReactiveBeacons` object:

```java
private ReactiveBeacons reactiveBeacons;

@Override protected void onCreate(Bundle savedInstanceState) {
  reactiveBeacons = new ReactiveBeacons(this);
}
```

### Step 2

Create subscribtion:

```java
private Subscription subscription;

@Override protected void onResume() {
  super.onResume();
  
  // optionally, we can request Bluetooth Access
  reactiveBeacons.requestBluetoothAccessIfDisabled(this);

  subscription = reactiveBeacons.observe()
    .observeOn(AndroidSchedulers.mainThread())
    .subscribeOn(Schedulers.io())
    .subscribe(new Action1<Beacon>() {
      @Override public void call(Beacon beacon) {
        // do something with beacon
      }
    });
}    
```

### Step 3

Unsubscribe subscription in `onPause()` method to stop BLE scan.

```java
@Override protected void onPause() {
  super.onPause();
  subscription.unsubscribe();
}
```

**Please note**: Library may emit information about the same beacon multiple times. New emission is created everytime when RSSI changes. We can distinguish several beacons by their MAC addresses with `beacon.device.getAddress()` method.

Example
-------

Exemplary application is located in `app` directory of this repository.

Beacon class
------------

`Beacon` class represents BLE beacon and has the following attributes:

```java
BluetoothDevice device;
int rssi;
byte[] scanRecord;
int txPower;
```

All of the elements are assigned dynamically, but `txPower` has default value equal to `-59`.
It works quite fine for different types of beacons.

Beacon class has also `getDistance()` method, which returns distance from mobile device to beacon in meters and `getProximity()` method, which returns `Proximity` value.

`Proximity` can be as follows:
- `IMMEDIATE` - from 0m to 1m
- `NEAR` - from 1m to 3m
- `FAR` - more than 3m

Beacon class has also static `create(...)` method responsible for creating Beacon objects.

Filter class
------------

`Filter` class provides static filtering methods, which can be used with RxJava `filter(...)` method inside specific subscription.

Currently the following filters are available:
- `proximityIsEqualTo(Proximity)`
- `proximityIsNotEqualTo(Proximity)`
- `distanceIsEqualTo(double)`
- `distanceIsGreaterThan(double)`
- `distanceIsLowerThan(double)`
- `hasName(String)`
- `hasMacAddress(String)`

Of course, we can create our own custom filters, which are not listed above if we need to.

**Exemplary usage**

In the example below, we are filtering all Beacons with `Proximity` equal to `NEAR` value.

```java
reactiveBeacons.observe()
    .filter(Filter.proximityIsEqualTo(Proximity.NEAR))
    .subscribe(new Action1<Beacon>() {
      @Override public void call(Beacon beacon) {
        beacons.put(beacon.device.getAddress(), beacon);
        refreshBeaconList();
      }
    });
```    

Download
--------

You can depend on the library through Maven:

```xml
<dependency>
    <groupId>com.github.pwittchen</groupId>
    <artifactId>reactivebeacons</artifactId>
    <version>0.1.0</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  compile 'com.github.pwittchen:reactivebeacons:0.1.0'
}
```

Code style
----------

Code style used in the project is called `SquareAndroid` from Java Code Styles repository by Square available at: https://github.com/square/java-code-styles. Currently, library doesn't have checkstyle verification attached. It can be done in the future.

References
----------

### Useful resources

- [Bluetooth Low Energy on Wikipedia](https://en.wikipedia.org/wiki/Bluetooth_low_energy)
- [android-bluetooth-demo repository](https://github.com/Pixplicity/android-bluetooth-demo)
- [Converting callbacks to RxJava Observables](https://getpocket.com/a/read/1052659262)
- [Transmission power range and RSSI](https://support.kontakt.io/hc/en-gb/articles/201621521-Transmission-power-Range-and-RSSI)
- [What are Broadcasting Power, RSSI and other characteristics of beacon's signal?](https://community.estimote.com/hc/en-us/articles/201636913-What-are-Broadcasting-Power-RSSI-and-other-characteristics-of-beacon-s-signal-)
- [Estimating beacon proximity/distance based on RSSI - Bluetooth LE](http://stackoverflow.com/questions/22784516/estimating-beacon-proximity-distance-based-on-rssi-bluetooth-le)
- [RSSI (Received Signal Strength Indication) on Wikipedia](https://en.wikipedia.org/wiki/Received_signal_strength_indication)
- [Specification for Eddystone, an open beacon format from Google](https://github.com/google/eddystone)

### Producers of BLE beacons

- [Estimote](http://estimote.com)
- [Kontakt.io](http://kontakt.io)

### Other APIs and libraries
- [Android SDK by Estimote](https://github.com/Estimote/Android-SDK)
- [Android SDK by Kontakt.io](https://github.com/kontaktio/Android-SDK)
- [Android Beacon Library by AltBeacon](https://github.com/AltBeacon/android-beacon-library)


License
-------

    Copyright 2015 Piotr Wittchen

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


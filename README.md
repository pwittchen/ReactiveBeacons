# ReactiveBeacons

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ReactiveBeacons-brightgreen.svg?style=flat-square)](http://android-arsenal.com/details/1/2576)

| Current Branch | Branch  | Artifact Id | Build Status  | Maven Central |
|:--------------:|:-------:|:-----------:|:-------------:|:-------------:|
| | [`RxJava1.x`](https://github.com/pwittchen/ReactiveBeacons/tree/RxJava1.x) | `reactivebeacons` | [![Build Status for RxJava1.x](https://img.shields.io/travis/pwittchen/ReactiveBeacons/RxJava1.x.svg?style=flat-square)](https://travis-ci.org/pwittchen/ReactiveBeacons) | ![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivebeacons.svg?style=flat-square) |
| :ballot_box_with_check: | [`RxJava2.x`](https://github.com/pwittchen/ReactiveBeacons/tree/RxJava2.x) | `reactivebeacons-rx2` | [![Build Status for RxJava2.x](https://img.shields.io/travis/pwittchen/ReactiveBeacons/RxJava2.x.svg?style=flat-square)](https://travis-ci.org/pwittchen/ReactiveBeacons) |![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivebeacons-rx2.svg?style=flat-square) |


This is **RxJava2.x** branch. To see documentation for RxJava1.x, switch to [RxJava1.x](https://github.com/pwittchen/ReactiveBeacons/tree/RxJava1.x) branch.

Android library scanning BLE (Bluetooth Low Energy) beacons nearby with RxJava

Library was tested with Estimote and Kontakt beacons.

This library has limited functionality, but its API is simple:

```java
ReactiveBeacons(context)
boolean isBleSupported()
boolean isBluetoothEnabled()
boolean isLocationEnabled(context)
boolean isAtLeastAndroidLollipop()
void requestBluetoothAccess(activity)
void requestLocationAccess(activity)
Observable<Beacon> observe()
Observable<Beacon> observe(ScanStrategy scanStrategy)
```

JavaDoc is available at: http://pwittchen.github.io/ReactiveBeacons/RxJava2.x

min SDK = 9, but if you are using API level lower than 18, don't forget to [check BLE support](#checking-ble-support) on the device.

Contents
--------
- [Usage](#usage)
- [Good practices](#good-practices)
  - [Updating Manifest](#updating-manifest)
  - [Checking BLE support](#checking-ble-support)
  - [Requesting Bluetooth access](#requesting-bluetooth-access)
  - [Requesting Location access](#requesting-location-access)
  - [Requesting Runtime Permissions](#requesting-runtime-permissions)
  - [Exemplary code snippet](#exemplary-code-snippet)
- [Examples](#examples)
- [Compatibility with different Android versions](#compatibility-with-different-android-versions)
- [Beacon class](#beacon-class)
- [Filter class](#filter-class)
- [Download](#download)
- [Tests](#tests)
- [Code style](#code-style)
- [Static code analysis](#static-code-analysis)
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
private Disposable subscription;

@Override protected void onResume() {
  super.onResume();
  
  if (!reactiveBeacons.isBleSupported()) { // optional, but recommended step
    // show message for the user that BLE is not supported on the device
    return;
  }
  
  // we should check Bluetooth and Location access here
  // if they're disabled, we can request access
  // if you want to know how to do it, check next sections 
  // of this documentation and sample app

  subscription = reactiveBeacons.observe()
    .subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Consumer<Beacon>() {
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
  if (subscription != null && !subscription.isDisposed()) {
    subscription.dispose()
  }
}
```

**Please note**: Library may emit information about the same beacon multiple times. New emission is created everytime when RSSI changes. We can distinguish several beacons by their MAC addresses with `beacon.device.getAddress()` method.

Good practices
--------------

### Updating Manifest

Add `<uses-feature .../>` tag inside `<manifest ...>` tag in `AndroidManifest.xml` file in your application if you support Android devices with API level 18 or higher. You can skip this, if you are supporting lower API levels.

```xml
<uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="true" />
```

### Checking BLE support

Check BLE support if you are supporting devices with API level lower than 18.

```java
if (!reactiveBeacons.isBleSupported()) {
  // show message for the user that BLE is not supported on the device
}
```

If BLE is not supported, Observable emitting Beacons will be always empty.

### Requesting Bluetooth access

Use `requestBluetoothAccess(activity)` method to ensure that Bluetooth is enabled.
If you are supporting devices with API level lower than 18, you don't have to request Bluetooth access every time.

```java
if (!reactiveBeacons.isBluetoothEnabled()) {
  reactiveBeacons.requestBluetoothAccess(activity);
}
```

### Requesting Location access

Since API 23 (Android 6 - Marshmallow), Bluetooth Low Energy scan, requires `ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION` permissions.
Moreover, we need to enable Location services in order to scan BLE beacons. You don't have to worry about that if your apps are targeted to lower APIs than 23.
Nevertheless, you have to be aware of that, if you want to detect beacons on the newest versions of Android. Read more at: https://code.google.com/p/android/issues/detail?id=190372. Use `requestLocationAccess(activity)` method to ensure that Location services are enabled. If you are supporting devices with API level lower than 18, you don't have to request Location access every time.

```java
if (!reactiveBeacons.isLocationEnabled(activity)) {
  reactiveBeacons.requestLocationAccess(activity);
}
```

### Requesting Runtime Permissions

Since Android M (API 23), we need to request Runtime Permissions.
If we want to scan for BLE beacons, we need to request for `ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION` permission.
For more details, check sample `app`.

### Exemplary code snippet

With API methods, we can create the following code snippet:

```java
private boolean canObserveBeacons() {
  if (!reactiveBeacons.isBleSupported()) {
    Toast.makeText(this, "BLE is not supported on this device", Toast.LENGTH_SHORT).show();
    return false;
  }

  if (!reactiveBeacons.isBluetoothEnabled()) {
    reactiveBeacons.requestBluetoothAccess(this);
    return false;
  } else if (!reactiveBeacons.isLocationEnabled(this)) {
    reactiveBeacons.requestLocationAccess(this);
    return false;
  } else if (!isFineOrCoarseLocationPermissionGranted()
             && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    requestCoarseLocationPermission();
    return false;
   }

  return true;
}
```

You can adjust this snippet to your needs or handle this logic in your own way.

After that, we can perform the following operation:

```java
if(canObserveBeacons()) {
  // observe beacons here
}
```

Examples
--------

Exemplary application is located in `app` directory of this repository.

If you want to know, how to use this library with Kotlin, check `app-kotlin` sample.

Compatibility with different Android versions
---------------------------------------------

BLE scanning is available from Android 4.3 `JELLY_BEAN_MR2` (API 18).
You can use this library on lower versions of Android, but you won't be able to scan BLE devices,
you should handle that situation in your app and notify user about that. See [Good practices](#good-practices) section.
Since Android 5.0 `LOLLIPOP` (API 21), we have different API for BLE scanning.
That's why this library has two different BLE scanning strategies:
- `PreLollipopScanStrategy` used for pre-Lollipop devices (from API 18 to 20)
- `LollipopScanStrategy` used for Lollipop devices (API 21 or higher)

Library automatically chooses proper strategy with `isAtLeastAndroidLollipop()` method,
which checks version of the system installed on a device and uses selected strategy in `Observable<Beacon> observe()` method from the library.
Moreover, you can force using one of the existing strategies or your own custom scanning strategy
with the following method available in the library:

```java
Observable<Beacon> observe(ScanStrategy scanStrategy)
```

`ScanStrategy` is an interface with the following method:

```java
Observable<Beacon> observe();
```

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
    .subscribe(new Consumer<Beacon>() {
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
    <artifactId>reactivebeacons-rx2</artifactId>
    <version>0.6.0</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  compile 'com.github.pwittchen:reactivebeacons-rx2:0.6.0'
}
```

Tests
-----

Tests are available in `library/src/test/java/` directory and can be executed without emulator or Android device from CLI with the following command:

```
./gradlew test
```

Code style
----------

Code style used in the project is called `SquareAndroid` from Java Code Styles repository by Square available at: https://github.com/square/java-code-styles. Currently, library doesn't have checkstyle verification attached. It can be done in the future.

Static code analysis
--------------------

Static code analysis runs Checkstyle, FindBugs, PMD and Lint. It can be executed with command:

 ```
 ./gradlew check
 ```

Reports from analysis are generated in `library/build/reports/` directory.

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
- [Bluetooth Low Energy on Android Developer Guide](http://developer.android.com/guide/topics/connectivity/bluetooth-le.html)

### Producers of BLE beacons

- [Estimote](http://estimote.com)
- [Kontakt.io](http://kontakt.io)

### Other APIs and libraries
- Beacons/BLE
  - [Android SDK by Estimote](https://github.com/Estimote/Android-SDK)
  - [Android SDK by Kontakt.io](https://github.com/kontaktio/Android-SDK)
  - [Android Beacon Library by AltBeacon](https://github.com/AltBeacon/android-beacon-library)
  - [Bluetooth LE Library - Android](https://github.com/alt236/Bluetooth-LE-Library---Android)
  - [EasyBle](https://github.com/Ficat/EasyBle)
  - [RxAndroidBle](https://github.com/Polidea/RxAndroidBle)
  - [RxCentralBle](https://github.com/uber/RxCentralBle)
- Bluetooth
  - [RxBluetooth](https://github.com/IvBaranov/RxBluetooth)
  - [LMBluetoothSDK](https://github.com/whilu/LMBluetoothSdk)
  - [Android-Bluetooth-Library](https://github.com/arissa34/Android-Bluetooth-Library)
  - [AndroidSmoothBluetooth](https://github.com/palaima/AndroidSmoothBluetooth)
  - [BlueDuff](https://github.com/Marchuck/BlueDuff)

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


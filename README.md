# ReactiveBeacons
Android library scanning BLE beacons nearby with RxJava Observables

This library has limited functionality, but its API is very simple and has just three methods including constructor:

```java
ReactiveBeacons(context)
void requestBluetoothAccessIfDisabled(activity)
Observable<Beacon> observe()
```

Contents
--------
- [Usage](#usage)
- [Example](#example)
- [Download](#download)
- [Code style](#code-style)
- [References](#references)
- [License](#license)

Usage
-----

Initialize `ReactiveBeacons` object:

```java
private ReactiveBeacons reactiveBeacons;

@Override protected void onCreate(Bundle savedInstanceState) {
  reactiveBeacons = new ReactiveBeacons(this);
}
```

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

Download
--------

Download section will be updated in the future.

Code style
----------

Code style used in the project is called `SquareAndroid` from Java Code Styles repository by Square available at: https://github.com/square/java-code-styles. Currently, library doesn't have checkstyle verification attached. It can be done in the future.

References
----------

### Useful resources

- [Bluetooth Low Energy on Wikipedia](https://en.wikipedia.org/wiki/Bluetooth_low_energy)
- [android-bluetooth-demo repository](https://github.com/Pixplicity/android-bluetooth-demo)
- [Converting callbacks to RxJava Observables](https://getpocket.com/a/read/1052659262)

### Producers of BLE beacons

- [Estimote](http://estimote.com)
- [Kontakt.io](http://kontakt.io)

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


CHANGELOG
=========

v. 0.3.1
--------
*09 Nov 2015*

- improved filters in PR #24
- fixed RxJava usage in sample app
- fixed RxJava usage in code snippets in `README.md`
- added static code analysis
- added sample app in Kotlin
- added sample app with Android Service

v. 0.3.0
--------
*25 Oct 2015*

- replaced `distinct()` operator with `distinctUntilChanged()` operator in `Observable<Beacon> observe()` method in `ReactiveBeacons` class.
- added permissions `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` to satisfy requirements of Android 6
- renamed `void requestBluetoothAccessIfDisabled(activity)` method to `void requestBluetoothAccess(activity)`
- added `boolean isBluetoothEnabled()` method
- added `boolean isLocationEnabled(context)` method
- added `void requestLocationAccess(activity)` method
- modified sample app in order to make it work on Android 6 Marshmallow
- reduced target API from 23 to 22 in library due to problems with additional permissions and new permission model (it can be subject of improvements in the next releases)
- added package private `AccessRequester` class

v. 0.2.0
--------
*11 Oct 2015*

- decreased min SDK version to 9
- added `isBleSupported()` method to the public API
- if BLE is not supported by the device, library emits an empty Observable
- updated exemplary app
- updated documentation in `README.md` file

v. 0.1.0
--------
*05 Oct 2015*

- added `Filter` class providing methods, which can be used with `filter(...)` method from RxJava inside specific subscription. These methods can be used for filtering stream of Beacons by Proximity, distance, device names and MAC addresses.
- added missing `reactivebeacons` package to library module


v. 0.0.1
--------
*29 Sep 2015*

First release of the library.

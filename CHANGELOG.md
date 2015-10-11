CHANGELOG
=========

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
